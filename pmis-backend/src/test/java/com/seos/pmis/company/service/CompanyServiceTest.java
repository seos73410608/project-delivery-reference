package com.seos.pmis.company.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.company.dto.request.CompanySearchRequest;
import com.seos.pmis.company.dto.request.CreateCompanyRequest;
import com.seos.pmis.company.dto.request.UpdateCompanyRequest;
import com.seos.pmis.company.dto.response.CompanyResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.mapper.CompanyMapper;
import com.seos.pmis.company.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    private Company company;

    private CompanyResponse companyResponse;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .legalName("테스트 주식회사")
                .businessRegistrationNumber("123-45-67890")
                .countryCode("KOR")
                .website("https://example.com")
                .status(CompanyStatus.ACTIVE)
                .description("테스트 회사 설명")
                .build();

        companyResponse = CompanyResponse.builder()
                .id(1L)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .legalName("테스트 주식회사")
                .businessRegistrationNumber("123-45-67890")
                .countryCode("KOR")
                .website("https://example.com")
                .status(CompanyStatus.ACTIVE)
                .description("테스트 회사 설명")
                .build();
    }

    @Test
    void create_회사를_정상적으로_등록한다() {
        // given
        CreateCompanyRequest request = mock(CreateCompanyRequest.class);

        when(request.getCompanyCode()).thenReturn("TEST-COMPANY");
        when(request.getBusinessRegistrationNumber())
                .thenReturn("123-45-67890");
        when(companyRepository.existsByCompanyCode("TEST-COMPANY"))
                .thenReturn(false);
        when(companyRepository.existsByBusinessRegistrationNumber(
                "123-45-67890")).thenReturn(false);
        when(companyMapper.toEntity(request)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toResponse(company)).thenReturn(companyResponse);

        // when
        CompanyResponse response = companyService.create(request);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCompanyCode()).isEqualTo("TEST-COMPANY");
        assertThat(response.getCompanyName()).isEqualTo("테스트 회사");

        assertThat(company.getStatus()).isEqualTo(CompanyStatus.ACTIVE);

        verify(companyRepository).existsByCompanyCode("TEST-COMPANY");
        verify(companyRepository)
                .existsByBusinessRegistrationNumber("123-45-67890");
        verify(companyRepository).save(company);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void create_회사코드가_중복되면_예외가_발생한다() {
        // given
        CreateCompanyRequest request = mock(CreateCompanyRequest.class);

        when(request.getCompanyCode()).thenReturn("TEST-COMPANY");
        when(companyRepository.existsByCompanyCode("TEST-COMPANY"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> companyService.create(request))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository).existsByCompanyCode("TEST-COMPANY");
        verify(companyRepository, never()).save(any());
        verifyNoInteractions(companyMapper);
    }

    @Test
    void create_사업자등록번호가_중복되면_예외가_발생한다() {
        // given
        CreateCompanyRequest request = mock(CreateCompanyRequest.class);

        when(request.getCompanyCode()).thenReturn("NEW-COMPANY");
        when(request.getBusinessRegistrationNumber())
                .thenReturn("123-45-67890");

        when(companyRepository.existsByCompanyCode("NEW-COMPANY"))
                .thenReturn(false);
        when(companyRepository.existsByBusinessRegistrationNumber(
                "123-45-67890")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> companyService.create(request))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository).existsByCompanyCode("NEW-COMPANY");
        verify(companyRepository)
                .existsByBusinessRegistrationNumber("123-45-67890");
        verify(companyRepository, never()).save(any());
        verifyNoInteractions(companyMapper);
    }

    @Test
    void get_회사_상세정보를_조회한다() {
        // given
        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        // when
        CompanyResponse response = companyService.get(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCompanyCode()).isEqualTo("TEST-COMPANY");
        assertThat(response.getCompanyName()).isEqualTo("테스트 회사");

        verify(companyRepository).findById(1L);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void get_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> companyService.get(999L))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository).findById(999L);
        verifyNoInteractions(companyMapper);
    }

    @Test
    void search_검색조건에_맞는_회사목록을_조회한다() {
        // given
        CompanySearchRequest request = new CompanySearchRequest();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Company> companyPage = new PageImpl<>(
                List.of(company), pageable, 1);

        when(companyRepository.findAll(
                any(Specification.class), any(Pageable.class)))
                .thenReturn(companyPage);

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        // when
        Page<CompanyResponse> response =
                companyService.search(request, pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getCompanyCode())
                .isEqualTo("TEST-COMPANY");

        verify(companyRepository).findAll(
                any(Specification.class), eq(pageable));
        verify(companyMapper).toResponse(company);
    }

    @Test
    void update_회사정보를_정상적으로_수정한다() {
        // given
        UpdateCompanyRequest request = mock(UpdateCompanyRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(request.getCompanyName()).thenReturn("변경된 회사명");
        when(request.getLegalName()).thenReturn("변경된 법인명");
        when(request.getBusinessRegistrationNumber())
                .thenReturn("987-65-43210");
        when(request.getCountryCode()).thenReturn("USA");
        when(request.getWebsite()).thenReturn("https://updated.example.com");
        when(request.getDescription()).thenReturn("변경된 설명");

        when(companyRepository.existsByBusinessRegistrationNumber(
                "987-65-43210")).thenReturn(false);

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        // when
        companyService.update(1L, request);

        // then
        assertThat(company.getCompanyName()).isEqualTo("변경된 회사명");
        assertThat(company.getLegalName()).isEqualTo("변경된 법인명");
        assertThat(company.getBusinessRegistrationNumber())
                .isEqualTo("987-65-43210");
        assertThat(company.getCountryCode()).isEqualTo("USA");
        assertThat(company.getWebsite())
                .isEqualTo("https://updated.example.com");
        assertThat(company.getDescription()).isEqualTo("변경된 설명");

        verify(companyRepository).findById(1L);
        verify(companyRepository)
                .existsByBusinessRegistrationNumber("987-65-43210");
        verify(companyMapper).toResponse(company);
    }

    @Test
    void update_기존_사업자등록번호를_유지하면_중복검사를_생략한다() {
        // given
        UpdateCompanyRequest request = mock(UpdateCompanyRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(request.getCompanyName()).thenReturn("변경된 회사명");
        when(request.getLegalName()).thenReturn("테스트 주식회사");
        when(request.getBusinessRegistrationNumber())
                .thenReturn("123-45-67890");
        when(request.getCountryCode()).thenReturn("KOR");
        when(request.getWebsite()).thenReturn("https://example.com");
        when(request.getDescription()).thenReturn("변경된 설명");

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        // when
        companyService.update(1L, request);

        // then
        verify(companyRepository, never())
                .existsByBusinessRegistrationNumber(any());
        assertThat(company.getCompanyName()).isEqualTo("변경된 회사명");
    }

    @Test
    void update_다른회사의_사업자등록번호와_중복되면_예외가_발생한다() {
        // given
        UpdateCompanyRequest request = mock(UpdateCompanyRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(request.getBusinessRegistrationNumber())
                .thenReturn("987-65-43210");

        when(companyRepository.existsByBusinessRegistrationNumber(
                "987-65-43210")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> companyService.update(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository)
                .existsByBusinessRegistrationNumber("987-65-43210");
        verify(companyMapper, never()).toResponse(any());
    }

    @Test
    void update_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        UpdateCompanyRequest request = mock(UpdateCompanyRequest.class);

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> companyService.update(999L, request))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository).findById(999L);
        verifyNoInteractions(companyMapper);
    }

    @Test
    void changeStatus_회사상태를_비활성으로_변경한다() {
        // given
        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyMapper.toResponse(company))
                .thenReturn(companyResponse);

        // when
        companyService.changeStatus(1L, CompanyStatus.INACTIVE);

        // then
        assertThat(company.getStatus()).isEqualTo(CompanyStatus.INACTIVE);

        verify(companyRepository).findById(1L);
        verify(companyMapper).toResponse(company);
    }

    @Test
    void changeStatus_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyService.changeStatus(999L, CompanyStatus.INACTIVE))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository).findById(999L);
        verifyNoInteractions(companyMapper);
    }
}