package br.com.sodexo.new4ccore.account.configuration;

import java.util.Map;
import java.util.Objects;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestContextCloningDecorator implements TaskDecorator {

	@Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes requestContext = this.cloneRequestAttributes(RequestContextHolder.currentRequestAttributes());
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(contextMap);
                RequestContextHolder.setRequestAttributes(requestContext);
                runnable.run();
            } finally {
                MDC.clear();
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }

    private RequestAttributes cloneRequestAttributes(RequestAttributes requestAttributes) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;

        try {
            RequestAttributes clonedRequestAttribute = new ServletRequestAttributes(servletRequestAttributes.getRequest(), servletRequestAttributes.getResponse());
            String[] scopeAttributes = requestAttributes.getAttributeNames(RequestAttributes.SCOPE_REQUEST);
            if (scopeAttributes.length > 0) {
                for (String attrName : scopeAttributes)
                    clonedRequestAttribute.setAttribute(attrName,
                            Objects.requireNonNull(requestAttributes.getAttribute(attrName, RequestAttributes.SCOPE_REQUEST)),
                            RequestAttributes.SCOPE_REQUEST);
            }
            return clonedRequestAttribute;
        } catch (Exception e) {
            return requestAttributes;
        }
    }
}
