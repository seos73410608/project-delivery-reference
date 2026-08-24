import type { Project } from '../types/project';

import './ProjectList.css';

interface ProjectListProps {
  projects: Project[];
  loading: boolean;
}

function ProjectList({
  projects,
  loading,
}: ProjectListProps) {
  if (loading) {
    return (
      <section className="project-list">
        <div className="project-list__loading">
          <div className="project-list__spinner" />
          <span>
            프로젝트를 불러오는 중입니다...
          </span>
        </div>
      </section>
    );
  }

  if (projects.length === 0) {
    return (
      <section className="project-list">
        <div className="project-list__empty">
          <div className="project-list__empty-icon">
            PRJ
          </div>

          <strong>
            조회된 프로젝트가 없습니다.
          </strong>

          <span>
            검색 조건에 해당하는 프로젝트가 없습니다.
          </span>
        </div>
      </section>
    );
  }

  return (
    <section className="project-list">
      {/* Header */}
      <div className="project-list__header">
        <div>
          <h2>Project List</h2>
          <span>프로젝트 목록</span>
        </div>

        <div className="project-list__count">
          <span>Projects</span>
          <strong>{projects.length}</strong>
        </div>
      </div>

      {/* Table */}
      <div className="project-list__table-wrapper">
        <table className="project-list__table">
          <thead>
            <tr>
              <th>프로젝트 코드</th>
              <th>프로젝트명</th>
              <th>고객사</th>
              <th>PM</th>
              <th>상태</th>
              <th>우선순위</th>
              <th>시작일</th>
              <th>종료일</th>
            </tr>
          </thead>

          <tbody>
            {projects.map((project) => (
              <tr key={project.id}>
                {/* Project Code */}
                <td>
                  <span className="project-list__code">
                    {project.projectCode}
                  </span>
                </td>

                {/* Project Name */}
                <td>
                  <button
                    type="button"
                    className="project-list__name"
                    onClick={() =>
                      console.log(
                        '프로젝트 상세:',
                        project.id,
                      )
                    }
                  >
                    {project.projectName}
                  </button>
                </td>

                {/* Customer */}
                <td>
                  <span className="project-list__customer">
                    {project.customerName}
                  </span>
                </td>

                {/* PM */}
                <td>
                  <span className="project-list__manager">
                    {project.projectManager}
                  </span>
                </td>

                {/* Status */}
                <td>
                  <ProjectStatusBadge
                    status={project.status}
                  />
                </td>

                {/* Priority */}
                <td>
                  <ProjectPriorityBadge
                    priority={project.priority}
                  />
                </td>

                {/* Start Date */}
                <td>
                  <span className="project-list__date">
                    {project.startDate}
                  </span>
                </td>

                {/* End Date */}
                <td>
                  <span className="project-list__date">
                    {project.endDate}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

/**
 * 프로젝트 상태 Badge
 */
function ProjectStatusBadge({
  status,
}: {
  status: Project['status'];
}) {
  const statusLabel: Record<
    Project['status'],
    string
  > = {
    PLANNING: '계획',
    IN_PROGRESS: '진행중',
    ON_HOLD: '보류',
    COMPLETED: '완료',
    CANCELLED: '취소',
  };

  return (
    <span
      className={`project-list__badge project-list__badge--${status.toLowerCase()}`}
    >
      <span className="project-list__badge-dot" />
      {statusLabel[status]}
    </span>
  );
}

/**
 * 프로젝트 우선순위 Badge
 */
function ProjectPriorityBadge({
  priority,
}: {
  priority: Project['priority'];
}) {
  const priorityLabel: Record<
    Project['priority'],
    string
  > = {
    LOW: '낮음',
    MEDIUM: '보통',
    HIGH: '높음',
    CRITICAL: '긴급',
  };

  return (
    <span
      className={`project-list__badge project-list__badge--priority-${priority.toLowerCase()}`}
    >
      {priorityLabel[priority]}
    </span>
  );
}

export default ProjectList;