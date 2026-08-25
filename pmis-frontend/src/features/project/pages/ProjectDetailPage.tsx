import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { getProject } from '../api/projectApi';
import ProjectDetail from '../components/ProjectDetail';
import type { Project } from '../types/project';

function ProjectDetailPage() {
  const { projectId } = useParams<{ projectId: string }>();
  const navigate = useNavigate();

  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  /**
   * 프로젝트 상세 조회
   */
  const fetchProject = useCallback(async () => {
    if (!projectId) {
      setError('프로젝트 ID가 없습니다.');
      setLoading(false);
      return;
    }

    const id = Number(projectId);

    if (Number.isNaN(id)) {
      setError('올바르지 않은 프로젝트 ID입니다.');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const response = await getProject(id);

      setProject(response);
    } catch (err) {
      console.error('프로젝트 상세 조회 실패:', err);

      setProject(null);
      setError(
        '프로젝트 정보를 불러오지 못했습니다.',
      );
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  /**
   * 최초 상세 조회
   */
  useEffect(() => {
    fetchProject();
  }, [fetchProject]);

  /**
   * 프로젝트 목록으로 이동
   */
  const handleBack = () => {
    navigate('/project');
  };

  if (loading) {
    return (
      <div className="project-detail-page">
        <div className="project-detail-page__loading">
          프로젝트 정보를 불러오는 중입니다...
        </div>
      </div>
    );
  }

  if (error || !project) {
    return (
      <div className="project-detail-page">
        <div
          className="project-detail-page__error"
          role="alert"
        >
          {error ?? '프로젝트를 찾을 수 없습니다.'}
        </div>

        <button
          type="button"
          className="project-detail-page__back-button"
          onClick={handleBack}
        >
          프로젝트 목록
        </button>
      </div>
    );
  }

  return (
    <div className="project-detail-page">
      <div className="project-detail-page__header">
        <div>
          <div className="project-detail-page__eyebrow">
            PROJECT DETAIL
          </div>

          <h1>프로젝트 상세</h1>

          <p>
            프로젝트의 상세 정보를 확인합니다.
          </p>
        </div>

        <button
          type="button"
          className="project-detail-page__back-button"
          onClick={handleBack}
        >
          ← 목록으로
        </button>
      </div>

      <ProjectDetail project={project} />
    </div>
  );
}

export default ProjectDetailPage;