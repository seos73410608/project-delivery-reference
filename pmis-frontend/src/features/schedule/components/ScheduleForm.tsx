import { useEffect, useState } from 'react';

import type {
  ScheduleCreateRequest,
  ScheduleResponse,
  ScheduleStatus,
  ScheduleUpdateRequest,
} from '../types/schedule';

interface ScheduleFormProps {
  projectId: number;
  schedule?: ScheduleResponse | null;
  onSubmit: (
    request: ScheduleCreateRequest | ScheduleUpdateRequest,
  ) => void;
  onCancel: () => void;
}

/**
 * Schedule 생성 / 수정 Form
 *
 * Schedule 생성과 수정에 공통으로 사용하는 Form이다.
 *
 * API 호출은 상위 SchedulePage에서 처리한다.
 * Form은 사용자 입력과 기본 Validation만 담당한다.
 */
const ScheduleForm = ({
  projectId,
  schedule = null,
  onSubmit,
  onCancel,
}: ScheduleFormProps) => {
  const [wbsId, setWbsId] = useState('');
  const [scheduleName, setScheduleName] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [status, setStatus] =
    useState<ScheduleStatus>('PLANNED');
  const [description, setDescription] = useState('');
  const [sortOrder, setSortOrder] = useState('');

  const [error, setError] = useState('');

  const isEdit = Boolean(schedule);

  /**
   * 수정 모드:
   * 선택된 Schedule 데이터를 Form에 반영한다.
   *
   * 생성 모드:
   * Form을 초기화한다.
   */
  useEffect(() => {
    if (schedule) {
      setWbsId(String(schedule.wbsId));
      setScheduleName(schedule.scheduleName);
      setStartDate(schedule.startDate);
      setEndDate(schedule.endDate);
      setStatus(schedule.status);
      setDescription(schedule.description ?? '');
      setSortOrder(String(schedule.sortOrder));
      setError('');

      return;
    }

    setWbsId('');
    setScheduleName('');
    setStartDate('');
    setEndDate('');
    setStatus('PLANNED');
    setDescription('');
    setSortOrder('');
    setError('');
  }, [schedule]);

  /**
   * Schedule 생성 / 수정
   *
   * 기본적인 사용자 입력 Validation 후
   * 상위 컴포넌트의 onSubmit을 호출한다.
   */
  const handleSubmit = (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    setError('');

    if (!wbsId) {
      setError('WBS ID is required.');
      return;
    }

    if (!scheduleName.trim()) {
      setError('Schedule name is required.');
      return;
    }

    if (!startDate) {
      setError('Start date is required.');
      return;
    }

    if (!endDate) {
      setError('End date is required.');
      return;
    }

    if (startDate > endDate) {
      setError(
        'Start date must be before or equal to end date.',
      );
      return;
    }

    const parsedWbsId = Number(wbsId);

    if (!Number.isInteger(parsedWbsId) || parsedWbsId <= 0) {
      setError('WBS ID must be a valid positive number.');
      return;
    }

    let parsedSortOrder: number | undefined;

    if (sortOrder) {
      parsedSortOrder = Number(sortOrder);

      if (
        !Number.isInteger(parsedSortOrder) ||
        parsedSortOrder < 0
      ) {
        setError('Sort order must be a valid non-negative number.');
        return;
      }
    }

    const request = {
      wbsId: parsedWbsId,
      scheduleName: scheduleName.trim(),
      startDate,
      endDate,
      status,
      description: description.trim() || undefined,
      ...(parsedSortOrder !== undefined
        ? { sortOrder: parsedSortOrder }
        : {}),
    };

    onSubmit(request);
  };

  return (
    <section>
      <h2>
        {isEdit ? 'Edit Schedule' : 'Create Schedule'}
      </h2>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="schedule-project">
            Project ID
          </label>

          <input
            id="schedule-project"
            type="number"
            value={projectId}
            disabled
          />
        </div>

        <div>
          <label htmlFor="schedule-wbs">
            WBS ID
          </label>

          <input
            id="schedule-wbs"
            type="number"
            min="1"
            value={wbsId}
            onChange={(event) =>
              setWbsId(event.target.value)
            }
          />
        </div>

        <div>
          <label htmlFor="schedule-name">
            Schedule Name
          </label>

          <input
            id="schedule-name"
            type="text"
            value={scheduleName}
            onChange={(event) =>
              setScheduleName(event.target.value)
            }
          />
        </div>

        <div>
          <label htmlFor="schedule-start-date">
            Start Date
          </label>

          <input
            id="schedule-start-date"
            type="date"
            value={startDate}
            onChange={(event) =>
              setStartDate(event.target.value)
            }
          />
        </div>

        <div>
          <label htmlFor="schedule-end-date">
            End Date
          </label>

          <input
            id="schedule-end-date"
            type="date"
            value={endDate}
            onChange={(event) =>
              setEndDate(event.target.value)
            }
          />
        </div>

        <div>
          <label htmlFor="schedule-status">
            Status
          </label>

          <select
            id="schedule-status"
            value={status}
            onChange={(event) =>
              setStatus(
                event.target.value as ScheduleStatus,
              )
            }
          >
            <option value="PLANNED">
              Planned
            </option>

            <option value="IN_PROGRESS">
              In Progress
            </option>

            <option value="COMPLETED">
              Completed
            </option>

            <option value="ON_HOLD">
              On Hold
            </option>

            <option value="CANCELLED">
              Cancelled
            </option>
          </select>
        </div>

        <div>
          <label htmlFor="schedule-description">
            Description
          </label>

          <textarea
            id="schedule-description"
            value={description}
            onChange={(event) =>
              setDescription(event.target.value)
            }
          />
        </div>

        <div>
          <label htmlFor="schedule-sort-order">
            Sort Order
          </label>

          <input
            id="schedule-sort-order"
            type="number"
            min="0"
            value={sortOrder}
            onChange={(event) =>
              setSortOrder(event.target.value)
            }
          />
        </div>

        {error && (
          <p role="alert">
            {error}
          </p>
        )}

        <div>
          <button type="submit">
            {isEdit ? 'Update' : 'Create'}
          </button>

          <button
            type="button"
            onClick={onCancel}
          >
            Cancel
          </button>
        </div>
      </form>
    </section>
  );
};

export default ScheduleForm;