
package com.seos.pmis.operation.repository;

import com.seos.pmis.operation.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationRepository
        extends JpaRepository<Operation, Long> {

    boolean existsByOperationCode(String operationCode);

    Optional<Operation> findByOperationCode(String operationCode);
}
