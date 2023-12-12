package br.com.sodexo.new4ccore.account.controller.resource;

import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.GetAccountResponseDTO;
import br.com.sodexo.new4ccore.account.enums.RouteEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Tag(name = "accounts")
@RequestMapping(RouteEnum.CONSUMERS_API_BASE_PATH)
public interface ConsumerResource {

    @Operation(summary = "Retrieves a list of accounts for the consumer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @GetMapping(value = "/{consumerId}/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccountList(@PathVariable @Positive(message = "{consumerId.positive}") Long consumerId,
                                            @RequestParam(defaultValue ="" ,name = "status") String status,
                                            @RequestParam(name = "product_id",required = false) Long productId,
                                            @RequestParam(defaultValue = "20", name = "size") Integer size,
                                            @RequestParam(defaultValue = "0", name = "page") Integer page) throws AccountException;

    @Operation(summary = "Create account for consumer and customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Account already exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @PostMapping(value = "/{consumerId}/accounts", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> createAccount(@PathVariable Long consumerId, @RequestBody @Valid CreateAccountRequestDTO request, UriComponentsBuilder uriBuilder) throws AccountException, Exception;

}
