package com.seos.pmis.company.mapper;

import com.seos.pmis.company.dto.request.CreateCompanyRequest;
import com.seos.pmis.company.dto.response.CompanyResponse;
import com.seos.pmis.company.entity.Company;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyMapper {

    /**
     * CreateCompanyRequest → Company Entity
     */
    public Company toEntity(CreateCompanyRequest request) {

        return Company.builder()
                .companyCode(request.getCompanyCode())
                .companyName(request.getCompanyName())
                .legalName(request.getLegalName())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .countryCode(request.getCountryCode())
                .website(request.getWebsite())
                .description(request.getDescription())
                .build();
    }

    /**
     * Company Entity → CompanyResponse
     */
    public CompanyResponse toResponse(Company company) {

        return CompanyResponse.builder()
                .id(company.getId())
                .companyCode(company.getCompanyCode())
                .companyName(company.getCompanyName())
                .legalName(company.getLegalName())
                .businessRegistrationNumber(company.getBusinessRegistrationNumber())
                .countryCode(company.getCountryCode())
                .website(company.getWebsite())
                .status(company.getStatus())
                .description(company.getDescription())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}