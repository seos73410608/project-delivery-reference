
package com.seos.pmis.evidence.entity;

import com.seos.pmis.common.entity.BaseEntity;
import com.seos.pmis.evidence.enums.VerificationStatus;
import com.seos.pmis.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "evidence_verification_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvidenceVerificationHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evidence_version_id", nullable = false)
    private EvidenceVersion evidenceVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    private VerificationStatus verificationStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verified_by", nullable = false)
    private User verifiedBy;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @Lob
    @Column(name = "remark")
    private String remark;

    public static EvidenceVerificationHistory create(
            EvidenceVersion version,
            VerificationStatus status,
            User verifiedBy,
            LocalDateTime verifiedAt,
            String remark
    ) {
        EvidenceVerificationHistory history =
                new EvidenceVerificationHistory();

        history.evidenceVersion = version;
        history.verificationStatus = status;
        history.verifiedBy = verifiedBy;
        history.verifiedAt = verifiedAt;
        history.remark = remark;

        return history;
    }
}
