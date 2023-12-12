package br.com.sodexo.new4ccore.account.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.sodexo.new4ccore.account.interceptor.LoggerInterceptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDTO implements Serializable {

	private static final long serialVersionUID = -6525795135018774281L;

	@JsonProperty("uuid")
    private String uuid;

    @JsonProperty("code")
    private String code;
    
    @JsonProperty("summary")
    private String summary;

    @JsonProperty("field")
    private String field;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("details")
    private String details;

    public ErrorResponseDTO(String message){
        this.uuid = LoggerInterceptor.REQUEST_UUID;
        this.message = message;
    }
    
	public ErrorResponseDTO(String field, String message) {
        this.uuid = LoggerInterceptor.REQUEST_UUID;
		this.field = field;
		this.message = message;
	}
    
}
