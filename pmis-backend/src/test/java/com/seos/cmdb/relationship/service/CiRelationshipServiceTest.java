package com.seos.cmdb.relationship.service;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.cmdb.relationship.dto.request.CreateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.request.UpdateCiRelationshipRequest;
import com.seos.cmdb.relationship.dto.response.CiRelationshipResponse;
import com.seos.cmdb.relationship.dto.response.CiRelationshipTypeResponse;
import com.seos.cmdb.relationship.entity.RelationshipDirection;
import com.seos.pmis.auth.security.principal.UserPrincipal;
import com.seos.pmis.user.entity.User;
import com.seos.pmis.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import com.seos.cmdb.relationship.entity.CiRelationship;
import com.seos.cmdb.relationship.entity.CiRelationshipType;
import com.seos.cmdb.relationship.entity.RelationshipStatus;
import com.seos.cmdb.relationship.exception.CiRelationshipErrorCode;
import com.seos.cmdb.relationship.mapper.CiRelationshipMapper;
import com.seos.cmdb.relationship.repository.CiRelationshipRepository;
import com.seos.cmdb.relationship.repository.CiRelationshipTypeRepository;
import com.seos.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CiRelationshipServiceTest {

    @Mock
    private CiRelationshipRepository relationshipRepository;

    @Mock
    private CiRelationshipTypeRepository relationshipTypeRepository;

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CiRelationshipMapper relationshipMapper;

    @InjectMocks
    private CiRelationshipService relationshipService;

    private ConfigurationItem sourceCi;
    private ConfigurationItem targetCi;
    private CiRelationshipType relationshipType;
    private CiRelationshipResponse response;

    @BeforeEach
    void setUp() {
        sourceCi = ConfigurationItem.builder()
                .id(100L)
                .ciCode("CI-000100")
                .ciName("애플리케이션 서버")
                .build();

        targetCi = ConfigurationItem.builder()
                .id(200L)
                .ciCode("CI-000200")
                .ciName("데이터베이스 서버")
                .build();

        relationshipType = CiRelationshipType.builder()
                .id(1L)
                .code("DEPENDS_ON")
                .name("의존")
                .enabled(true)
                .sortOrder(1)
                .build();

        response = CiRelationshipResponse.builder()
                .id(10L)
                .sourceCiId(100L)
                .targetCiId(200L)
                .relationshipTypeId(1L)
                .status(RelationshipStatus.ACTIVE)
                .build();
    }

    @Test
    void create_신규_관계를_생성한다() {
        // given
        CreateCiRelationshipRequest request =
                mock(CreateCiRelationshipRequest.class);

        when(request.getTargetCiId()).thenReturn(200L);
        when(request.getRelationshipTypeId()).thenReturn(1L);
        when(request.getDescription()).thenReturn("DB 의존 관계");

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));
        when(configurationItemRepository.findById(200L))
                .thenReturn(Optional.of(targetCi));
        when(relationshipTypeRepository.findById(1L))
                .thenReturn(Optional.of(relationshipType));
        when(relationshipRepository
                .findBySourceCi_IdAndTargetCi_IdAndRelationshipType_Id(
                        100L, 200L, 1L
                ))
                .thenReturn(Optional.empty());

        when(relationshipRepository.save(any(CiRelationship.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(relationshipMapper.toResponse(any(CiRelationship.class)))
                .thenReturn(response);

        // when
        CiRelationshipResponse result =
                relationshipService.create(100L, request);

        // then
        assertThat(result.getStatus()).isEqualTo(RelationshipStatus.ACTIVE);

        verify(relationshipRepository).save(any(CiRelationship.class));
    }

    @Test
    void create_이미_ACTIVE인_관계는_기존_관계를_그대로_반환한다() {
        // given
        CreateCiRelationshipRequest request =
                mock(CreateCiRelationshipRequest.class);

        when(request.getTargetCiId()).thenReturn(200L);
        when(request.getRelationshipTypeId()).thenReturn(1L);

        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(relationshipType)
                .description("기존 설명")
                .status(RelationshipStatus.ACTIVE)
                .build();

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));
        when(configurationItemRepository.findById(200L))
                .thenReturn(Optional.of(targetCi));
        when(relationshipTypeRepository.findById(1L))
                .thenReturn(Optional.of(relationshipType));
        when(relationshipRepository
                .findBySourceCi_IdAndTargetCi_IdAndRelationshipType_Id(
                        100L, 200L, 1L
                ))
                .thenReturn(Optional.of(existing));
        when(relationshipMapper.toResponse(existing))
                .thenReturn(response);

        // when
        CiRelationshipResponse result =
                relationshipService.create(100L, request);

        // then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(existing.getDescription()).isEqualTo("기존 설명");
        assertThat(existing.getStatus()).isEqualTo(RelationshipStatus.ACTIVE);

        verify(relationshipRepository, never()).save(any());
        verify(relationshipMapper).toResponse(existing);
    }

    @Test
    void create_INACTIVE_관계는_기존_ID를_유지하고_재활성화한다() {
        // given
        CreateCiRelationshipRequest request =
                mock(CreateCiRelationshipRequest.class);

        when(request.getTargetCiId()).thenReturn(200L);
        when(request.getRelationshipTypeId()).thenReturn(1L);
        when(request.getDescription()).thenReturn("재등록된 설명");

        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(relationshipType)
                .description("이전 설명")
                .status(RelationshipStatus.INACTIVE)
                .build();

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));
        when(configurationItemRepository.findById(200L))
                .thenReturn(Optional.of(targetCi));
        when(relationshipTypeRepository.findById(1L))
                .thenReturn(Optional.of(relationshipType));
        when(relationshipRepository
                .findBySourceCi_IdAndTargetCi_IdAndRelationshipType_Id(
                        100L, 200L, 1L
                ))
                .thenReturn(Optional.of(existing));
        when(relationshipMapper.toResponse(existing))
                .thenReturn(response);

        // when
        relationshipService.create(100L, request);

        // then
        assertThat(existing.getId()).isEqualTo(10L);
        assertThat(existing.getStatus()).isEqualTo(RelationshipStatus.ACTIVE);
        assertThat(existing.getDescription()).isEqualTo("재등록된 설명");
        assertThat(existing.getDeactivatedAt()).isNull();
        assertThat(existing.getDeactivatedBy()).isNull();
        assertThat(existing.getDeactivationReason()).isNull();

        verify(relationshipRepository, never()).save(any());
        verify(relationshipMapper).toResponse(existing);
    }

    @Test
    void create_비활성화된_Type으로_INACTIVE_관계를_재활성화할_수_없다() {
        // given
        CreateCiRelationshipRequest request =
                mock(CreateCiRelationshipRequest.class);

        when(request.getTargetCiId()).thenReturn(200L);
        when(request.getRelationshipTypeId()).thenReturn(1L);

        CiRelationshipType disabledType = CiRelationshipType.builder()
                .id(1L)
                .code("DEPENDS_ON")
                .name("의존")
                .enabled(false)
                .build();

        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(disabledType)
                .status(RelationshipStatus.INACTIVE)
                .build();

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));
        when(configurationItemRepository.findById(200L))
                .thenReturn(Optional.of(targetCi));
        when(relationshipTypeRepository.findById(1L))
                .thenReturn(Optional.of(disabledType));
        when(relationshipRepository
                .findBySourceCi_IdAndTargetCi_IdAndRelationshipType_Id(
                        100L, 200L, 1L
                ))
                .thenReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> relationshipService.create(100L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiRelationshipErrorCode.RELATIONSHIP_TYPE_DISABLED);

        assertThat(existing.getStatus()).isEqualTo(RelationshipStatus.INACTIVE);
        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void create_자기_자신과의_관계는_생성할_수_없다() {
        // given
        CreateCiRelationshipRequest request =
                mock(CreateCiRelationshipRequest.class);

        when(request.getTargetCiId()).thenReturn(100L);

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));

        // when & then
        assertThatThrownBy(() -> relationshipService.create(100L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        CiRelationshipErrorCode.RELATIONSHIP_SELF_REFERENCE
                );

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void getById_관계_ID로_단건_조회한다() {
        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(relationshipType)
                .status(RelationshipStatus.ACTIVE)
                .build();

        when(relationshipRepository.findById(10L))
                .thenReturn(Optional.of(existing));
        when(relationshipMapper.toResponse(existing))
                .thenReturn(response);

        CiRelationshipResponse result = relationshipService.getById(10L);

        assertThat(result).isSameAs(response);
        verify(relationshipRepository).findById(10L);
        verify(relationshipMapper).toResponse(existing);
    }

    @Test
    void getAllForCi_OUTGOING_조건으로_관계_목록을_조회한다() {
        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(relationshipType)
                .status(RelationshipStatus.ACTIVE)
                .build();

        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));
        when(relationshipRepository.findAllForCi(
                100L, true, false, RelationshipStatus.ACTIVE, 1L
        )).thenReturn(List.of(existing));
        when(relationshipMapper.toResponse(existing))
                .thenReturn(response);

        List<CiRelationshipResponse> result =
                relationshipService.getAllForCi(
                        100L,
                        RelationshipDirection.OUTGOING,
                        RelationshipStatus.ACTIVE,
                        1L
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isSameAs(response);

        verify(relationshipRepository).findAllForCi(
                100L, true, false, RelationshipStatus.ACTIVE, 1L
        );
    }

    @Test
    void getAllForCi_방향이_null이면_예외가_발생한다() {
        when(configurationItemRepository.findById(100L))
                .thenReturn(Optional.of(sourceCi));

        assertThatThrownBy(() ->
                relationshipService.getAllForCi(
                        100L, null, null, null
                )
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(
                        CiRelationshipErrorCode.RELATIONSHIP_INVALID_DIRECTION
                );

        verify(relationshipRepository, never())
                .findAllForCi(any(), anyBoolean(), anyBoolean(), any(), any());
    }

    @Test
    void update_관계_설명만_수정한다() {
        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(relationshipType)
                .description("수정 전 설명")
                .status(RelationshipStatus.ACTIVE)
                .build();

        UpdateCiRelationshipRequest request =
                mock(UpdateCiRelationshipRequest.class);

        when(request.getDescription()).thenReturn("수정 후 설명");
        when(relationshipRepository.findById(10L))
                .thenReturn(Optional.of(existing));
        when(relationshipMapper.toResponse(existing))
                .thenReturn(response);

        CiRelationshipResponse result =
                relationshipService.update(10L, request);

        assertThat(existing.getDescription()).isEqualTo("수정 후 설명");
        assertThat(existing.getSourceCi()).isSameAs(sourceCi);
        assertThat(existing.getTargetCi()).isSameAs(targetCi);
        assertThat(existing.getRelationshipType()).isSameAs(relationshipType);
        assertThat(result).isSameAs(response);

        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void deactivate_인증된_사용자와_사유를_기록한다() {
        CiRelationship existing = CiRelationship.builder()
                .id(10L)
                .sourceCi(sourceCi)
                .targetCi(targetCi)
                .relationshipType(relationshipType)
                .description("DB 의존 관계")
                .status(RelationshipStatus.ACTIVE)
                .build();

        User actor = mock(User.class);

        UserPrincipal principal = UserPrincipal.builder()
                .userId(7L)
                .username("test-user")
                .password("test-password")
                .role("ROLE_USER")
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(relationshipRepository.findById(10L))
                .thenReturn(Optional.of(existing));
        when(userRepository.findById(7L))
                .thenReturn(Optional.of(actor));
        when(relationshipMapper.toResponse(existing))
                .thenReturn(response);

        try {
            CiRelationshipResponse result =
                    relationshipService.deactivate(
                            10L,
                            "서버 교체에 따른 관계 종료"
                    );

            assertThat(existing.getStatus())
                    .isEqualTo(RelationshipStatus.INACTIVE);
            assertThat(existing.getDeactivatedAt()).isNotNull();
            assertThat(existing.getDeactivatedBy()).isSameAs(actor);
            assertThat(existing.getDeactivationReason())
                    .isEqualTo("서버 교체에 따른 관계 종료");
            assertThat(result).isSameAs(response);

            verify(userRepository).findById(7L);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getRelationshipTypes_비활성_Type_포함_여부에_따라_조회한다() {
        CiRelationshipTypeResponse typeResponse =
                mock(CiRelationshipTypeResponse.class);

        when(relationshipTypeRepository
                .findAllByEnabledTrueOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(relationshipType));

        when(relationshipMapper.toTypeResponse(relationshipType))
                .thenReturn(typeResponse);

        List<CiRelationshipTypeResponse> result =
                relationshipService.getRelationshipTypes(false);

        assertThat(result).containsExactly(typeResponse);

        verify(relationshipTypeRepository)
                .findAllByEnabledTrueOrderBySortOrderAscNameAsc();
        verify(relationshipTypeRepository, never())
                .findAllByOrderBySortOrderAscNameAsc();
    }
}
