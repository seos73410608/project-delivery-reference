
package com.seos.pmis.auth.service;

import com.seos.pmis.auth.entity.SessionEndReason;
import com.seos.pmis.auth.entity.UserSession;
import com.seos.pmis.auth.entity.UserSessionHistory;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import com.seos.pmis.auth.repository.UserSessionHistoryRepository;
import com.seos.pmis.auth.repository.UserSessionRepository;
import com.seos.pmis.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UserSessionServiceTest {

    private UserSessionRepository userSessionRepository;
    private UserSessionHistoryRepository userSessionHistoryRepository;
    private UserSessionService userSessionService;

    private User user;

    @BeforeEach
    void setUp() {
        userSessionRepository = mock(UserSessionRepository.class);
        userSessionHistoryRepository =
                mock(UserSessionHistoryRepository.class);

        userSessionService = new UserSessionService(
                userSessionRepository,
                userSessionHistoryRepository
        );

        user = mock(User.class);
        given(user.getId()).willReturn(1L);
    }

    @Test
    void getActiveSessions_사용자의_활성세션목록을_조회한다() {
        List<UserSession> sessions = List.of(
                mock(UserSession.class),
                mock(UserSession.class)
        );

        given(userSessionRepository.findAllByUserId(1L))
                .willReturn(sessions);

        List<UserSession> result =
                userSessionService.getActiveSessions(1L);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(sessions);

        verify(userSessionRepository).findAllByUserId(1L);
    }

    @Test
    void getActiveSessions_사용자ID가_null이면_예외가발생한다() {
        assertThatThrownBy(
                () -> userSessionService.getActiveSessions(null)
        )
                .isInstanceOf(IllegalArgumentException.class);

        verify(userSessionRepository, never()).findAllByUserId(any());
    }


    @Test
    void terminateSelectedSessions_선택한세션의_이력을정확히저장하고_이후삭제한다() {
        UUID sessionId = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 11, 16, 9, 0);
        LocalDateTime lastAccessedAt =
                LocalDateTime.of(2026, 11, 19, 9, 55);

        UserSession session = mock(UserSession.class);

        given(session.getSessionId()).willReturn(sessionId);
        given(session.getUser()).willReturn(user);
        given(session.getCreatedAt()).willReturn(createdAt);
        given(session.getLastAccessedAt()).willReturn(lastAccessedAt);

        given(userSessionRepository.findAllByUserId(1L))
                .willReturn(List.of(session));

        userSessionService.terminateSelectedSessions(
                user,
                List.of(sessionId)
        );

        ArgumentCaptor<Iterable<UserSessionHistory>> captor =
                ArgumentCaptor.forClass(Iterable.class);

        InOrder inOrder = inOrder(
                userSessionHistoryRepository,
                userSessionRepository
        );

        inOrder.verify(userSessionHistoryRepository)
                .saveAll(captor.capture());

        inOrder.verify(userSessionRepository)
                .deleteAll(List.of(session));

        List<UserSessionHistory> histories =
                java.util.stream.StreamSupport
                        .stream(captor.getValue().spliterator(), false)
                        .toList();

        assertThat(histories).hasSize(1);

        UserSessionHistory history = histories.get(0);

        assertThat(history.getSessionId()).isEqualTo(sessionId);
        assertThat(history.getUser()).isSameAs(user);
        assertThat(history.getCreatedAt()).isEqualTo(createdAt);
        assertThat(history.getLastAccessedAt()).isEqualTo(lastAccessedAt);
        assertThat(history.getEndReason())
                .isEqualTo(SessionEndReason.USER_SELECTED);
        assertThat(history.getEndedAt()).isNotNull();
    }


    @Test
    void terminateSelectedSessions_중복된세션ID는_거부한다() {
        UUID sessionId = UUID.randomUUID();

        assertThatThrownBy(
                () -> userSessionService.terminateSelectedSessions(
                        user,
                        List.of(sessionId, sessionId)
                )
        )
                .isInstanceOf(IllegalArgumentException.class);

        verify(userSessionHistoryRepository, never()).saveAll(anyList());
        verify(userSessionRepository, never()).deleteAll(anyList());
    }

    @Test
    void terminateSelectedSessions_본인소유가아닌세션은_거부한다() {
        UUID otherSessionId = UUID.randomUUID();

        given(userSessionRepository.findAllByUserId(1L))
                .willReturn(List.of());

        assertThatThrownBy(
                () -> userSessionService.terminateSelectedSessions(
                        user,
                        List.of(otherSessionId)
                )
        )
                .isInstanceOf(IllegalArgumentException.class);

        verify(userSessionHistoryRepository, never()).saveAll(anyList());
        verify(userSessionRepository, never()).deleteAll(anyList());
    }

    @Test
    void terminateSelectedSessions_선택목록이_비어있으면_거부한다() {
        assertThatThrownBy(
                () -> userSessionService.terminateSelectedSessions(
                        user,
                        List.of()
                )
        )
                .isInstanceOf(IllegalArgumentException.class);

        verify(userSessionHistoryRepository, never()).saveAll(anyList());
        verify(userSessionRepository, never()).deleteAll(anyList());
    }

    private UserSession createSession(UUID sessionId) {
        LocalDateTime now = LocalDateTime.of(2026, 11, 19, 10, 0);

        UserSession session = mock(UserSession.class);

        given(session.getSessionId()).willReturn(sessionId);
        given(session.getUser()).willReturn(user);
        given(session.getCreatedAt()).willReturn(now.minusDays(3));
        given(session.getLastAccessedAt()).willReturn(now.minusMinutes(5));

        return session;
    }
}
