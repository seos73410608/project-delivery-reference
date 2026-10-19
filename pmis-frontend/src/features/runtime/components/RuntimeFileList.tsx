
import { useEffect, useState } from 'react';
import {
  downloadRuntimeFile,
  getExecutionFiles,
} from '../api/runtimeFileApi';
import type { RuntimeFile } from '../api/runtimeFileApi';
import './RuntimeFileList.css';

interface RuntimeFileListProps {
  executionUuid: string;
}

export default function RuntimeFileList({
  executionUuid,
}: RuntimeFileListProps) {
  const [files, setFiles] = useState<RuntimeFile[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [downloadingFileId, setDownloadingFileId] = useState<string | null>(
    null,
  );

  useEffect(() => {
    let cancelled = false;

    async function fetchFiles() {
      setLoading(true);
      setError(null);

      try {
        const result = await getExecutionFiles(executionUuid);

        if (!cancelled) {
          setFiles(result);
        }
      } catch (err) {
        if (!cancelled) {
          setFiles([]);
          setError(
            err instanceof Error
              ? err.message
              : '실행 결과 파일 목록을 조회하지 못했습니다.',
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    if (!executionUuid.trim()) {
      setFiles([]);
      setError('실행 UUID가 필요합니다.');
      setLoading(false);
      return;
    }

    void fetchFiles();

    return () => {
      cancelled = true;
    };
  }, [executionUuid]);

  async function handleDownload(file: RuntimeFile) {
    setDownloadingFileId(file.file_id);
    setError(null);

    try {
      const blob = await downloadRuntimeFile(file.file_id);
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');

      link.href = url;
      link.download = `${file.vendor_name}.${file.file_type}`;

      document.body.appendChild(link);
      link.click();
      link.remove();

      // 브라우저가 다운로드를 시작할 시간을 확보한 뒤 URL 해제
      window.setTimeout(() => URL.revokeObjectURL(url), 1000);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : '파일 다운로드에 실패했습니다.',
      );
    } finally {
      setDownloadingFileId(null);
    }
  }

  if (loading) {
    return (
      <section className="card runtime-file-list">
        <div className="card__body">
          <div className="state state--loading">
            <span className="spinner" aria-hidden="true" />
            <p>실행 결과 파일을 조회하고 있습니다...</p>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section className="card runtime-file-list">
      <header className="card__header">
        <div className="card__header-content">
          <h2 className="card__title">파일 목록</h2>
          <p className="card__description">
            실행 결과로 생성된 파일입니다.
          </p>
        </div>

        <div className="card__meta">
          총 <strong>{files.length}</strong>개
        </div>
      </header>

      <div className="card__body">
        {error && (
          <div className="state state--error" role="alert">
            {error}
          </div>
        )}

        {files.length === 0 && !error ? (
          <div className="state state--empty">
            조회된 실행 결과 파일이 없습니다.
          </div>
        ) : (
          <div className="runtime-file-list__table-wrapper">
            <table className="runtime-file-list__table">
              <thead>
                <tr>
                  <th scope="col">업체명</th>
                  <th scope="col">Job ID</th>
                  <th scope="col">파일 형식</th>
                  <th scope="col">생성 일시</th>
                  <th scope="col">작업</th>
                </tr>
              </thead>

              <tbody>
                {files.map((file) => (
                  <tr key={file.file_id}>
                    <td>{file.vendor_name}</td>
                    <td>{file.job_id}</td>
                    <td>
                      <span className="runtime-file-list__type">
                        {file.file_type.toUpperCase()}
                      </span>
                    </td>
                    <td>
                      {new Date(file.created_at).toLocaleString('ko-KR')}
                    </td>
                    <td>
                      <button
                        type="button"
                        className="button button--secondary button--sm"
                        disabled={downloadingFileId !== null}
                        onClick={() => void handleDownload(file)}
                      >
                        {downloadingFileId === file.file_id
                          ? '다운로드 중...'
                          : '다운로드'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}
