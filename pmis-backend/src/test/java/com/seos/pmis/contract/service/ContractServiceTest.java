package com.seos.pmis.contract.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.contract.dto.request.ContractSearchRequest;
import com.seos.pmis.contract.dto.request.CreateContractRequest;
import com.seos.pmis.contract.dto.request.UpdateContractRequest;
import com.seos.pmis.contract.dto.response.ContractResponse;
import com.seos.pmis.contract.entity.Contract;
import com.seos.pmis.contract.entity.ContractStatus;
import com.seos.pmis.contract.entity.ContractType;
import com.seos.pmis.contract.mapper.ContractMapper;
import com.seos.pmis.contract.repository.ContractRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractService contractService;

    private Contract contract;

    private ContractResponse contractResponse;

    @BeforeEach
    void setUp() {
        contract = Contract.builder()
                .id(1L)
                .contractCode("CONTRACT-001")
                .contractName("테스트 계약")
                .contractType(ContractType.PROJECT)
                .status(ContractStatus.DRAFT)
                .signedDate(LocalDate.of(2026, 1, 1))
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .currencyCode("KRW")
                .contractAmount(new BigDecimal("100000000"))
                .description("테스트 계약 설명")
                .build();

        contractResponse = ContractResponse.builder()
                .id(1L)
                .contractCode("CONTRACT-001")
                .contractName("테스트 계약")
                .contractType(ContractType.PROJECT)
                .status(ContractStatus.DRAFT)
                .signedDate(LocalDate.of(2026, 1, 1))
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .currencyCode("KRW")
                .contractAmount(new BigDecimal("100000000"))
                .description("테스트 계약 설명")
                .build();
    }

    @Test
    void create_계약을_정상적으로_등록한다() {
        // given
        CreateContractRequest request = mock(CreateContractRequest.class);

        when(request.getContractCode()).thenReturn("CONTRACT-001");
        when(request.getContractName()).thenReturn("테스트 계약");
        when(request.getContractType()).thenReturn(ContractType.PROJECT);
        when(request.getSignedDate())
                .thenReturn(LocalDate.of(2026, 1, 1));
        when(request.getStartDate())
                .thenReturn(LocalDate.of(2026, 1, 1));
        when(request.getEndDate())
                .thenReturn(LocalDate.of(2026, 12, 31));
        when(request.getCurrencyCode()).thenReturn("KRW");
        when(request.getContractAmount())
                .thenReturn(new BigDecimal("100000000"));
        when(request.getDescription()).thenReturn("테스트 계약 설명");

        when(contractRepository.existsByContractCode("CONTRACT-001"))
                .thenReturn(false);
        when(contractRepository.save(any(Contract.class)))
                .thenReturn(contract);
        when(contractMapper.toResponse(contract))
                .thenReturn(contractResponse);

        // when
        ContractResponse response = contractService.create(request);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getContractCode()).isEqualTo("CONTRACT-001");
        assertThat(response.getContractName()).isEqualTo("테스트 계약");

        assertThat(contract.getStatus()).isEqualTo(ContractStatus.DRAFT);

        verify(contractRepository).existsByContractCode("CONTRACT-001");
        verify(contractRepository).save(any(Contract.class));
        verify(contractMapper).toResponse(contract);
    }

    @Test
    void create_계약코드가_중복되면_예외가_발생한다() {
        // given
        CreateContractRequest request = mock(CreateContractRequest.class);

        when(request.getContractCode()).thenReturn("CONTRACT-001");
        when(contractRepository.existsByContractCode("CONTRACT-001"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> contractService.create(request))
                .isInstanceOf(BusinessException.class);

        verify(contractRepository).existsByContractCode("CONTRACT-001");
        verify(contractRepository, never()).save(any());
        verifyNoInteractions(contractMapper);
    }

    @Test
    void create_계약기간이_잘못되면_예외가_발생한다() {
        // given
        CreateContractRequest request = mock(CreateContractRequest.class);

        when(request.getContractCode()).thenReturn("CONTRACT-002");
        when(contractRepository.existsByContractCode("CONTRACT-002"))
                .thenReturn(false);
        when(request.getStartDate())
                .thenReturn(LocalDate.of(2026, 12, 31));
        when(request.getEndDate())
                .thenReturn(LocalDate.of(2026, 1, 1));

        // when & then
        assertThatThrownBy(() -> contractService.create(request))
                .isInstanceOf(BusinessException.class);

        verify(contractRepository, never()).save(any());
        verifyNoInteractions(contractMapper);
    }

    @Test
    void get_계약_상세정보를_조회한다() {
        // given
        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));
        when(contractMapper.toResponse(contract))
                .thenReturn(contractResponse);

        // when
        ContractResponse response = contractService.get(1L);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getContractCode()).isEqualTo("CONTRACT-001");
        assertThat(response.getContractName()).isEqualTo("테스트 계약");

        verify(contractRepository).findById(1L);
        verify(contractMapper).toResponse(contract);
    }

    @Test
    void get_계약이_존재하지_않으면_예외가_발생한다() {
        // given
        when(contractRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contractService.get(999L))
                .isInstanceOf(BusinessException.class);

        verify(contractRepository).findById(999L);
        verifyNoInteractions(contractMapper);
    }

    @Test
    void search_검색조건에_맞는_계약목록을_조회한다() {
        // given
        ContractSearchRequest request = new ContractSearchRequest();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Contract> contractPage = new PageImpl<>(
                List.of(contract), pageable, 1
        );

        when(contractRepository.findAll(
                any(Specification.class), any(Pageable.class)
        )).thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(contractResponse);

        // when
        Page<ContractResponse> response =
                contractService.search(request, pageable);

        // then
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getContractCode())
                .isEqualTo("CONTRACT-001");

        verify(contractRepository).findAll(
                any(Specification.class), eq(pageable)
        );
        verify(contractMapper).toResponse(contract);
    }

    @Test
    void update_계약정보를_정상적으로_수정한다() {
        // given
        UpdateContractRequest request = mock(UpdateContractRequest.class);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));

        when(request.getContractName()).thenReturn("변경된 계약명");
        when(request.getContractType()).thenReturn(ContractType.MAINTENANCE);
        when(request.getSignedDate())
                .thenReturn(LocalDate.of(2026, 2, 1));
        when(request.getStartDate())
                .thenReturn(LocalDate.of(2026, 2, 1));
        when(request.getEndDate())
                .thenReturn(LocalDate.of(2027, 1, 31));
        when(request.getCurrencyCode()).thenReturn("USD");
        when(request.getContractAmount())
                .thenReturn(new BigDecimal("200000"));
        when(request.getDescription()).thenReturn("변경된 설명");

        when(contractMapper.toResponse(contract))
                .thenReturn(contractResponse);

        // when
        contractService.update(1L, request);

        // then
        assertThat(contract.getContractName()).isEqualTo("변경된 계약명");
        assertThat(contract.getContractType())
                .isEqualTo(ContractType.MAINTENANCE);
        assertThat(contract.getStartDate())
                .isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(contract.getEndDate())
                .isEqualTo(LocalDate.of(2027, 1, 31));
        assertThat(contract.getCurrencyCode()).isEqualTo("USD");
        assertThat(contract.getContractAmount())
                .isEqualByComparingTo("200000");
        assertThat(contract.getDescription()).isEqualTo("변경된 설명");

        verify(contractRepository).findById(1L);
        verify(contractMapper).toResponse(contract);
    }

    @Test
    void update_계약기간이_잘못되면_예외가_발생한다() {
        // given
        UpdateContractRequest request = mock(UpdateContractRequest.class);

        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));
        when(request.getStartDate())
                .thenReturn(LocalDate.of(2027, 1, 1));
        when(request.getEndDate())
                .thenReturn(LocalDate.of(2026, 1, 1));

        // when & then
        assertThatThrownBy(() -> contractService.update(1L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractMapper, never()).toResponse(any());
    }

    @Test
    void update_계약이_존재하지_않으면_예외가_발생한다() {
        // given
        UpdateContractRequest request = mock(UpdateContractRequest.class);

        when(contractRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contractService.update(999L, request))
                .isInstanceOf(BusinessException.class);

        verify(contractRepository).findById(999L);
        verifyNoInteractions(contractMapper);
    }

    @Test
    void changeStatus_계약상태를_ACTIVE로_변경한다() {
        // given
        when(contractRepository.findById(1L))
                .thenReturn(Optional.of(contract));
        when(contractMapper.toResponse(contract))
                .thenReturn(contractResponse);

        // when
        contractService.changeStatus(1L, ContractStatus.ACTIVE);

        // then
        assertThat(contract.getStatus()).isEqualTo(ContractStatus.ACTIVE);

        verify(contractRepository).findById(1L);
        verify(contractMapper).toResponse(contract);
    }

    @Test
    void changeStatus_계약이_존재하지_않으면_예외가_발생한다() {
        // given
        when(contractRepository.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                contractService.changeStatus(999L, ContractStatus.ACTIVE))
                .isInstanceOf(BusinessException.class);

        verify(contractRepository).findById(999L);
        verifyNoInteractions(contractMapper);
    }

    @Test
    void changeStatus_상태가_null이면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() ->
                contractService.changeStatus(1L, null))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(contractRepository, contractMapper);
    }
}