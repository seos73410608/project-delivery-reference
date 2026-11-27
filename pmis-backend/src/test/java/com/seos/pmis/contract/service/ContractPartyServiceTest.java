
package com.seos.pmis.contract.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.company.entity.Company;
import com.seos.pmis.company.entity.CompanyStatus;
import com.seos.pmis.company.entity.Organization;
import com.seos.pmis.company.entity.OrganizationStatus;
import com.seos.pmis.company.repository.CompanyRepository;
import com.seos.pmis.company.repository.OrganizationRepository;
import com.seos.pmis.contract.dto.request.CreateContractPartyRequest;
import com.seos.pmis.contract.dto.response.ContractPartyResponse;
import com.seos.pmis.contract.entity.Contract;
import com.seos.pmis.contract.entity.ContractParty;
import com.seos.pmis.contract.entity.ContractPartyRole;
import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.entity.ContractType;
import com.seos.pmis.contract.mapper.ContractPartyMapper;
import com.seos.pmis.contract.repository.ContractPartyRepository;
import com.seos.pmis.contract.repository.ContractRepository;
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
class ContractPartyServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractPartyRepository contractPartyRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ContractPartyMapper contractPartyMapper;

    @InjectMocks
    private ContractPartyService contractPartyService;

    private Contract contract;
    private Company company;
    private ContractParty party;
    private ContractPartyResponse response;

    @BeforeEach
    void setUp() {
        contract = Contract.builder()
                .id(1L)
                .contractCode("CONTRACT-001")
                .contractName("테스트 계약")
                .contractType(ContractType.PROJECT)
                .status(ContractStatus.ACTIVE)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .currencyCode("KRW")
                .build();

        company = Company.builder()
                .id(10L)
                .companyCode("COMPANY-001")
                .companyName("테스트 회사")
                .status(CompanyStatus.ACTIVE)
                .build();

        party = ContractParty.builder()
                .id(100L)
                .contract(contract)
                .company(company)
                .partyRole(ContractPartyRole.CONTRACTOR)
                .primary(true)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .description("주 계약 당사자")
                .build();

        response = ContractPartyResponse.builder()
                .id(100L)
                .contractId(1L)
                .contractCode("CONTRACT-001")
                .contractName("테스트 계약")
                .companyId(10L)
                .companyCode("COMPANY-001")
                .companyName("테스트 회사")
                .partyRole(ContractPartyRole.CONTRACTOR)
                .primary(true)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .description("주 계약 당사자")
                .build();
    }

    @Test
    void getByContract_계약별_당사자_목록을_조회한다() {
        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findByContractId(1L))
                .thenReturn(List.of(party));
        when(contractPartyMapper.toResponse(party)).thenReturn(response);

        List<ContractPartyResponse> result =
                contractPartyService.getByContract(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContractId()).isEqualTo(1L);
        assertThat(result.get(0).getCompanyName()).isEqualTo("테스트 회사");

        verify(contractRepository).existsById(1L);
        verify(contractPartyRepository).findByContractId(1L);
        verify(contractPartyMapper).toResponse(party);
    }

    @Test
    void getByContract_계약이_존재하지_않으면_예외가_발생한다() {
        when(contractRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> contractPartyService.getByContract(999L))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).findByContractId(any());
        verifyNoInteractions(contractPartyMapper);
    }

    @Test
    void getByCompany_회사별_계약_당사자_이력을_조회한다() {
        when(companyRepository.existsById(10L)).thenReturn(true);
        when(contractPartyRepository.findByCompanyId(10L))
                .thenReturn(List.of(party));
        when(contractPartyMapper.toResponse(party)).thenReturn(response);

        List<ContractPartyResponse> result =
                contractPartyService.getByCompany(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompanyId()).isEqualTo(10L);

        verify(companyRepository).existsById(10L);
        verify(contractPartyRepository).findByCompanyId(10L);
    }

    @Test
    void getByCompany_회사가_존재하지_않으면_예외가_발생한다() {
        when(companyRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> contractPartyService.getByCompany(999L))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).findByCompanyId(any());
        verifyNoInteractions(contractPartyMapper);
    }

    @Test
    void addParty_계약_당사자를_정상적으로_등록한다() {
        CreateContractPartyRequest request = mock(CreateContractPartyRequest.class);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(request.getCompanyId()).thenReturn(10L);
        when(request.getOrganizationId()).thenReturn(null);
        when(request.getPartyRole()).thenReturn(ContractPartyRole.CONTRACTOR);
        when(request.isPrimary()).thenReturn(true);
        when(request.getEffectiveFrom()).thenReturn(LocalDate.of(2026, 1, 1));
        when(request.getDescription()).thenReturn("주 계약 당사자");

        when(contractPartyRepository
                .existsByContractIdAndCompanyIdAndOrganizationIdAndPartyRoleAndEffectiveToIsNull(
                        1L, 10L, null, ContractPartyRole.CONTRACTOR
                )).thenReturn(false);

        when(contractPartyRepository.save(any(ContractParty.class)))
                .thenReturn(party);
        when(contractPartyMapper.toResponse(party)).thenReturn(response);

        ContractPartyResponse result =
                contractPartyService.addParty(1L, request);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getPartyRole()).isEqualTo(ContractPartyRole.CONTRACTOR);

        verify(contractPartyRepository).save(any(ContractParty.class));
        verify(contractPartyMapper).toResponse(party);
    }

    @Test
    void addParty_계약이_존재하지_않으면_예외가_발생한다() {
        CreateContractPartyRequest request = mock(CreateContractPartyRequest.class);
        when(contractRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractPartyService.addParty(999L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).save(any());
        verifyNoInteractions(contractPartyMapper);
    }

    @Test
    void addParty_회사가_존재하지_않으면_예외가_발생한다() {
        CreateContractPartyRequest request = mock(CreateContractPartyRequest.class);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(request.getCompanyId()).thenReturn(999L);
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractPartyService.addParty(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).save(any());
    }

    @Test
    void addParty_비활성_회사이면_예외가_발생한다() {
        Company inactiveCompany = Company.builder()
                .id(10L)
                .companyCode("COMPANY-001")
                .companyName("테스트 회사")
                .status(CompanyStatus.INACTIVE)
                .build();

        CreateContractPartyRequest request = mock(CreateContractPartyRequest.class);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(request.getCompanyId()).thenReturn(10L);
        when(companyRepository.findById(10L))
                .thenReturn(Optional.of(inactiveCompany));

        assertThatThrownBy(() -> contractPartyService.addParty(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).save(any());
    }

    @Test
    void addParty_조직이_회사에_속하지_않으면_예외가_발생한다() {
        Organization organization = mock(Organization.class);
        Company anotherCompany = mock(Company.class);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));

        CreateContractPartyRequest request = mock(CreateContractPartyRequest.class);
        when(request.getCompanyId()).thenReturn(10L);
        when(request.getOrganizationId()).thenReturn(20L);
        when(organizationRepository.findById(20L))
                .thenReturn(Optional.of(organization));
        when(organization.getCompany()).thenReturn(anotherCompany);
        when(anotherCompany.getId()).thenReturn(99L);

        assertThatThrownBy(() -> contractPartyService.addParty(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).save(any());
    }

    @Test
    void addParty_비활성_조직이면_예외가_발생한다() {
        Organization organization = mock(Organization.class);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));

        CreateContractPartyRequest request = mock(CreateContractPartyRequest.class);
        when(request.getCompanyId()).thenReturn(10L);
        when(request.getOrganizationId()).thenReturn(20L);
        when(organizationRepository.findById(20L))
                .thenReturn(Optional.of(organization));
        when(organization.getCompany()).thenReturn(company);
        when(organization.getStatus()).thenReturn(OrganizationStatus.INACTIVE);

        assertThatThrownBy(() -> contractPartyService.addParty(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractPartyRepository, never()).save(any());
    }

    @Test
    void closeParty_계약_당사자를_정상적으로_종료한다() {
        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findById(100L))
                .thenReturn(Optional.of(party));
        when(contractPartyMapper.toResponse(party)).thenReturn(response);

        LocalDate effectiveTo = LocalDate.of(2026, 6, 30);

        ContractPartyResponse result =
                contractPartyService.closeParty(1L, 100L, effectiveTo);

        assertThat(party.getEffectiveTo()).isEqualTo(effectiveTo);
        assertThat(result.getId()).isEqualTo(100L);

        verify(contractPartyRepository).findById(100L);
        verify(contractPartyMapper).toResponse(party);
    }

    @Test
    void closeParty_시작일보다_빠른_종료일이면_예외가_발생한다() {
        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findById(100L))
                .thenReturn(Optional.of(party));

        assertThatThrownBy(() -> contractPartyService.closeParty(
                1L, 100L, LocalDate.of(2025, 12, 31)
        )).isInstanceOf(BusinessException.class);

        verify(contractPartyMapper, never()).toResponse(any());
    }

    @Test
    void closeParty_이미_종료된_당사자이면_예외가_발생한다() {
        party.close(LocalDate.of(2026, 6, 30));

        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findById(100L))
                .thenReturn(Optional.of(party));

        assertThatThrownBy(() -> contractPartyService.closeParty(
                1L, 100L, LocalDate.of(2026, 7, 31)
        )).isInstanceOf(BusinessException.class);
    }

    @Test
    void closeParty_다른_계약의_당사자이면_예외가_발생한다() {
        Contract anotherContract = Contract.builder()
                .id(2L)
                .contractCode("CONTRACT-002")
                .contractName("다른 계약")
                .contractType(ContractType.PROJECT)
                .status(ContractStatus.ACTIVE)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .currencyCode("KRW")
                .build();

        ContractParty anotherParty = ContractParty.builder()
                .id(100L)
                .contract(anotherContract)
                .company(company)
                .partyRole(ContractPartyRole.CONTRACTOR)
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findById(100L))
                .thenReturn(Optional.of(anotherParty));

        assertThatThrownBy(() -> contractPartyService.closeParty(
                1L, 100L, LocalDate.of(2026, 6, 30)
        )).isInstanceOf(BusinessException.class);

        verifyNoInteractions(contractPartyMapper);
    }

    @Test
    void changePrimary_주요_당사자_여부를_변경한다() {
        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findById(100L))
                .thenReturn(Optional.of(party));
        when(contractPartyMapper.toResponse(party)).thenReturn(response);

        contractPartyService.changePrimary(1L, 100L, false);

        assertThat(party.isPrimary()).isFalse();

        verify(contractPartyMapper).toResponse(party);
    }

    @Test
    void changePrimary_종료된_당사자이면_예외가_발생한다() {
        party.close(LocalDate.of(2026, 6, 30));

        when(contractRepository.existsById(1L)).thenReturn(true);
        when(contractPartyRepository.findById(100L))
                .thenReturn(Optional.of(party));

        assertThatThrownBy(() ->
                contractPartyService.changePrimary(1L, 100L, false))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(contractPartyMapper);
    }
}
