
package com.seos.cmdb.ci.entity;

import com.seos.common.entity.BaseEntity;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(
        name = "configuration_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_configuration_items_ci_code",
                        columnNames = "ci_code"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationItem extends BaseEntity {

    /**
     * CI PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * CI 고유 식별 코드
     * PostgreSQL Sequence 기반 자동 생성
     */
    @Column(name = "ci_code", nullable = false, length = 50, updatable = false)
    private String ciCode;

    /**
     * CI 이름
     */
    @Column(name = "ci_name", nullable = false, length = 200)
    private String ciName;

    /**
     * CI 유형
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ci_type_id", nullable = false)
    private CiType ciType;

    /**
     * CI 생명주기 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private CiStatus status = CiStatus.PLANNED;

    /**
     * 운영 환경
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CiEnvironment environment;

    /**
     * CI 담당자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    /**
     * 설치 위치 또는 관리 위치
     */
    @Column(length = 200)
    private String location;

    /**
     * CI 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * CI 기본 정보 수정
     */
    public void update(
            String ciName,
            CiType ciType,
            CiEnvironment environment,
            User manager,
            String location,
            String description
    ) {
        this.ciName = ciName;
        this.ciType = ciType;
        this.environment = environment;
        this.manager = manager;
        this.location = location;
        this.description = description;
    }

    /**
     * CI 상태 변경
     */
    public void changeStatus(CiStatus status) {
        this.status = status;
    }
}
