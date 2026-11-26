package com.seos.pmis.company.specification;

import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class CompanySpecification {

    private CompanySpecification() {
    }

    /**
     * 회사 코드 부분 검색 (대소문자 구분 없음)
     */
    public static Specification<Company> companyCodeContains(String companyCode) {

        return (root, query, cb) -> {

            if (companyCode == null || companyCode.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("companyCode")),
                    "%" + companyCode.trim().toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    /**
     * 회사명 부분 검색 (대소문자 구분 없음)
     */
    public static Specification<Company> companyNameContains(String companyName) {

        return (root, query, cb) -> {

            if (companyName == null || companyName.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("companyName")),
                    "%" + companyName.trim().toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    /**
     * 사업자등록번호 부분 검색
     */
    public static Specification<Company> businessRegistrationNumberContains(
            String businessRegistrationNumber
    ) {

        return (root, query, cb) -> {

            if (businessRegistrationNumber == null
                    || businessRegistrationNumber.isBlank()) {
                return null;
            }

            return cb.like(
                    root.get("businessRegistrationNumber"),
                    "%" + businessRegistrationNumber.trim() + "%"
            );
        };
    }

    /**
     * 국가 코드 정확히 일치 (ISO 3166-1 alpha-3)
     */
    public static Specification<Company> hasCountryCode(String countryCode) {

        return (root, query, cb) -> {

            if (countryCode == null || countryCode.isBlank()) {
                return null;
            }

            return cb.equal(
                    root.get("countryCode"),
                    countryCode.trim().toUpperCase(Locale.ROOT)
            );
        };
    }

    /**
     * 회사 상태
     */
    public static Specification<Company> hasStatus(CompanyStatus status) {

        return (root, query, cb) -> {

            if (status == null) {
                return null;
            }

            return cb.equal(root.get("status"), status);
        };
    }
}