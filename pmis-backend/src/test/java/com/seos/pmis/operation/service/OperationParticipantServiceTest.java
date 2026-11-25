
package com.seos.pmis.operation.service;

import com.seos.common.exception.BusinessException;
import com.seos.pmis.operation.dto.response.OperationParticipantResponse;
import com.seos.pmis.operation.entity.OperationParticipant;
import com.seos.pmis.operation.entity.OperationParticipantRole;
import com.seos.pmis.operation.mapper.OperationParticipantMapper;
import com.seos.pmis.operation.repository.OperationParticipantRepository;
import com.seos.pmis.operation.repository.OperationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OperationParticipantService 테스트")
class OperationParticipantServiceTest {

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationParticipantRepository participantRepository;

    @Mock
    private OperationParticipantMapper participantMapper;

    @InjectMocks
    private OperationParticipantService participantService;

    private final LocalDate plannedHandoverDate =
            LocalDate.of(2026, 11, 30);

    private OperationParticipantResponse createResponse() {
        return OperationParticipantResponse.builder()
                .id(100L)
                .role(OperationParticipantRole.OPERATOR)
                .primary(true)
                .company(OperationParticipantResponse.CompanySummary.builder()
                        .id(10L)
                        .companyCode("COMP-001")
                        .companyName("운영 회사")
                        .build())
                .organization(OperationParticipantResponse.OrganizationSummary.builder()
                        .id(20L)
                        .organizationCode("ORG-001")
                        .organizationName("운영 조직")
                        .build())
                .build();
    }

    @Test
    @DisplayName("예정일 없이 인계 대상 참여자 목록을 조회한다")
    void findHandoverEligibleParticipants_withoutDate() {
        OperationParticipant participant = mock(OperationParticipant.class);
        OperationParticipantResponse expected = createResponse();

        when(operationRepository.existsById(1L)).thenReturn(true);
        when(participantRepository.findHandoverEligibleParticipants(1L))
                .thenReturn(List.of(participant));
        when(participantMapper.toResponse(participant))
                .thenReturn(expected);

        List<OperationParticipantResponse> result =
                participantService.findHandoverEligibleParticipants(
                        1L,
                        null);

        assertThat(result).containsExactly(expected);

        verify(participantRepository)
                .findHandoverEligibleParticipants(1L);
        verify(participantRepository, never())
                .findHandoverEligibleParticipantsOnDate(
                        anyLong(),
                        any(LocalDate.class));
        verify(participantMapper).toResponse(participant);
    }

    @Test
    @DisplayName("예정일 기준으로 유효한 인계 대상 참여자를 조회한다")
    void findHandoverEligibleParticipants_withDate() {
        OperationParticipant participant = mock(OperationParticipant.class);
        OperationParticipantResponse expected = createResponse();

        when(operationRepository.existsById(1L)).thenReturn(true);
        when(participantRepository.findHandoverEligibleParticipantsOnDate(
                1L,
                plannedHandoverDate))
                .thenReturn(List.of(participant));
        when(participantMapper.toResponse(participant))
                .thenReturn(expected);

        List<OperationParticipantResponse> result =
                participantService.findHandoverEligibleParticipants(
                        1L,
                        plannedHandoverDate);

        assertThat(result).containsExactly(expected);

        verify(participantRepository)
                .findHandoverEligibleParticipantsOnDate(
                        1L,
                        plannedHandoverDate);
        verify(participantRepository, never())
                .findHandoverEligibleParticipants(anyLong());
        verify(participantMapper).toResponse(participant);
    }

    @Test
    @DisplayName("등록된 인계 대상 참여자가 없으면 빈 목록을 반환한다")
    void findHandoverEligibleParticipants_empty() {
        when(operationRepository.existsById(1L)).thenReturn(true);
        when(participantRepository.findHandoverEligibleParticipants(1L))
                .thenReturn(List.of());

        List<OperationParticipantResponse> result =
                participantService.findHandoverEligibleParticipants(
                        1L,
                        null);

        assertThat(result).isEmpty();

        verifyNoInteractions(participantMapper);
    }

    @Test
    @DisplayName("존재하지 않는 운영 서비스의 참여자를 조회할 수 없다")
    void findHandoverEligibleParticipants_operationNotFound() {
        when(operationRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() ->
                participantService.findHandoverEligibleParticipants(
                        999L,
                        null))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(participantRepository, participantMapper);
    }

    @Test
    @DisplayName("운영 서비스 ID가 null이면 참여자를 조회할 수 없다")
    void findHandoverEligibleParticipants_nullOperationId() {
        assertThatThrownBy(() ->
                participantService.findHandoverEligibleParticipants(
                        null,
                        null))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                operationRepository,
                participantRepository,
                participantMapper);
    }
}
