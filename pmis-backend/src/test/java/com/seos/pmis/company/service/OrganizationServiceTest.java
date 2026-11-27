package com.seos.pmis.company.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.company.dto.request.CreateOrganizationRequest;
import com.seos.pmis.company.dto.request.UpdateOrganizationRequest;
import com.seos.pmis.company.dto.response.OrganizationResponse;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.entity.OrganizationType;
import com.seos.pmis.company.mapper.OrganizationMapper;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.OrganizationRepository;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    @InjectMocks
    private OrganizationService organizationService;

    private Company company;
    private Organization organization;
    private OrganizationResponse organizationResponse;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(1L)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .status(CompanyStatus.ACTIVE)
                .build();

        organization = Organization.builder()
                .id(10L)
                .company(company)
                .organizationCode("DEV")
                .organizationName("개발팀")
                .organizationType(OrganizationType.TEAM)
                .status(OrganizationStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        organizationResponse = OrganizationResponse.builder()
                .id(10L)
                .companyId(1L)
                .companyCode("TEST-COMPANY")
                .companyName("테스트 회사")
                .organizationCode("DEV")
                .organizationName("개발팀")
                .organizationType(OrganizationType.TEAM)
                .status(OrganizationStatus.ACTIVE)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();
    }

    @Test
    void create_조직을_정상적으로_등록한다() {
        // given
        CreateOrganizationRequest request = mock(CreateOrganizationRequest.class);

        when(request.getOrganizationCode()).thenReturn("DEV");
        when(request.getParentOrganizationId()).thenReturn(null);
        when(request.getManagerUserId()).thenReturn(null);
        when(request.getEffectiveFrom()).thenReturn(LocalDate.of(2026, 1, 1));

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
        when(organizationRepository.existsByCompanyIdAndOrganizationCode(1L, "DEV"))
                .thenReturn(false);
        when(organizationMapper.toEntity(request, company, null, null))
                .thenReturn(organization);
        when(organizationRepository.save(organization))
                .thenReturn(organization);
        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        // when
        OrganizationResponse response = organizationService.create(1L, request);

        // then
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getOrganizationCode()).isEqualTo("DEV");
        assertThat(response.getOrganizationName()).isEqualTo("개발팀");

        verify(companyRepository).findById(1L);
        verify(organizationRepository)
                .existsByCompanyIdAndOrganizationCode(1L, "DEV");
        verify(organizationRepository).save(organization);
        verify(organizationMapper).toResponse(organization);
    }

    @Test
    void create_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        CreateOrganizationRequest request = mock(CreateOrganizationRequest.class);

        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> organizationService.create(999L, request))
                .isInstanceOf(BusinessException.class);

        verify(companyRepository).findById(999L);
        verifyNoInteractions(organizationRepository, organizationMapper);
    }

    @Test
    void create_비활성_회사에는_조직을_등록할_수_없다() {
        // given
        CreateOrganizationRequest request = mock(CreateOrganizationRequest.class);
        company.changeStatus(CompanyStatus.INACTIVE);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));

        // when & then
        assertThatThrownBy(() -> organizationService.create(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationRepository, never()).save(any());
        verifyNoInteractions(organizationMapper);
    }

    @Test
    void create_동일한_회사에_중복된_조직코드는_등록할_수_없다() {
        // given
        CreateOrganizationRequest request = mock(CreateOrganizationRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
        when(organizationRepository.existsByCompanyIdAndOrganizationCode(1L, "DEV"))
                .thenReturn(true);
        when(request.getOrganizationCode()).thenReturn("DEV");

        // when & then
        assertThatThrownBy(() -> organizationService.create(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationRepository, never()).save(any());
        verifyNoInteractions(organizationMapper);
    }

    @Test
    void create_종료일에_시작일이_없으면_예외가_발생한다() {
        // given
        CreateOrganizationRequest request = mock(CreateOrganizationRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
        when(organizationRepository.existsByCompanyIdAndOrganizationCode(1L, "DEV"))
                .thenReturn(false);
        when(request.getOrganizationCode()).thenReturn("DEV");
        when(request.getEffectiveTo()).thenReturn(LocalDate.of(2026, 12, 31));

        // when & then
        assertThatThrownBy(() -> organizationService.create(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationRepository, never()).save(any());
        verifyNoInteractions(organizationMapper);
    }

    @Test
    void create_시작일이_종료일보다_늦으면_예외가_발생한다() {
        // given
        CreateOrganizationRequest request = mock(CreateOrganizationRequest.class);

        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
        when(organizationRepository.existsByCompanyIdAndOrganizationCode(1L, "DEV"))
                .thenReturn(false);
        when(request.getOrganizationCode()).thenReturn("DEV");
        when(request.getEffectiveFrom()).thenReturn(LocalDate.of(2026, 12, 31));
        when(request.getEffectiveTo()).thenReturn(LocalDate.of(2026, 1, 1));

        // when & then
        assertThatThrownBy(() -> organizationService.create(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationRepository, never()).save(any());
        verifyNoInteractions(organizationMapper);
    }

    @Test
    void getOrganizations_회사별_조직목록을_조회한다() {
        // given
        when(companyRepository.findById(1L))
                .thenReturn(Optional.of(company));
        when(organizationRepository.findByCompanyId(1L))
                .thenReturn(List.of(organization));
        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        // when
        List<OrganizationResponse> response =
                organizationService.getOrganizations(1L);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getOrganizationCode()).isEqualTo("DEV");

        verify(companyRepository).findById(1L);
        verify(organizationRepository).findByCompanyId(1L);
        verify(organizationMapper).toResponse(organization);
    }

    @Test
    void getOrganizations_회사가_존재하지_않으면_예외가_발생한다() {
        // given
        when(companyRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> organizationService.getOrganizations(999L))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(organizationRepository, organizationMapper);
    }

    @Test
    void get_조직_상세정보를_조회한다() {
        // given
        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        // when
        OrganizationResponse response = organizationService.get(1L, 10L);

        // then
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getOrganizationName()).isEqualTo("개발팀");

        verify(organizationRepository).findById(10L);
        verify(organizationMapper).toResponse(organization);
    }

    @Test
    void get_다른_회사에_소속된_조직은_조회할_수_없다() {
        // given
        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));

        // when & then
        assertThatThrownBy(() -> organizationService.get(999L, 10L))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(organizationMapper);
    }

    @Test
    void get_조직이_존재하지_않으면_예외가_발생한다() {
        // given
        when(organizationRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> organizationService.get(1L, 999L))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(organizationMapper);
    }

    @Test
    void update_조직정보를_정상적으로_수정한다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(request.getOrganizationName()).thenReturn("플랫폼개발팀");
        when(request.getOrganizationType()).thenReturn(OrganizationType.TEAM);
        when(request.getParentOrganizationId()).thenReturn(null);
        when(request.getManagerUserId()).thenReturn(null);
        when(request.getEffectiveFrom()).thenReturn(LocalDate.of(2026, 1, 1));
        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        // when
        organizationService.update(1L, 10L, request);

        // then
        assertThat(organization.getOrganizationName()).isEqualTo("플랫폼개발팀");

        verify(organizationRepository).findById(10L);
        verify(organizationMapper).toResponse(organization);
    }

    @Test
    void update_자기자신을_상위조직으로_지정할_수_없다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(request.getParentOrganizationId()).thenReturn(10L);

        // when & then
        assertThatThrownBy(() -> organizationService.update(1L, 10L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationMapper, never()).toResponse(any());
    }

    @Test
    void update_하위조직을_상위조직으로_지정하여_순환관계를_만들_수_없다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);

        Organization child = Organization.builder()
                .id(20L)
                .company(company)
                .parentOrganization(organization)
                .organizationCode("DEV-CHILD")
                .organizationName("개발1팀")
                .organizationType(OrganizationType.TEAM)
                .status(OrganizationStatus.ACTIVE)
                .build();

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(organizationRepository.findById(20L))
                .thenReturn(Optional.of(child));
        when(request.getParentOrganizationId()).thenReturn(20L);

        // when & then
        assertThatThrownBy(() -> organizationService.update(1L, 10L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationMapper, never()).toResponse(any());
    }

    @Test
    void update_비활성_상위조직을_지정할_수_없다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);

        Organization inactiveParent = Organization.builder()
                .id(20L)
                .company(company)
                .organizationCode("PARENT")
                .organizationName("상위조직")
                .organizationType(OrganizationType.DIVISION)
                .status(OrganizationStatus.INACTIVE)
                .build();

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(organizationRepository.findById(20L))
                .thenReturn(Optional.of(inactiveParent));
        when(request.getParentOrganizationId()).thenReturn(20L);

        // when & then
        assertThatThrownBy(() -> organizationService.update(1L, 10L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationMapper, never()).toResponse(any());
    }

    @Test
    void update_다른_회사에_소속된_상위조직을_지정할_수_없다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(organizationRepository.findById(20L))
                .thenReturn(Optional.of(Organization.builder()
                        .id(20L)
                        .company(Company.builder().id(2L).build())
                        .organizationCode("OTHER")
                        .organizationName("다른 회사 조직")
                        .organizationType(OrganizationType.DIVISION)
                        .status(OrganizationStatus.ACTIVE)
                        .build()));
        when(request.getParentOrganizationId()).thenReturn(20L);

        // when & then
        assertThatThrownBy(() -> organizationService.update(1L, 10L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationMapper, never()).toResponse(any());
    }

    @Test
    void update_존재하지_않는_담당자를_지정할_수_없다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(request.getParentOrganizationId()).thenReturn(null);
        when(request.getManagerUserId()).thenReturn(999L);
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> organizationService.update(1L, 10L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationMapper, never()).toResponse(any());
    }

    @Test
    void update_비활성_사용자를_담당자로_지정할_수_없다() {
        // given
        UpdateOrganizationRequest request = mock(UpdateOrganizationRequest.class);
        User disabledUser = mock(User.class);

        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(request.getParentOrganizationId()).thenReturn(null);
        when(request.getManagerUserId()).thenReturn(100L);
        when(userRepository.findById(100L))
                .thenReturn(Optional.of(disabledUser));
        when(disabledUser.getEnabled()).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> organizationService.update(1L, 10L, request))
                .isInstanceOf(BusinessException.class);

        verify(organizationMapper, never()).toResponse(any());
    }

    @Test
    void changeStatus_조직상태를_비활성으로_변경한다() {
        // given
        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));
        when(organizationMapper.toResponse(organization))
                .thenReturn(organizationResponse);

        // when
        organizationService.changeStatus(1L, 10L, OrganizationStatus.INACTIVE);

        // then
        assertThat(organization.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);

        verify(organizationRepository).findById(10L);
        verify(organizationMapper).toResponse(organization);
    }

    @Test
    void changeStatus_다른_회사에_소속된_조직은_상태를_변경할_수_없다() {
        // given
        when(organizationRepository.findById(10L))
                .thenReturn(Optional.of(organization));

        // when & then
        assertThatThrownBy(() ->
                organizationService.changeStatus(999L, 10L, OrganizationStatus.INACTIVE))
                .isInstanceOf(BusinessException.class);

        assertThat(organization.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        verify(organizationMapper, never()).toResponse(any());
    }
}