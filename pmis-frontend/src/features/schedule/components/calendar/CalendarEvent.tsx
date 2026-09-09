import type {
  ScheduleResponse,
} from '../../types/schedule';

import {
  isSameDate,
  parseDate,
} from '../../utils/calendarUtils';


interface CalendarEventProps {

  date: Date;

  schedule: ScheduleResponse;

  onSelect?: (
    schedule: ScheduleResponse,
  ) => void;

}


/**
 * =====================================================
 * Calendar Event
 * =====================================================
 *
 * Calendar의 개별 Schedule을 표시한다.
 *
 * - Schedule Name
 * - Schedule Status
 * - Start Date
 * - End Date
 * - Multi Day Schedule
 */
const CalendarEvent = (
  {
    date,
    schedule,
    onSelect,
  }: CalendarEventProps,
) => {


  /**
   * =====================================================
   * Schedule Date
   * =====================================================
   */


  /**
   * Schedule 시작일
   */
  const startDate =
    parseDate(
      schedule.startDate,
    );


  /**
   * Schedule 종료일
   */
  const endDate =
    parseDate(
      schedule.endDate,
    );


  /**
   * =====================================================
   * Schedule Position
   * =====================================================
   */


  /**
   * 시작 날짜인지 확인한다.
   */
  const isStartDate =
    isSameDate(
      date,
      startDate,
    );


  /**
   * 종료 날짜인지 확인한다.
   */
  const isEndDate =
    isSameDate(
      date,
      endDate,
    );


  /**
   * =====================================================
   * Render
   * =====================================================
   */
  return (

    <button
      type="button"
      className={[
        'schedule-calendar__schedule',

        `schedule-calendar__schedule--${schedule.status.toLowerCase()}`,

        isStartDate
          ? 'schedule-calendar__schedule--start'
          : '',

        isEndDate
          ? 'schedule-calendar__schedule--end'
          : '',
      ]
        .filter(Boolean)
        .join(' ')}
      onClick={
        () =>
          onSelect?.(
            schedule,
          )
      }
      title={
        `${schedule.scheduleName}
${schedule.startDate} ~ ${schedule.endDate}`
      }
    >

      <span
        className="schedule-calendar__schedule-name"
      >
        {schedule.scheduleName}
      </span>

    </button>

  );

};


export default CalendarEvent;