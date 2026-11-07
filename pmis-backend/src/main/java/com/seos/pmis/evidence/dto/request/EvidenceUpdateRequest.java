package com.seos.pmis.evidence.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Evidence 관리 정보 수정 요청
 *
 * 일반 수정에서는 WBS 연결 정보만 변경한다.
 * 제출물 내용 변경은 EvidenceVersion 생성 API를 사용한다.
 */
@Getter
@NoArgsConstructor
public class EvidenceUpdateRequest {

        /**
         * 연결할 WBS ID
         *
         * null이면 기존 WBS 연결을 해제한다.
         */
        private Long wbsId;
}