
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.response.CiAttributeDefinitionResponse;
import com.seos.cmdb.ci.entity.AttributeNodeType;
import com.seos.cmdb.ci.entity.CiAttributeDefinition;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.CiAttributeDefinitionMapper;
import com.seos.cmdb.ci.repository.CiAttributeDefinitionRepository;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CiAttributeDefinitionServiceTest {

    @Mock
    private CiAttributeDefinitionRepository ciAttributeDefinitionRepository;

    @Mock
    private CiTypeRepository ciTypeRepository;

    private CiAttributeDefinitionService ciAttributeDefinitionService;

    private CiType parent;
    private CiType child;

    @BeforeEach
    void setUp() {

        ciAttributeDefinitionService =
                new CiAttributeDefinitionService(
                        ciAttributeDefinitionRepository,
                        ciTypeRepository,
                        new CiAttributeDefinitionMapper()
                );

        parent = CiType.builder()
                .id(1L)
                .code("HARDWARE")
                .name("하드웨어")
                .assignable(false)
                .enabled(false)
                .sortOrder(1)
                .build();

        child = CiType.builder()
                .id(2L)
                .code("SERVER")
                .name("서버")
                .parent(parent)
                .assignable(true)
                .enabled(true)
                .sortOrder(1)
                .build();
    }

    @Test
    void getEffectiveDefinitions_현재_Type과_상위_Type의_속성_정의를_조회한다() {

        // given
        CiAttributeDefinition group = CiAttributeDefinition.builder()
                .id(100L)
                .attributeKey("common")
                .displayName("공통 정보")
                .nodeType(AttributeNodeType.GROUP)
                .displayOrder(1)
                .enabled(true)
                .build();

        CiAttributeDefinition field = CiAttributeDefinition.builder()
                .id(101L)
                .parent(group)
                .attributeKey("ci_code")
                .displayName("CI 코드")
                .nodeType(AttributeNodeType.FIELD)
                .displayOrder(1)
                .enabled(true)
                .build();

        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));

        when(ciAttributeDefinitionRepository.findEffectiveDefinitions(
                anyCollection()
        )).thenReturn(List.of(group, field));

        // when
        List<CiAttributeDefinitionResponse> result =
                ciAttributeDefinitionService.getEffectiveDefinitions(2L);

        // then
        assertThat(result).hasSize(1);

        CiAttributeDefinitionResponse groupResponse = result.get(0);

        assertThat(groupResponse.getAttributeKey()).isEqualTo("common");
        assertThat(groupResponse.getNodeType())
                .isEqualTo(AttributeNodeType.GROUP);

        assertThat(groupResponse.getChildren()).hasSize(1);
        assertThat(groupResponse.getChildren().get(0).getAttributeKey())
                .isEqualTo("ci_code");

        ArgumentCaptor<Collection<Long>> captor =
                ArgumentCaptor.forClass(Collection.class);

        verify(ciAttributeDefinitionRepository)
                .findEffectiveDefinitions(captor.capture());

        assertThat(captor.getValue())
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void getEffectiveDefinitions_비활성화된_상위_Type도_상속에_포함한다() {

        // given
        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));

        when(ciAttributeDefinitionRepository.findEffectiveDefinitions(
                anyCollection()
        )).thenReturn(List.of());

        // when
        ciAttributeDefinitionService.getEffectiveDefinitions(2L);

        // then
        ArgumentCaptor<Collection<Long>> captor =
                ArgumentCaptor.forClass(Collection.class);

        verify(ciAttributeDefinitionRepository)
                .findEffectiveDefinitions(captor.capture());

        assertThat(captor.getValue())
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void getEffectiveDefinitions_존재하지_않는_Type이면_예외가_발생한다() {

        // given
        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));

        // when & then
        assertThatThrownBy(
                () -> ciAttributeDefinitionService.getEffectiveDefinitions(999L)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_NOT_FOUND);

        verify(ciAttributeDefinitionRepository, never())
                .findEffectiveDefinitions(anyCollection());
    }

    @Test
    void getEffectiveDefinitions_비활성화된_대상_Type이면_예외가_발생한다() {

        // given
        CiType disabledType = CiType.builder()
                .id(3L)
                .code("DATABASE")
                .name("데이터베이스")
                .assignable(true)
                .enabled(false)
                .sortOrder(1)
                .build();

        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(disabledType));

        // when & then
        assertThatThrownBy(
                () -> ciAttributeDefinitionService.getEffectiveDefinitions(3L)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_DISABLED);

        verify(ciAttributeDefinitionRepository, never())
                .findEffectiveDefinitions(anyCollection());
    }

    @Test
    void getEffectiveDefinitions_displayOrder와_ID_기준으로_정렬한다() {

        // given
        CiAttributeDefinition group = CiAttributeDefinition.builder()
                .id(100L)
                .attributeKey("common")
                .displayName("공통 정보")
                .nodeType(AttributeNodeType.GROUP)
                .displayOrder(1)
                .enabled(true)
                .build();

        CiAttributeDefinition fieldB = CiAttributeDefinition.builder()
                .id(103L)
                .parent(group)
                .attributeKey("hostname")
                .displayName("호스트명")
                .nodeType(AttributeNodeType.FIELD)
                .displayOrder(2)
                .enabled(true)
                .build();

        CiAttributeDefinition fieldA = CiAttributeDefinition.builder()
                .id(102L)
                .parent(group)
                .attributeKey("ci_code")
                .displayName("CI 코드")
                .nodeType(AttributeNodeType.FIELD)
                .displayOrder(1)
                .enabled(true)
                .build();

        CiAttributeDefinition fieldC = CiAttributeDefinition.builder()
                .id(104L)
                .parent(group)
                .attributeKey("manager_id")
                .displayName("담당자")
                .nodeType(AttributeNodeType.FIELD)
                .displayOrder(1)
                .enabled(true)
                .build();

        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));

        when(ciAttributeDefinitionRepository.findEffectiveDefinitions(anyCollection()))
                .thenReturn(List.of(group, fieldB, fieldC, fieldA));

        // when
        List<CiAttributeDefinitionResponse> result =
                ciAttributeDefinitionService.getEffectiveDefinitions(2L);

        // then
        assertThat(result.get(0).getChildren())
                .extracting(CiAttributeDefinitionResponse::getAttributeKey)
                .containsExactly("ci_code", "manager_id", "hostname");
    }

    @Test
    void getEffectiveDefinitions_중첩된_Group을_트리로_구성한다() {

        // given
        CiAttributeDefinition rootGroup = CiAttributeDefinition.builder()
                .id(100L)
                .attributeKey("server")
                .displayName("서버 정보")
                .nodeType(AttributeNodeType.GROUP)
                .displayOrder(1)
                .enabled(true)
                .build();

        CiAttributeDefinition nestedGroup = CiAttributeDefinition.builder()
                .id(101L)
                .parent(rootGroup)
                .attributeKey("hardware")
                .displayName("하드웨어 정보")
                .nodeType(AttributeNodeType.GROUP)
                .displayOrder(1)
                .enabled(true)
                .build();

        CiAttributeDefinition field = CiAttributeDefinition.builder()
                .id(102L)
                .parent(nestedGroup)
                .attributeKey("cpu_cores")
                .displayName("CPU 코어")
                .nodeType(AttributeNodeType.FIELD)
                .displayOrder(1)
                .enabled(true)
                .build();

        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));

        when(ciAttributeDefinitionRepository.findEffectiveDefinitions(anyCollection()))
                .thenReturn(List.of(rootGroup, nestedGroup, field));

        // when
        List<CiAttributeDefinitionResponse> result =
                ciAttributeDefinitionService.getEffectiveDefinitions(2L);

        // then
        assertThat(result).hasSize(1);

        CiAttributeDefinitionResponse rootResponse = result.get(0);

        assertThat(rootResponse.getAttributeKey()).isEqualTo("server");
        assertThat(rootResponse.getChildren()).hasSize(1);

        CiAttributeDefinitionResponse nestedResponse =
                rootResponse.getChildren().get(0);

        assertThat(nestedResponse.getAttributeKey()).isEqualTo("hardware");
        assertThat(nestedResponse.getNodeType())
                .isEqualTo(AttributeNodeType.GROUP);

        assertThat(nestedResponse.getChildren()).hasSize(1);
        assertThat(nestedResponse.getChildren().get(0).getAttributeKey())
                .isEqualTo("cpu_cores");
    }

    @Test
    void getEffectiveDefinitions_비활성_Group의_하위_Field는_응답에서_제외한다() {

        // given
        CiAttributeDefinition activeGroup = CiAttributeDefinition.builder()
                .id(100L)
                .attributeKey("common")
                .displayName("공통 정보")
                .nodeType(AttributeNodeType.GROUP)
                .displayOrder(1)
                .enabled(true)
                .build();

        CiAttributeDefinition disabledGroup = CiAttributeDefinition.builder()
                .id(101L)
                .attributeKey("disabled_group")
                .displayName("비활성 그룹")
                .nodeType(AttributeNodeType.GROUP)
                .displayOrder(2)
                .enabled(false)
                .build();

        CiAttributeDefinition hiddenField = CiAttributeDefinition.builder()
                .id(102L)
                .parent(disabledGroup)
                .attributeKey("hidden_value")
                .displayName("숨김 항목")
                .nodeType(AttributeNodeType.FIELD)
                .displayOrder(1)
                .enabled(true)
                .build();

        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));

        // Repository의 enabled = true 조건을 반영
        when(ciAttributeDefinitionRepository.findEffectiveDefinitions(anyCollection()))
                .thenReturn(List.of(activeGroup, hiddenField));

        // when
        List<CiAttributeDefinitionResponse> result =
                ciAttributeDefinitionService.getEffectiveDefinitions(2L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAttributeKey()).isEqualTo("common");

        assertThat(result)
                .noneMatch(response ->
                        response.getAttributeKey().equals("disabled_group"));

        assertThat(result.get(0).getChildren()).isEmpty();
    }
}
