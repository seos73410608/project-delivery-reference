import { useCallback, useEffect, useState } from 'react';

import {
  createSchedule,
  deleteSchedule,
  getProjectSchedules,
  getSchedule,
  updateSchedule,
} from './api/scheduleApi';

import ScheduleDetail from './components/ScheduleDetail';
import ScheduleForm from './components/ScheduleForm';
import ScheduleList from './components/ScheduleList';
import ScheduleToolbar from './components/ScheduleToolbar';

import type {
  ScheduleCreateRequest,
  ScheduleResponse,
  ScheduleStatus,
  ScheduleUpdateRequest,
} from './types/schedule';

/**
 * 현재 선택된 프로젝트 ID
 *
 * TODO:
 * 향후 Project Context / Route Parameter 등
 * 실제 프로젝트 선택 구조에 맞게 변경한다.
 */
const PROJECT_ID = 2;

/**
 * Schedule Page
 *
 * Schedule Domain의 전체 화면을 담당한다.
 *
 * 주요 책임:
 * - 프로젝트 Schedule 목록 조회
 * - Schedule 검색
 * - Schedule 상세 조회
 * - Schedule 생성
 * - Schedule 수정
 * - Schedule 삭제
 * - Schedule 선택 상태 관리
 * - Schedule Form 상태 관리
 */
const SchedulePage = () => {
  const [schedules, setSchedules] = useState<
    ScheduleResponse[]
  >([]);

  const [selectedSchedule, setSelectedSchedule] =
    useState<ScheduleResponse | null>(null);

  const [editingSchedule, setEditingSchedule] =
    useState<ScheduleResponse | null>(null);

  const [isCreating, setIsCreating] =
    useState(false);

  const [keyword, setKeyword] =
    useState('');

  const [status, setStatus] =
    useState<ScheduleStatus | ''>('');

  const [wbsId, setWbsId] =
    useState<number | ''>('');

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState('');

  /**
   * 프로젝트 Schedule 목록 조회
   *
   * GET /api/projects/{projectId}/schedules
   */
  const loadSchedules = useCallback(
    async () => {
      setLoading(true);
      setError('');

      try {
        const data =
          await getProjectSchedules(PROJECT_ID);

        setSchedules(data);
      } catch (err) {
        console.error(
          'Failed to load schedules.',
          err,
        );

        setError(
          'Schedule 목록을 불러오지 못했습니다.',
        );
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  /**
   * 최초 Schedule 목록 조회
   */
  useEffect(() => {
    void loadSchedules();
  }, [loadSchedules]);

  /**
   * Schedule 검색
   *
   * 현재 프로젝트 Schedule 전체 목록을 조회한 후
   * Frontend에서 검색 조건을 적용한다.
   *
   * TODO:
   * Backend 검색 API 연동 시
   * searchSchedules() 방식으로 변경한다.
   */
  const handleSearch = async () => {
    setLoading(true);
    setError('');

    try {
      const data =
        await getProjectSchedules(PROJECT_ID);

      const filtered = data.filter(
        (schedule: ScheduleResponse) => {
          const normalizedKeyword =
            keyword.trim().toLowerCase();

          const matchesKeyword =
            !normalizedKeyword ||
            schedule.scheduleName
              .toLowerCase()
              .includes(normalizedKeyword);

          const matchesStatus =
            !status ||
            schedule.status === status;

          const matchesWbs =
            wbsId === '' ||
            schedule.wbsId === wbsId;

          return (
            matchesKeyword &&
            matchesStatus &&
            matchesWbs
          );
        },
      );

      setSchedules(filtered);
      setSelectedSchedule(null);
    } catch (err) {
      console.error(
        'Failed to search schedules.',
        err,
      );

      setError(
        'Schedule 검색에 실패했습니다.',
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Schedule 상세 조회
   *
   * GET /api/schedules/{id}
   */
  const handleSelect = async (
    schedule: ScheduleResponse,
  ) => {
    setError('');

    try {
      const data =
        await getSchedule(schedule.id);

      setSelectedSchedule(data);
    } catch (err) {
      console.error(
        'Failed to load schedule detail.',
        err,
      );

      setError(
        'Schedule 상세 정보를 불러오지 못했습니다.',
      );
    }
  };

  /**
   * Schedule 생성 화면 열기
   */
  const handleCreateOpen = () => {
    setSelectedSchedule(null);
    setEditingSchedule(null);
    setIsCreating(true);
    setError('');
  };

  /**
   * Schedule 생성
   *
   * POST /api/projects/{projectId}/schedules
   */
  const handleCreate = async (
    request: ScheduleCreateRequest,
  ) => {
    setLoading(true);
    setError('');

    try {
      const created =
        await createSchedule(
          PROJECT_ID,
          request,
        );

      setSchedules((current) => [
        ...current,
        created,
      ]);

      setSelectedSchedule(created);
      setIsCreating(false);
    } catch (err) {
      console.error(
        'Failed to create schedule.',
        err,
      );

      setError(
        'Schedule 생성에 실패했습니다.',
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Schedule 수정 화면 열기
   */
  const handleEdit = (
    schedule: ScheduleResponse,
  ) => {
    setSelectedSchedule(schedule);
    setEditingSchedule(schedule);
    setIsCreating(false);
    setError('');
  };

  /**
   * Schedule 수정
   *
   * PUT /api/schedules/{id}
   */
  const handleUpdate = async (
    scheduleId: number,
    request: ScheduleUpdateRequest,
  ) => {
    setLoading(true);
    setError('');

    try {
      const updated =
        await updateSchedule(
          scheduleId,
          request,
        );

      setSchedules((current) =>
        current.map((schedule) =>
          schedule.id === updated.id
            ? updated
            : schedule,
        ),
      );

      setSelectedSchedule(updated);
      setEditingSchedule(null);
    } catch (err) {
      console.error(
        'Failed to update schedule.',
        err,
      );

      setError(
        'Schedule 수정에 실패했습니다.',
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Schedule 삭제
   *
   * ScheduleList에서는 Schedule ID를 전달받는다.
   */
  const handleDelete = async (
    scheduleId: number,
  ) => {
    const schedule =
      schedules.find(
        (item) =>
          item.id === scheduleId,
      );

    if (!schedule) {
      setError(
        '삭제할 Schedule을 찾을 수 없습니다.',
      );
      return;
    }

    const confirmed =
      window.confirm(
        `"${schedule.scheduleName}" 일정을 삭제하시겠습니까?`,
      );

    if (!confirmed) {
      return;
    }

    setLoading(true);
    setError('');

    try {
      await deleteSchedule(scheduleId);

      setSchedules((current) =>
        current.filter(
          (item) =>
            item.id !== scheduleId,
        ),
      );

      setSelectedSchedule(null);
      setEditingSchedule(null);
      setIsCreating(false);
    } catch (err) {
      console.error(
        'Failed to delete schedule.',
        err,
      );

      setError(
        'Schedule 삭제에 실패했습니다.',
      );
    } finally {
      setLoading(false);
    }
  };

  /**
   * Form 닫기
   */
  const handleFormClose = () => {
    setIsCreating(false);
    setEditingSchedule(null);
    setError('');
  };

  return (
    <div className="schedule-page">
      <header>
        <h1>Schedule</h1>

        <p>
          프로젝트 일정 관리
        </p>
      </header>

      {/* 검색 / 필터 */}
      <ScheduleToolbar
        keyword={keyword}
        status={status}
        wbsId={wbsId}
        onKeywordChange={setKeyword}
        onStatusChange={setStatus}
        onWbsIdChange={setWbsId}
        onSearch={handleSearch}
        onCreate={handleCreateOpen}
      />

      {/* 오류 메시지 */}
      {error && (
        <p role="alert">
          {error}
        </p>
      )}

      {/* Loading */}
      {loading && (
        <p>
          Loading...
        </p>
      )}

      {/* Schedule 목록 */}
      {!loading && (
        <ScheduleList
          schedules={schedules}
          onSelect={handleSelect}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}

      {/* Schedule 상세 */}
      <ScheduleDetail
        schedule={selectedSchedule}
        onEdit={handleEdit}
        onDelete={(schedule) =>
          handleDelete(schedule.id)
        }
      />

      {/* Schedule 생성 / 수정 */}
      {(isCreating || editingSchedule) && (
        <ScheduleForm
          projectId={PROJECT_ID}
          schedule={editingSchedule}
          onSubmit={async (request) => {
            if (editingSchedule) {
              await handleUpdate(
                editingSchedule.id,
                request as ScheduleUpdateRequest,
              );
              return;
            }

            await handleCreate(
              request as ScheduleCreateRequest,
            );
          }}
          onCancel={handleFormClose}
        />
      )}
    </div>
  );
};

export default SchedulePage;