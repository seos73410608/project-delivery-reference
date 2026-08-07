package com.seos.pmis.project.specification;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    /**
     * 프로젝트 코드
     */
    public static Specification<Project> projectCodeContains(String projectCode) {

        return (root, query, cb) -> {

            if (projectCode == null || projectCode.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("projectCode")),
                    "%" + projectCode.toLowerCase() + "%"
            );
        };
    }

    /**
     * 프로젝트명
     */
    public static Specification<Project> projectNameContains(String projectName) {

        return (root, query, cb) -> {

            if (projectName == null || projectName.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("projectName")),
                    "%" + projectName.toLowerCase() + "%"
            );
        };
    }

    /**
     * 고객사
     */
    public static Specification<Project> customerNameContains(String customerName) {

        return (root, query, cb) -> {

            if (customerName == null || customerName.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("customerName")),
                    "%" + customerName.toLowerCase() + "%"
            );
        };
    }

    /**
     * 프로젝트 관리자(PM)
     */
    public static Specification<Project> projectManagerContains(String projectManager) {

        return (root, query, cb) -> {

            if (projectManager == null || projectManager.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("projectManager")),
                    "%" + projectManager.toLowerCase() + "%"
            );
        };
    }

    /**
     * 프로젝트 상태
     */
    public static Specification<Project> hasStatus(ProjectStatus status) {

        return (root, query, cb) -> {

            if (status == null) {
                return null;
            }

            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * 프로젝트 우선순위
     */
    public static Specification<Project> hasPriority(ProjectPriority priority) {

        return (root, query, cb) -> {

            if (priority == null) {
                return null;
            }

            return cb.equal(root.get("priority"), priority);
        };
    }

    /**
     * 시작일 From
     */
    public static Specification<Project> startDateFrom(LocalDate startDateFrom) {

        return (root, query, cb) -> {

            if (startDateFrom == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(
                    root.get("startDate"),
                    startDateFrom
            );
        };
    }

    /**
     * 시작일 To
     */
    public static Specification<Project> startDateTo(LocalDate startDateTo) {

        return (root, query, cb) -> {

            if (startDateTo == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(
                    root.get("startDate"),
                    startDateTo
            );
        };
    }

    /**
     * 종료일 From
     */
    public static Specification<Project> endDateFrom(LocalDate endDateFrom) {

        return (root, query, cb) -> {

            if (endDateFrom == null) {
                return null;
            }

            return cb.greaterThanOrEqualTo(
                    root.get("endDate"),
                    endDateFrom
            );
        };
    }

    /**
     * 종료일 To
     */
    public static Specification<Project> endDateTo(LocalDate endDateTo) {

        return (root, query, cb) -> {

            if (endDateTo == null) {
                return null;
            }

            return cb.lessThanOrEqualTo(
                    root.get("endDate"),
                    endDateTo
            );
        };
    }

}