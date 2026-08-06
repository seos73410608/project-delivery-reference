package com.seos.pmis.project.specification;

import com.seos.pmis.project.entity.Project;
import com.seos.pmis.project.entity.ProjectPriority;
import com.seos.pmis.project.entity.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {

    private ProjectSpecification() {
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
}