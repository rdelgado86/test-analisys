package br.com.sodexo.new4ccore.account.repository;

import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor {

    Optional<AccountEntity> findById(Long id);
    Optional<AccountEntity> findByIdAndProductId(Long id, Long productId);

    Optional<AccountEntity> findByConsumerIdAndCustomerIdAndProductIdAndStatusNot(
            Long consumerId, Long customerId, Long productId, AccountStatusEnum status);

    Boolean existsByConsumerIdAndCustomerIdAndProductIdAndStatusNot(
            Long consumerId, Long customerId, Long productId, AccountStatusEnum status);

    Page<AccountEntity> findByConsumerId(Long consumerId, Pageable pageable);

    Page<AccountEntity> findByCustomerIdAndStatusNot(Long customerId, AccountStatusEnum status, Pageable pageable);

    @Query(value = "SELECT nextval('mbe.ACCOUNT_NUMBER_SEQ')", nativeQuery = true)
    Long getNextAccountNumber();
}
