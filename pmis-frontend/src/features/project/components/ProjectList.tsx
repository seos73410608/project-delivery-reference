import type { Project } from '../types/project';

import './ProjectList.css';


interface ProjectListProps {
  projects: Project[];
  loading: boolean;

  /**
   * 프로젝트 상세 페이지 이동
   *
   * ProjectPage에서 Route 이동을 처리한다.
   */
  onProjectClick: (
    project: Project,
  ) => void;
}


/* =========================================================
   Labels
   ========================================================= */

const STATUS_LABELS: Record<
  Project['status'],
  string
> = {
  PLANNING:
    '계획',

  IN_PROGRESS:
    '진행중',

  ON_HOLD:
    '보류',

  COMPLETED:
    '완료',

  CANCELLED:
    '취소',
};


const STATUS_BADGE_CLASSES: Record<
  Project['status'],
  string
> = {
  PLANNING:
    'badge--planned',

  IN_PROGRESS:
    'badge--in-progress',

  ON_HOLD:
    'badge--on-hold',

  COMPLETED:
    'badge--completed',

  CANCELLED:
    'badge--cancelled',
};


const PRIORITY_LABELS: Record<
  Project['priority'],
  string
> = {
  LOW:
    '낮음',

  MEDIUM:
    '보통',

  HIGH:
    '높음',

  CRITICAL:
    '긴급',
};


/* =========================================================
   Component
   ========================================================= */

function ProjectList({
  projects,
  loading,
  onProjectClick,
}: ProjectListProps) {


  /**
   * Loading
   */

  if (loading) {

    return (
      <section className="project-list">

        <div className="state state--loading project-list__loading">

          <div className="spinner" />

          <span className="state__description">
            프로젝트를 불러오는 중입니다...
          </span>

        </div>

      </section>
    );

  }


  /**
   * Empty
   */

  if (projects.length === 0) {

    return (
      <section className="project-list">

        <div className="state state--empty project-list__empty">

          <div className="state__icon">
            PRJ
          </div>


          <strong className="state__title">
            조회된 프로젝트가 없습니다.
          </strong>


          <span className="state__description">
            검색 조건에 해당하는 프로젝트가 없습니다.
          </span>

        </div>

      </section>
    );

  }


  return (
    <section className="project-list">


      {/* ========================================
          Header
          ======================================== */}

      <div className="project-list__header">

        <div>

          <h2>
            Project List
          </h2>

          <span>
            프로젝트 목록
          </span>

        </div>


        <div className="project-list__count">

          <span>
            Projects
          </span>

          <strong>
            {projects.length}
          </strong>

        </div>

      </div>


      {/* ========================================
          Table
          ======================================== */}

      <div className="project-list__table-wrapper">

        <table className="project-list__table">

          <thead>

            <tr>

              <th>
                프로젝트 코드
              </th>

              <th>
                프로젝트명
              </th>

              <th>
                고객사
              </th>

              <th>
                PM
              </th>

              <th>
                상태
              </th>

              <th>
                우선순위
              </th>

              <th>
                시작일
              </th>

              <th>
                종료일
              </th>

            </tr>

          </thead>


          <tbody>

            {projects.map((project) => (

              <tr
                key={project.id}
              >


                {/* Project Code */}

                <td>

                  <span
                    className="project-list__code"
                  >
                    {project.projectCode}
                  </span>

                </td>


                {/* Project Name */}

                <td>

                  <button
                    type="button"
                    className="button button--link project-list__name"
                    onClick={() =>
                      onProjectClick(
                        project,
                      )
                    }
                    title="프로젝트 상세 보기"
                  >
                    {project.projectName}
                  </button>

                </td>


                {/* Customer */}

                <td>

                  <span
                    className="project-list__customer"
                  >
                    {project.customerName}
                  </span>

                </td>


                {/* PM */}

                <td>

                  <span
                    className="project-list__manager"
                  >
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

                  <span
                    className="project-list__date"
                  >
                    {project.startDate}
                  </span>

                </td>


                {/* End Date */}

                <td>

                  <span
                    className="project-list__date"
                  >
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


/* =========================================================
   프로젝트 상태 Badge
   ========================================================= */

function ProjectStatusBadge({
  status,
}: {
  status: Project['status'];
}) {

  return (
    <span
      className={`badge ${STATUS_BADGE_CLASSES[status]}`}
    >
      {STATUS_LABELS[status]}
    </span>
  );
}


/* =========================================================
   프로젝트 우선순위 Badge
   ========================================================= */

function ProjectPriorityBadge({
  priority,
}: {
  priority: Project['priority'];
}) {

  return (
    <span
      className={
        `project-list__badge ` +
        `project-list__badge--priority-${priority.toLowerCase()}`
      }
    >
      {PRIORITY_LABELS[priority]}
    </span>
  );
}


export default ProjectList;