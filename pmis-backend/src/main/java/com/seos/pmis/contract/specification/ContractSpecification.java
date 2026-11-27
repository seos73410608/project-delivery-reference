package com.seos.pmis.contract.specification;

import com.seos.pmis.contract.dto.request.ContractSearchRequest;
import com.seos.pmis.contract.entity.Contract;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ContractSpecification {

    private ContractSpecification() {
    }

    public static Specification<Contract> search(
            ContractSearchRequest request
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return criteriaBuilder.conjunction();
            }

            if (StringUtils.hasText(request.getContractCode())) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("contractCode")
                                ),
                                "%" + request.getContractCode()
                                        .trim().toLowerCase() + "%"
                        )
                );
            }

            if (StringUtils.hasText(request.getContractName())) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("contractName")
                                ),
                                "%" + request.getContractName()
                                        .trim().toLowerCase() + "%"
                        )
                );
            }

            if (request.getContractType() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("contractType"),
                                request.getContractType()
                        )
                );
            }

            if (request.getStatus() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                request.getStatus()
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}