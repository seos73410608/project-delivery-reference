package com.seos.pmis.wbs.dto.response;

import com.seos.pmis.wbs.entity.Wbs;
import com.seos.pmis.wbs.entity.WbsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**

* WBS Tree 응답
*
* WBS의 계층 구조를 표현하기 위한 응답 DTO이다.
*
* parent-child 관계를 children 목록으로 표현하며,
* 하위 WBS가 없는 경우 children은 빈 목록을 반환한다.
  */
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public class WbsTreeResponse {

  /**

  * WBS PK
    */
    private Long id;

  /**

  * 프로젝트 ID
    */
    private Long projectId;

  /**

  * 상위 WBS ID
  *
  * 최상위 WBS인 경우 null이다.
    */
    private Long parentId;

  /**

  * WBS 코드
  *
  * 예:
  * 1
  * 1.1
  * 1.1.1
    */
    private String wbsCode;

  /**

  * WBS 명
    */
    private String wbsName;

  /**

  * WBS Level
    */
    private Integer level;

  /**

  * 동일 부모 내 표시 순서
    */
    private Integer sortOrder;

  /**

  * WBS 상태
    */
    private WbsStatus status;

  /**

  * 설명
    */
    private String description;

  /**

  * 하위 WBS
  *
  * 계층 구조를 재귀적으로 표현한다.
    */
    @Builder.Default
    private List<WbsTreeResponse> children = new ArrayList<>();

  /**

  * Wbs Entity → WbsTreeResponse 변환
  *
  * 단일 WBS를 Tree Node 형태로 변환한다.
  *
  * @param wbs WBS Entity
  * @return WBS Tree Response
    */
    public static WbsTreeResponse from(Wbs wbs) {

    return WbsTreeResponse.builder()
    .id(wbs.getId())
    .projectId(
    wbs.getProject() != null
    ? wbs.getProject().getId()
    : null
    )
    .parentId(
    wbs.getParent() != null
    ? wbs.getParent().getId()
    : null
    )
    .wbsCode(wbs.getWbsCode())
    .wbsName(wbs.getWbsName())
    .level(wbs.getLevel())
    .sortOrder(wbs.getSortOrder())
    .status(wbs.getStatus())
    .description(wbs.getDescription())
    .build();
    }

  /**

  * 하위 WBS 추가
  *
  * @param child 하위 WBS Tree Response
    */
    public void addChild(WbsTreeResponse child) {
    this.children.add(child);
    }

  /**

  * 하위 WBS 목록 설정
  *
  * @param children 하위 WBS 목록
    */
    public void setChildren(List<WbsTreeResponse> children) {
    this.children = children != null
    ? children
    : new ArrayList<>();
    }
    }
