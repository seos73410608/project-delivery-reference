import type { Project } from '../types/project';

import './ProjectDetail.css';

interface ProjectDetailProps {
  project: Project;
}

const STATUS_LABELS: Record<string, string> = {
  PLANNING: '계획',
  IN_PROGRESS: '진행중',
  ON_HOLD: '보류',
  COMPLETED: '완료',
  CANCELLED: '취소',
};

const PRIORITY_LABELS: Record<string, string> = {
  LOW: '낮음',
  MEDIUM: '보통',
  HIGH: '높음',
  CRITICAL: '긴급',
};

function ProjectDetail({
  project,
}: ProjectDetailProps) {
  const statusLabel =
    STATUS_LABELS[project.status] ??
    project.status;

  const priorityLabel =
    PRIORITY_LABELS[project.priority] ??
    project.priority;

  return (
    <section className="project-detail">
      {/* ========================================
          Project Summary
          ======================================== */}

      <div className="project-detail__summary">
        <div className="project-detail__summary-main">
          <div className="project-detail__code">
            {project.projectCode}
          </div>

          <h2>{project.projectName}</h2>

          <p>
            {project.description ||
              '프로젝트 설명이 없습니다.'}
          </p>
        </div>

        <div className="project-detail__summary-status">
          <span
            className={`project-status project-status--${project.status.toLowerCase()}`}
          >
            {statusLabel}
          </span>

          <span
            className={`project-priority project-priority--${project.priority.toLowerCase()}`}
          >
            {priorityLabel}
          </span>
        </div>
      </div>

      {/* ========================================
          Project Information
          ======================================== */}

      <div className="project-detail__card">
        <div className="project-detail__card-header">
          <div>
            <span className="project-detail__section-label">
              PROJECT INFORMATION
            </span>

            <h3>프로젝트 정보</h3>
          </div>
        </div>

        <div className="project-detail__grid">
          <div className="project-detail__item">
            <span>프로젝트 코드</span>
            <strong>{project.projectCode}</strong>
          </div>

          <div className="project-detail__item">
            <span>프로젝트명</span>
            <strong>{project.projectName}</strong>
          </div>

          <div className="project-detail__item">
            <span>고객사</span>
            <strong>{project.customerName}</strong>
          </div>

          <div className="project-detail__item">
            <span>프로젝트 관리자</span>
            <strong>{project.projectManager}</strong>
          </div>

          <div className="project-detail__item">
            <span>시작일</span>
            <strong>{project.startDate}</strong>
          </div>

          <div className="project-detail__item">
            <span>종료 예정일</span>
            <strong>{project.endDate}</strong>
          </div>

          <div className="project-detail__item">
            <span>프로젝트 상태</span>

            <strong>
              <span
                className={`project-status project-status--${project.status.toLowerCase()}`}
              >
                {statusLabel}
              </span>
            </strong>
          </div>

          <div className="project-detail__item">
            <span>우선순위</span>

            <strong>
              <span
                className={`project-priority project-priority--${project.priority.toLowerCase()}`}
              >
                {priorityLabel}
              </span>
            </strong>
          </div>
        </div>
      </div>

      {/* ========================================
          Description
          ======================================== */}

      <div className="project-detail__card">
        <div className="project-detail__card-header">
          <div>
            <span className="project-detail__section-label">
              DESCRIPTION
            </span>

            <h3>프로젝트 설명</h3>
          </div>
        </div>

        <div className="project-detail__description">
          {project.description ||
            '등록된 프로젝트 설명이 없습니다.'}
        </div>
      </div>

      {/* ========================================
          Metadata
          ======================================== */}

      <div className="project-detail__metadata">
        <div>
          <span>생성일</span>
          <strong>{project.createdAt}</strong>
        </div>

        <div>
          <span>수정일</span>
          <strong>{project.updatedAt}</strong>
        </div>
      </div>
    </section>
  );
}

export default ProjectDetail;