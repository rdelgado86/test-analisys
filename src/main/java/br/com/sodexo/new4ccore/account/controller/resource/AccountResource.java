package br.com.sodexo.new4ccore.account.controller.resource;

import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.UpdateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.GeneralResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.GetAccountByCCPResponseDTO;
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

@Tag(name="accounts")
@RequestMapping(RouteEnum.ACCOUNTS_API_BASE_PATH)
public interface AccountResource {

    @Operation(summary = "Retrieves Account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @GetMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getAccountById(@PathVariable @Positive(message = "{accountId.positive}") Long accountId, @RequestParam(name = "product_id" ,required = false) Long productId) throws AccountException;

    @Operation(summary = "Retrieves Account information by Consumer ID, Customer ID and Product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @GetMapping(value = "/by-ccp/{consumerId}/{customerId}/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetAccountByCCPResponseDTO> getAccountByCCP(
            @PathVariable @Positive(message = "{consumerId.positive}") Long consumerId,
            @PathVariable @Positive(message = "{customerId.positive}") Long customerId,
            @PathVariable @Positive(message = "{productId.positive}") Long productId) throws AccountException;

    @Operation(summary = "Updates Account Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GetAccountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @PutMapping(value = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    ResponseEntity<?> updateAccount(@PathVariable Long accountId, @RequestBody @Valid UpdateAccountRequestDTO request, UriComponentsBuilder uriBuilder) throws AccountException;

    @Operation(summary = "Retrieves Account information by Customer ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationDTO.class)))})
    @GetMapping(value = "/by-customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<PaginationDTO<GetAccountResponseDTO>> getAccountsByCustomerId(@PathVariable @Positive(message = "{customerId.positive}") Long customerId,
                                                                                 @RequestParam(defaultValue = "20", name = "size") Integer size,
                                                                                 @RequestParam(defaultValue = "0", name = "page") Integer page);

    @Operation(summary = "Block Account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @PostMapping(value = "/{accountId}/block", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GeneralResponseDTO> blockAccount(@PathVariable @Positive(message = "{accountId.positive}") Long accountId) throws AccountException;

    @Operation(summary = "Unblock Account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))})
    @PostMapping(value = "/{accountId}/unblock", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GeneralResponseDTO> unblockAccount(@PathVariable @Positive(message = "{accountId.positive}") Long accountId) throws AccountException;


}
