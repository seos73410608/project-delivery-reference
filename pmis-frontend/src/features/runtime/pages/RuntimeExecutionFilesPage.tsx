
import { Link, useParams } from 'react-router-dom';
import RuntimeFileList from '../components/RuntimeFileList';

export default function RuntimeExecutionFilesPage() {
  const { executionUuid } = useParams<{ executionUuid: string }>();

  if (!executionUuid?.trim()) {
    return (
      <main className="page">
        <div className="page__container">
          <header className="page__header">
            <div className="page__header-content">
              <div className="page__eyebrow">RUNTIME / EXECUTIONS</div>
              <h1 className="page__title">실행 결과 파일</h1>
              <p className="page__description">
                실행 UUID가 없어 결과 파일을 조회할 수 없습니다.
              </p>
            </div>
          </header>

          <div className="state state--error" role="alert">
            올바른 실행 결과 파일 URL로 접근해 주세요.
          </div>

          <div className="state__actions">
            <Link to="/" className="button button--secondary">
              대시보드로 이동
            </Link>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="page">
      <div className="page__container">
        <header className="page__header">
          <div className="page__header-content">
            <div className="page__eyebrow">RUNTIME / EXECUTIONS</div>
            <h1 className="page__title">실행 결과 파일</h1>
            <p className="page__description">
              Runtime 실행에서 생성된 결과 파일을 조회하고 다운로드합니다.
            </p>
          </div>
        </header>

        <section className="card runtime-execution-files__info">
          <div className="card__body">
            <div className="runtime-execution-files__uuid-label">
              실행 UUID
            </div>
            <code className="runtime-execution-files__uuid">
              {executionUuid}
            </code>
          </div>
        </section>

        <RuntimeFileList executionUuid={executionUuid} />
      </div>
    </main>
  );
}
