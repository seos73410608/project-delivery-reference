
package com.seos.pmis.requirement.service;

import com.seos.common.exception.BusinessException;
import com.seos.common.exception.code.CommonErrorCode;
import com.seos.pmis.requirement.dto.request.OperationalScopeSearchRequest;
import com.seos.pmis.requirement.dto.response.OperationalScopeResponse;
import com.seos.pmis.requirement.entity.OperationalScope;
import com.seos.pmis.requirement.exception.OperationalScopeErrorCode;
import com.seos.pmis.requirement.mapper.OperationalScopeMapper;
import com.seos.pmis.requirement.repository.OperationalScopeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationalScopeQueryServiceTest {

    @Mock
    private OperationalScopeRepository operationalScopeRepository;

    @Mock
    private OperationalScopeMapper operationalScopeMapper;

    private OperationalScopeQueryService operationalScopeQueryService;

    @BeforeEach
    void setUp() {
        operationalScopeQueryService =
                new OperationalScopeQueryService(
                        operationalScopeRepository,
                        operationalScopeMapper
                );
    }

    /**
     * Operational Scope 검색 결과 및 페이징 처리
     */
    @Test
    void search_returnsOperationalScopePageSuccessfully() {
        // given
        OperationalScopeSearchRequest request =
                new OperationalScopeSearchRequest();

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("scopeCode").ascending()
        );

        OperationalScope scope = mock(OperationalScope.class);

        OperationalScopeResponse response =
                OperationalScopeResponse.builder()
                        .id(1L)
                        .scopeCode("OPS-000001")
                        .scopeName("Operations Database")
                        .build();

        Page<OperationalScope> scopePage = new PageImpl<>(
                List.of(scope),
                pageable,
                1
        );

        when(operationalScopeRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<OperationalScope>>any(),
                eq(pageable)
        )).thenReturn(scopePage);

        when(operationalScopeMapper.toResponse(scope))
                .thenReturn(response);

        // when
        Page<OperationalScopeResponse> result =
                operationalScopeQueryService.search(request, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getId())
                .isEqualTo(1L);

        assertThat(result.getContent().get(0).getScopeCode())
                .isEqualTo("OPS-000001");

        assertThat(result.getContent().get(0).getScopeName())
                .isEqualTo("Operations Database");

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);

        verify(operationalScopeRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<OperationalScope>>any(),
                eq(pageable)
        );

        verify(operationalScopeMapper).toResponse(scope);
    }

    /**
     * 허용된 정렬 컬럼 처리
     */
    @Test
    void search_acceptsAllowedSortProperty() {
        // given
        OperationalScopeSearchRequest request =
                new OperationalScopeSearchRequest();

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("createdAt").descending()
        );

        when(operationalScopeRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<OperationalScope>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(
                List.of(),
                pageable,
                0
        ));

        // when
        Page<OperationalScopeResponse> result =
                operationalScopeQueryService.search(request, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(operationalScopeRepository).findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<OperationalScope>>any(),
                eq(pageable)
        );

        verifyNoInteractions(operationalScopeMapper);
    }

    /**
     * 허용되지 않은 정렬 컬럼 차단
     */
    @Test
    void searchThrowsExceptionWhenSortPropertyIsNotAllowed() {
        // given
        OperationalScopeSearchRequest request =
                new OperationalScopeSearchRequest();

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("password").ascending()
        );

        // when & then
        assertThatThrownBy(
                () -> operationalScopeQueryService.search(request, pageable)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);

        verifyNoInteractions(operationalScopeRepository);
        verifyNoInteractions(operationalScopeMapper);
    }

    /**
     * Operational Scope 상세 조회 성공
     */
    @Test
    void getDetail_returnsOperationalScopeSuccessfully() {
        // given
        Long id = 1L;

        OperationalScope scope = mock(OperationalScope.class);

        OperationalScopeResponse response =
                OperationalScopeResponse.builder()
                        .id(id)
                        .scopeCode("OPS-000001")
                        .scopeName("Operations Database")
                        .build();

        when(operationalScopeRepository.findById(id))
                .thenReturn(Optional.of(scope));

        when(operationalScopeMapper.toResponse(scope))
                .thenReturn(response);

        // when
        OperationalScopeResponse result =
                operationalScopeQueryService.getDetail(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getScopeCode()).isEqualTo("OPS-000001");
        assertThat(result.getScopeName()).isEqualTo("Operations Database");

        verify(operationalScopeRepository).findById(id);
        verify(operationalScopeMapper).toResponse(scope);
    }

    /**
     * 존재하지 않는 Operational Scope 상세 조회 차단
     */
    @Test
    void getDetail_throwsExceptionWhenOperationalScopeNotFound() {
        // given
        Long id = 999L;

        when(operationalScopeRepository.findById(id))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> operationalScopeQueryService.getDetail(id)
        )
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(OperationalScopeErrorCode.OPERATIONAL_SCOPE_NOT_FOUND);

        verify(operationalScopeRepository).findById(id);
        verifyNoInteractions(operationalScopeMapper);
    }
}
