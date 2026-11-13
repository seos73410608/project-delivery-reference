
package com.seos.cmdb.server.entity;

import com.seos.cmdb.ci.entity.ConfigurationItem;
import com.seos.common.entity.BaseEntity;
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
        name = "server_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_server_details_ci_id",
                        columnNames = "ci_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerDetail extends BaseEntity {

    /**
     * 서버 상세정보 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연결된 공통 CI
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ci_id",
            nullable = false,
            unique = true
    )
    private ConfigurationItem configurationItem;

    /**
     * 호스트명
     */
    @Column(nullable = false, length = 200)
    private String hostname;

    /**
     * 서버 구분
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "server_category", nullable = false, length = 30)
    private ServerCategory serverCategory;

    /**
     * 관리 IP
     */
    @Column(name = "management_ip", length = 100)
    private String managementIp;

    /**
     * 운영체제 이름
     */
    @Column(name = "os_name", length = 100)
    private String osName;

    /**
     * 운영체제 버전
     */
    @Column(name = "os_version", length = 100)
    private String osVersion;

    /**
     * CPU 코어 수
     */
    @Column(name = "cpu_cores")
    private Integer cpuCores;

    /**
     * 메모리 크기(MB)
     */
    @Column(name = "memory_size_mb")
    private Long memorySizeMb;

    /**
     * 가상화 플랫폼
     */
    @Column(name = "virtualization_platform", length = 100)
    private String virtualizationPlatform;
}
