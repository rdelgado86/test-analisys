package br.com.sodexo.new4ccore.account.entity;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Builder
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    private Long accountNumber;

    @NonNull
    private Long productId;

    @NonNull
    private Long consumerId;

    @NonNull
    private Long customerId;

    private Long externalAccountNumber;

    @NonNull
    private AccountStatusEnum status;

    private LocalDate creationDate;

    private LocalDate closureDate;

    private LocalDateTime statusDate;

	@Column(name = "dxc_beneficiary_id")
	private Long beneficiaryId;

	private LocalDateTime lastModified;

    private Boolean syncPending;

    @PrePersist
    @PreUpdate
    void preSave() {
        //This value is going to be set to false only in the process that synchronizes the databases
        this.syncPending = true;
        this.lastModified = LocalDateTime.now();
    }

}

