package com.seos.pmis.company.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.company.dto.request.CloseCompanyRoleAssignmentRequest;
import com.seos.pmis.company.dto.request.CreateCompanyRoleAssignmentRequest;
import com.seos.pmis.company.dto.response.CompanyRoleAssignmentResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyRole;
import com.seos.pmis.company.entity.CompanyRoleAssignment;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.CompanyRoleAssignmentRepository;
import com.seos.pmis.company.repository.CompanyRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyRoleAssignmentServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyRoleRepository companyRoleRepository;

    @Mock
    private CompanyRoleAssignmentRepository companyRoleAssignmentRepository;

    @InjectMocks
    private CompanyRoleAssignmentService companyRoleAssignmentService;

    private Company company;
    private CompanyRole companyRole;
    private CompanyRoleAssignment assignment;

    private final LocalDate effectiveFrom = LocalDate.of(2026, 1, 1);

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .countryCode("KOR")
                .status(CompanyStatus.ACTIVE)
                .build();

        companyRole = CompanyRole.builder()
                .id(1L)
                .roleCode("CUSTOMER")
                .roleName("고객사")
                .description("프로젝트 또는 서비스를 발주하는 고객사")
                .build();

        assignment = CompanyRoleAssignment.builder()
                .id(1L)
                .company(company)
                .companyRole(companyRole)
                .effectiveFrom(effectiveFrom)
                .description("프로젝트 고객사")
                .build();
    }

    @Test
    void getAssignments_회사역할_전체이력을_조회한다() {
        // given
        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleAssignmentRepository
                .findByCompanyIdOrderByEffectiveFromDescIdDesc(1L))
                .thenReturn(List.of(assignment));

        // when
        List<CompanyRoleAssignmentResponse> response =
                companyRoleAssignmentService.getAssignments(1L);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(1L);
        assertThat(response.get(0).getRoleCode()).isEqualTo("CUSTOMER");
        assertThat(response.get(0).getRoleName()).isEqualTo("고객사");

        verify(companyRepository).findById(1L);
        verify(companyRoleAssignmentRepository)
                .findByCompanyIdOrderByEffectiveFromDescIdDesc(1L);
    }

    @Test
    void getAssignments_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.getAssignments(999L))
                .isInstanceOf(BusinessException.class);

        verify(companyRoleAssignmentRepository, never())
                .findByCompanyIdOrderByEffectiveFromDescIdDesc(any());
    }

    @Test
    void assign_회사역할을_정상적으로_부여한다() {
        // given
        CreateCompanyRoleAssignmentRequest request =
                mock(CreateCompanyRoleAssignmentRequest.class);

        when(request.getRoleCode()).thenReturn("CUSTOMER");
        when(request.getEffectiveFrom()).thenReturn(effectiveFrom);
        when(request.getDescription()).thenReturn("프로젝트 고객사");

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleRepository.findByRoleCode("CUSTOMER"))
                .thenReturn(Optional.of(companyRole));

        when(companyRoleAssignmentRepository
                .existsByCompanyIdAndCompanyRoleIdAndEffectiveToIsNull(1L, 1L))
                .thenReturn(false);

        when(companyRoleAssignmentRepository.save(any(CompanyRoleAssignment.class)))
                .thenReturn(assignment);

        // when
        CompanyRoleAssignmentResponse response =
                companyRoleAssignmentService.assign(1L, request);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCompanyId()).isEqualTo(1L);
        assertThat(response.getRoleCode()).isEqualTo("CUSTOMER");
        assertThat(response.getEffectiveFrom()).isEqualTo(effectiveFrom);

        verify(companyRoleAssignmentRepository)
                .save(any(CompanyRoleAssignment.class));
    }

    @Test
    void assign_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        CreateCompanyRoleAssignmentRequest request =
                mock(CreateCompanyRoleAssignmentRequest.class);

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.assign(999L, request))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(companyRoleRepository);
        verify(companyRoleAssignmentRepository, never()).save(any());
    }

    @Test
    void assign_비활성회사에는_역할을_부여할수없다() {
        // given
        Company inactiveCompany = Company.builder()
                .id(1L)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .countryCode("KOR")
                .status(CompanyStatus.INACTIVE)
                .build();

        CreateCompanyRoleAssignmentRequest request =
                mock(CreateCompanyRoleAssignmentRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(inactiveCompany));

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.assign(1L, request))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(companyRoleRepository);
        verify(companyRoleAssignmentRepository, never()).save(any());
    }

    @Test
    void assign_존재하지않는_역할코드이면_예외가_발생한다() {
        // given
        CreateCompanyRoleAssignmentRequest request =
                mock(CreateCompanyRoleAssignmentRequest.class);

        when(request.getRoleCode()).thenReturn("UNKNOWN");

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleRepository.findByRoleCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.assign(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(companyRoleAssignmentRepository, never()).save(any());
    }

    @Test
    void assign_동일한_미종료역할이_있으면_중복부여를_거부한다() {
        // given
        CreateCompanyRoleAssignmentRequest request =
                mock(CreateCompanyRoleAssignmentRequest.class);

        when(request.getRoleCode()).thenReturn("CUSTOMER");

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleRepository.findByRoleCode("CUSTOMER"))
                .thenReturn(Optional.of(companyRole));

        when(companyRoleAssignmentRepository
                .existsByCompanyIdAndCompanyRoleIdAndEffectiveToIsNull(1L, 1L))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.assign(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(companyRoleAssignmentRepository, never()).save(any());
    }

    @Test
    void close_회사역할을_정상적으로_종료한다() {
        // given
        LocalDate effectiveTo = LocalDate.of(2026, 12, 31);

        CloseCompanyRoleAssignmentRequest request =
                mock(CloseCompanyRoleAssignmentRequest.class);

        when(request.getEffectiveTo()).thenReturn(effectiveTo);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleAssignmentRepository.findById(1L))
                .thenReturn(Optional.of(assignment));

        // when
        CompanyRoleAssignmentResponse response =
                companyRoleAssignmentService.close(1L, 1L, request);

        // then
        assertThat(assignment.getEffectiveTo()).isEqualTo(effectiveTo);
        assertThat(response.getEffectiveTo()).isEqualTo(effectiveTo);

        verify(companyRoleAssignmentRepository).findById(1L);
    }

    @Test
    void close_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        CloseCompanyRoleAssignmentRequest request =
                mock(CloseCompanyRoleAssignmentRequest.class);

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.close(999L, 1L, request))
                .isInstanceOf(BusinessException.class);

        verify(companyRoleAssignmentRepository, never()).findById(any());
    }

    @Test
    void close_역할배정이_존재하지_않으면_예외가_발생한다() {
        // given
        CloseCompanyRoleAssignmentRequest request =
                mock(CloseCompanyRoleAssignmentRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleAssignmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.close(1L, 999L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void close_다른회사의_역할배정이면_예외가_발생한다() {
        // given
        Company anotherCompany = Company.builder()
                .id(2L)
                .companyCode("ANOTHER-COMPANY")
                .companyName("다른 회사")
                .countryCode("KOR")
                .status(CompanyStatus.ACTIVE)
                .build();

        CompanyRoleAssignment anotherAssignment =
                CompanyRoleAssignment.builder()
                        .id(1L)
                        .company(anotherCompany)
                        .companyRole(companyRole)
                        .effectiveFrom(effectiveFrom)
                        .build();

        CloseCompanyRoleAssignmentRequest request =
                mock(CloseCompanyRoleAssignmentRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleAssignmentRepository.findById(1L))
                .thenReturn(Optional.of(anotherAssignment));

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.close(1L, 1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void close_이미종료된_역할배정이면_예외가_발생한다() {
        // given
        CompanyRoleAssignment closedAssignment =
                CompanyRoleAssignment.builder()
                        .id(1L)
                        .company(company)
                        .companyRole(companyRole)
                        .effectiveFrom(effectiveFrom)
                        .effectiveTo(LocalDate.of(2026, 6, 30))
                        .build();

        CloseCompanyRoleAssignmentRequest request =
                mock(CloseCompanyRoleAssignmentRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleAssignmentRepository.findById(1L))
                .thenReturn(Optional.of(closedAssignment));

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.close(1L, 1L, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void close_시작일보다_빠른종료일이면_예외가_발생한다() {
        // given
        CloseCompanyRoleAssignmentRequest request =
                mock(CloseCompanyRoleAssignmentRequest.class);

        when(request.getEffectiveTo())
                .thenReturn(LocalDate.of(2025, 12, 31));

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        when(companyRoleAssignmentRepository.findById(1L))
                .thenReturn(Optional.of(assignment));

        // when & then
        assertThatThrownBy(() ->
                companyRoleAssignmentService.close(1L, 1L, request))
                .isInstanceOf(BusinessException.class);

        assertThat(assignment.getEffectiveTo()).isNull();
    }
}