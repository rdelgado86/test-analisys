package br.com.sodexo.new4ccore.account.service.baas;

import br.com.sodexo.mbpcore.dock.client.AbstractRestClient;
import br.com.sodexo.mbpcore.dock.client.account.BlockAccountClient;
import br.com.sodexo.mbpcore.dock.client.account.CancelAccountClient;
import br.com.sodexo.mbpcore.dock.client.account.CreateAccountClient;
import br.com.sodexo.mbpcore.dock.client.account.ReactivateAccountClient;
import br.com.sodexo.mbpcore.dock.client.address.FindAddressClient;
import br.com.sodexo.mbpcore.dock.dto.request.CreateAccountDockRequestDTO;
import br.com.sodexo.mbpcore.dock.dto.response.BlockAccountDockResponseDTO;
import br.com.sodexo.mbpcore.dock.dto.response.CancelAccountDockResponseDTO;
import br.com.sodexo.mbpcore.dock.dto.response.CreateAccountDockResponseDTO;
import br.com.sodexo.mbpcore.dock.dto.response.ReactivateAccountDockResponseDTO;
import br.com.sodexo.mbpcore.dock.repository.DockUsageRepository;
import br.com.sodexo.mbpcore.dock.utils.CredentialsUtils;
import br.com.sodexo.mbpcore.dock.utils.PropertiesUtils;
import br.com.sodexo.mbpcore.dock.utils.RestUtils;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BaasIntegrationImplTest {

    private BaasIntegrationImpl subject;
    private ModelMapper modelMapper;

    @Mock
    private DockUsageRepository dockUsageRepository;

    @Mock
    private CredentialsUtils credentialsUtils;

    @Mock
    private AbstractRestClient abstractRestClient;

    @Mock
    private FindAddressClient findAddressClient;

    @InjectMocks
    private CreateAccountClient createAccountClient;

    @InjectMocks
    private BlockAccountClient blockAccountClient;

    @InjectMocks
    private CancelAccountClient cancelAccountClient;

    @InjectMocks
    private ReactivateAccountClient reactivateAccountClient;

    // MockedStatic<PropertiesUtils> _properties = mockStatic(PropertiesUtils.class);
    // try {


    //     assertEquals(expected, PropertiesUtils.getDockEndpointForKey(key)); 
    // } finally {
    //     _properties.close();
    // }

    @BeforeAll
    static void setUpProperties() {
        var _properties = mockStatic(PropertiesUtils.class);
        _properties.when(() -> PropertiesUtils.getEnviromentVariable(PropertiesUtils.AUTH_VARIABLE)).thenReturn("https://auth.foo.io");
        _properties.when(() -> PropertiesUtils.getEnviromentVariable(PropertiesUtils.API_VARIABLE)).thenReturn("https://api.foo.io");
        _properties.when(() -> PropertiesUtils.getDockEndpointForKey(anyString())).thenCallRealMethod();
        _properties.when(() -> PropertiesUtils.get(anyString())).thenCallRealMethod();
    }

    @BeforeEach
    void setUp() {
        modelMapper = mock(ModelMapper.class);
        subject = new BaasIntegrationImpl(modelMapper, createAccountClient, blockAccountClient, cancelAccountClient, reactivateAccountClient);
    }

    @Test
    void givenValidRequest_whenCreateAccount_thenShouldReturnCreatedAccountObject() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn(getJsonBodyCreateDockRequest());

            when(RestUtils.post(anyString(), anyString(), anyString())).thenReturn(response);

            when(modelMapper.map(any(), any())).thenReturn(CreateAccountDockResponseDTO.builder().build());

            var responseReturn = subject.createAccount(CreateAccountRequestDTO.builder()
                            .customerId(1L)
                            .productId(1L)
                            .build(),
                    1L);

            assertNotNull(responseReturn);
        }
    }

    @Test
    void givenErrorWhenSendingDockRequest_whenCreateAccount_thenShouldReturnAccountException() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
            when(response.body()).thenReturn("{\"status\":\"404\",\"message\":\"error on dock\"}");

            when(RestUtils.post(anyString(), anyString(), anyString())).thenReturn(response);

            var e = assertThrows(AccountException.class, () -> subject.createAccount(CreateAccountRequestDTO.builder()
                            .build(),
                    1L));

            assertEquals("error on dock", e.getResponse().getMessage());
        }
    }

    @Test
    void givenExceptionWhenSendingDockRequest_whenCreateAccount_thenShouldReturnAccountException() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn("Alfa");

            var e = assertThrows(AccountException.class, () -> subject.createAccount(CreateAccountRequestDTO.builder()
                            .build(),
                    1L));

            assertEquals("Cannot invoke \"java.net.http.HttpResponse.statusCode()\" because \"response\" is null", e.getResponse().getMessage());
        }
    }

    @Test
    void givenIllegalArgument_whenCreateAccount_thenShouldThrowAccountException() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn(getJsonBodyCreateDockRequest());

            when(RestUtils.post(anyString(), anyString(), anyString())).thenReturn(response);

            when(modelMapper.map(any(), any()))
                    .thenThrow(IllegalArgumentException.class);

            assertThrows(AccountException.class, () -> subject.createAccount(CreateAccountRequestDTO.builder().build(), 1L));
        }
    }

    @Test
    void givenBlockStatus_whenUpdateAccount_thenShouldReturnBlockAccountObject() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn(getJsonBodyUpdateDockRequest());

            when(RestUtils.post(anyString(), anyString())).thenReturn(response);

            var responseUpdateAcc = subject.updateAccount(1L, AccountStatusEnum.BLOCKED);
            assertNotNull(responseUpdateAcc);

        }
    }

    @Test
    void givenCancelledStatus_whenUpdateAccount_thenShouldReturnCancelledAccountObject() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn(getJsonBodyCancelDockRequest());

            when(RestUtils.post(anyString(), anyString())).thenReturn(response);


            var responseCancel = subject.updateAccount(1L, AccountStatusEnum.CANCELLED);
            assertNotNull(responseCancel);
        }
    }

    @Test
    void givenActiveStatus_whenUpdateAccount_thenShouldReturnActiveAccountObject() throws Exception {
        try (var _1 = mockStatic(RestUtils.class)) {

            when(dockUsageRepository.save(any())).thenReturn(any());

            when(credentialsUtils.getAuthToken(abstractRestClient)).thenReturn("authTokenTest");

            var response = mock(HttpResponse.class);
            when(response.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(response.body()).thenReturn(getJsonBodyReactiveDockRequest());

            when(RestUtils.post(anyString(), anyString())).thenReturn(response);

            var responseActive = subject.updateAccount(1L, AccountStatusEnum.ACTIVE);
            assertNotNull(responseActive);
        }
    }

    private String getJsonBodyReactiveDockRequest() {
        return new Gson().toJson(ReactivateAccountDockResponseDTO.builder()
                .id(1L)
                .idProduto(3)
                .idOrigemComercial(4)
                .idPessoa(1)
                .diaVencimento(4)
                .melhorDiaCompra(5)
                .valorRenda(4)
                .idProposta(1)
                .funcaoAtiva("Ativo")
                .possuiOverLimit(true)
                .build());
    }

    private String getJsonBodyCancelDockRequest() {
        return new Gson().toJson(CancelAccountDockResponseDTO.builder()
                .id(1L)
                .idProduto(3)
                .idOrigemComercial(4)
                .idPessoa(1)
                .diaVencimento(4)
                .melhorDiaCompra(5)
                .valorRenda(4)
                .idProposta(1)
                .funcaoAtiva("Ativo")
                .possuiOverLimit(true)
                .build());
    }

    private String getJsonBodyCreateDockRequest() {
        return new Gson().toJson(CreateAccountDockRequestDTO.builder()
                .idPessoa(2L)
                .idOrigemComercial(4)
                .idProduto(3)
                .diaVencimento(4)
                .valorRenda(4)
                .valorPontuacao(5)
                .idEnderecoCorrespondencia(1211)
                .build());
    }

    private String getJsonBodyUpdateDockRequest() {
        return new Gson().toJson(BlockAccountDockResponseDTO.builder()
                .id(1L)
                .idProduto(3)
                .idOrigemComercial(4)
                .idPessoa(1)
                .diaVencimento(4)
                .melhorDiaCompra(5)
                .valorRenda(4)
                .idProposta(1)
                .funcaoAtiva("Ativo")
                .possuiOverLimit(true)
                .build());
    }

}