
package com.seos.cmdb.ci.service;

import com.seos.cmdb.ci.dto.request.CreateCiTypeRequest;
import com.seos.cmdb.ci.dto.request.UpdateCiTypeRequest;
import com.seos.cmdb.ci.dto.response.CiTypeResponse;
import com.seos.cmdb.ci.entity.CiType;
import com.seos.cmdb.ci.exception.CiTypeErrorCode;
import com.seos.cmdb.ci.mapper.CiTypeMapper;
import com.seos.cmdb.ci.repository.CiTypeRepository;
import com.seos.cmdb.ci.repository.ConfigurationItemRepository;
import com.seos.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CiTypeServiceTest {

    @Mock
    private CiTypeRepository ciTypeRepository;

    @Mock
    private ConfigurationItemRepository configurationItemRepository;

    @Mock
    private CiTypeMapper ciTypeMapper;

    @InjectMocks
    private CiTypeService ciTypeService;

    private CiType parent;
    private CiType child;

    @BeforeEach
    void setUp() {
        parent = CiType.builder()
                .id(1L)
                .code("HARDWARE")
                .name("하드웨어")
                .assignable(false)
                .enabled(true)
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
    void create_정상적으로_CI_Type을_생성한다() {
        // given
        CreateCiTypeRequest request = mock(CreateCiTypeRequest.class);
        CiTypeResponse response = CiTypeResponse.builder()
                .id(2L)
                .code("SERVER")
                .name("서버")
                .parentId(1L)
                .assignable(true)
                .enabled(true)
                .sortOrder(1)
                .build();

        when(request.getCode()).thenReturn("SERVER");
        when(request.getParentId()).thenReturn(1L);
        when(ciTypeRepository.existsByCode("SERVER")).thenReturn(false);
        when(ciTypeRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(ciTypeMapper.toEntity(request, parent)).thenReturn(child);
        when(ciTypeRepository.save(child)).thenReturn(child);
        when(ciTypeMapper.toResponse(child)).thenReturn(response);

        // when
        CiTypeResponse result = ciTypeService.create(request);

        // then
        assertThat(result.getCode()).isEqualTo("SERVER");
        assertThat(result.getParentId()).isEqualTo(1L);

        verify(ciTypeRepository).save(child);
    }

    @Test
    void create_중복된_코드이면_예외가_발생한다() {
        // given
        CreateCiTypeRequest request = mock(CreateCiTypeRequest.class);
        when(request.getCode()).thenReturn("SERVER");
        when(ciTypeRepository.existsByCode("SERVER")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> ciTypeService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_CODE_DUPLICATED);

        verify(ciTypeRepository, never()).save(any());
    }

    @Test
    void create_비활성화된_부모를_지정하면_예외가_발생한다() {
        // given
        CreateCiTypeRequest request = mock(CreateCiTypeRequest.class);
        CiType disabledParent = CiType.builder()
                .id(3L)
                .code("PLATFORM")
                .name("플랫폼")
                .enabled(false)
                .assignable(false)
                .build();

        when(request.getCode()).thenReturn("DATABASE");
        when(request.getParentId()).thenReturn(3L);
        when(ciTypeRepository.existsByCode("DATABASE")).thenReturn(false);
        when(ciTypeRepository.findById(3L))
                .thenReturn(Optional.of(disabledParent));

        // when & then
        assertThatThrownBy(() -> ciTypeService.create(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_PARENT_DISABLED);

        verify(ciTypeRepository, never()).save(any());
    }

    @Test
    void getAll_전체_CI_Type을_정렬된_목록으로_조회한다() {
        // given
        when(ciTypeRepository.findAllByOrderBySortOrderAscNameAsc())
                .thenReturn(List.of(parent, child));
        when(ciTypeMapper.toResponse(parent))
                .thenReturn(CiTypeResponse.builder().id(1L).build());
        when(ciTypeMapper.toResponse(child))
                .thenReturn(CiTypeResponse.builder().id(2L).build());

        // when
        List<CiTypeResponse> result = ciTypeService.getAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CiTypeResponse::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void update_자기_자신을_부모로_지정하면_예외가_발생한다() {
        // given
        UpdateCiTypeRequest request = mock(UpdateCiTypeRequest.class);

        when(ciTypeRepository.findById(2L)).thenReturn(Optional.of(child));
        when(request.getParentId()).thenReturn(2L);

        // when & then
        assertThatThrownBy(() -> ciTypeService.update(2L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_SELF_PARENT);
    }

    @Test
    void update_하위_Type를_부모로_지정하면_순환_참조_예외가_발생한다() {
        // given
        UpdateCiTypeRequest request = mock(UpdateCiTypeRequest.class);

        when(ciTypeRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(request.getParentId()).thenReturn(2L);
        when(ciTypeRepository.findById(2L)).thenReturn(Optional.of(child));

        // when & then
        assertThatThrownBy(() -> ciTypeService.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_CIRCULAR_REFERENCE);
    }

    @Test
    void update_CI가_참조중인_Type을_비할당_Type으로_변경할_수_없다() {
        // given
        UpdateCiTypeRequest request = mock(UpdateCiTypeRequest.class);

        when(ciTypeRepository.findById(2L))
                .thenReturn(Optional.of(child));

        when(request.getParentId()).thenReturn(null);
        when(request.getAssignable()).thenReturn(false);

        when(configurationItemRepository.existsByCiType_Id(2L))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> ciTypeService.update(2L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_HAS_CONFIGURATION_ITEMS);

        verify(configurationItemRepository).existsByCiType_Id(2L);
    }

    @Test
    void disable_활성_하위_Type가_있으면_비활성화할_수_없다() {
        // given
        when(ciTypeRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(ciTypeRepository.existsByParent_IdAndEnabledTrue(1L))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> ciTypeService.disable(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_HAS_ACTIVE_CHILDREN);

        verify(ciTypeRepository, never()).save(any());
    }

    @Test
    void disable_CI가_참조중이면_비활성화할_수_없다() {
        // given
        when(ciTypeRepository.findById(2L))
                .thenReturn(Optional.of(child));

        when(ciTypeRepository.existsByParent_IdAndEnabledTrue(2L))
                .thenReturn(false);

        when(configurationItemRepository.existsByCiType_Id(2L))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> ciTypeService.disable(2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_HAS_CONFIGURATION_ITEMS);

        verify(configurationItemRepository).existsByCiType_Id(2L);
    }

    @Test
    void delete_하위_Type가_있으면_삭제할_수_없다() {
        // given
        when(ciTypeRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(ciTypeRepository.countByParent_Id(1L)).thenReturn(1L);

        // when & then
        assertThatThrownBy(() -> ciTypeService.delete(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_HAS_CHILDREN);

        verify(ciTypeRepository, never()).delete(any());
    }

    @Test
    void delete_CI가_참조중이면_삭제할_수_없다() {
        // given
        when(ciTypeRepository.findById(2L)).thenReturn(Optional.of(child));
        when(ciTypeRepository.countByParent_Id(2L)).thenReturn(0L);
        when(configurationItemRepository.existsByCiType_Id(2L))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> ciTypeService.delete(2L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_IN_USE);

        verify(ciTypeRepository, never()).delete(any());
    }

    @Test
    void enable_부모가_비활성화되어_있으면_활성화할_수_없다() {
        // given
        CiType disabledParent = CiType.builder()
                .id(10L)
                .code("PLATFORM")
                .name("플랫폼")
                .enabled(false)
                .assignable(false)
                .build();

        CiType disabledChild = CiType.builder()
                .id(11L)
                .code("DATABASE")
                .name("데이터베이스")
                .parent(disabledParent)
                .enabled(false)
                .assignable(true)
                .build();

        when(ciTypeRepository.findById(11L))
                .thenReturn(Optional.of(disabledChild));

        // when & then
        assertThatThrownBy(() -> ciTypeService.enable(11L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CiTypeErrorCode.CI_TYPE_PARENT_DISABLED);
    }
}
