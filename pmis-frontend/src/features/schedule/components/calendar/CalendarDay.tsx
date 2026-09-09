import type {
  ScheduleResponse,
} from '../../types/schedule';

import {
  isSameDate,
} from '../../utils/calendarUtils';

import CalendarEvent
  from './CalendarEvent';


interface CalendarDayProps {

  date: Date;

  schedules:
    ScheduleResponse[];

  onSelect?: (
    schedule: ScheduleResponse,
  ) => void;

}


/**
 * =====================================================
 * Calendar Day
 * =====================================================
 *
 * Schedule Calendar의
 * 하루 Cell을 표시한다.
 *
 * - Date
 * - Today
 * - Sunday
 * - Saturday
 * - Schedule List
 */
const CalendarDay = (
  {
    date,
    schedules,
    onSelect,
  }: CalendarDayProps,
) => {


  /**
   * =====================================================
   * Today
   * =====================================================
   */

  const today =
    new Date();


  const isToday =
    isSameDate(
      date,
      today,
    );


  /**
   * =====================================================
   * Day Of Week
   * =====================================================
   */

  const dayOfWeek =
    date.getDay();


  /**
   * =====================================================
   * Render
   * =====================================================
   */

  return (

    <div
      className={[
        'schedule-calendar__day',

        isToday
          ? 'schedule-calendar__day--today'
          : '',

        dayOfWeek === 0
          ? 'schedule-calendar__day--sunday'
          : '',

        dayOfWeek === 6
          ? 'schedule-calendar__day--saturday'
          : '',
      ]
        .filter(Boolean)
        .join(' ')}
    >


      {/* ================================================
          Day Header
          ================================================ */}

      <div
        className="schedule-calendar__day-header"
      >

        <span
          className="schedule-calendar__day-number"
        >
          {date.getDate()}
        </span>

      </div>


      {/* ================================================
          Schedule List
          ================================================ */}

      <div
        className="schedule-calendar__schedule-list"
      >

        {schedules.map(
          (
            schedule,
          ) => (

            <CalendarEvent
              key={
                `${schedule.id}-${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`
              }
              date={
                date
              }
              schedule={
                schedule
              }
              onSelect={
                onSelect
              }
            />

          ),
        )}

      </div>

    </div>

  );

};


export default CalendarDay;