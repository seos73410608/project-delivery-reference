import type {
  ScheduleResponse,
} from '../../types/schedule';

import {
  formatDate,
  isDateInRange,
  parseDate,
} from '../../utils/calendarUtils';

import CalendarDay
  from './CalendarDay';


interface CalendarGridProps {

  calendarDays:
    Array<
      Date | null
    >;

  schedules:
    ScheduleResponse[];

  onSelect?: (
    schedule: ScheduleResponse,
  ) => void;

}


const DAYS_OF_WEEK = [
  'Sun',
  'Mon',
  'Tue',
  'Wed',
  'Thu',
  'Fri',
  'Sat',
];


/**
 * =====================================================
 * Calendar Grid
 * =====================================================
 *
 * Schedule Calendar Grid를 표시한다.
 *
 * - Week Header
 * - Calendar Day
 * - Empty Day
 */
const CalendarGrid = (
  {
    calendarDays,
    schedules,
    onSelect,
  }: CalendarGridProps,
) => {


  /**
   * =====================================================
   * Render Utility
   * =====================================================
   */


  /**
   * 특정 날짜에 포함되는
   * Schedule 목록을 조회한다.
   */
  const getSchedulesByDate =
    (
      date: Date,
    ): ScheduleResponse[] => {

      return schedules.filter(
        (
          schedule,
        ) => {

          const startDate =
            parseDate(
              schedule.startDate,
            );


          const endDate =
            parseDate(
              schedule.endDate,
            );


          return isDateInRange(
            date,
            startDate,
            endDate,
          );

        },
      );

    };


  /**
   * =====================================================
   * Render
   * =====================================================
   */
  return (

    <>


      {/* ================================================
          Week Header
          ================================================ */}

      <div
        className="schedule-calendar__week"
      >

        {DAYS_OF_WEEK.map(
          (
            day,
            index,
          ) => (

            <div
              key={day}
              className={[
                'schedule-calendar__week-day',

                index === 0
                  ? 'schedule-calendar__week-day--sunday'
                  : '',

                index === 6
                  ? 'schedule-calendar__week-day--saturday'
                  : '',
              ]
                .filter(Boolean)
                .join(' ')}
            >

              {day}

            </div>

          ),
        )}

      </div>


      {/* ================================================
          Calendar Days
          ================================================ */}

      <div
        className="schedule-calendar__grid"
      >

        {calendarDays.map(
          (
            date,
            index,
          ) => {


            /**
             * Empty Day
             */
            if (!date) {

              return (

                <div
                  key={
                    `empty-${index}`
                  }
                  className="schedule-calendar__day schedule-calendar__day--empty"
                />

              );

            }


            /**
             * 해당 날짜 Schedule
             */
            const daySchedules =
              getSchedulesByDate(
                date,
              );


            return (

              <CalendarDay
                key={
                  formatDate(
                    date,
                  )
                }
                date={
                  date
                }
                schedules={
                  daySchedules
                }
                onSelect={
                  onSelect
                }
              />

            );

          },
        )}

      </div>

    </>

  );

};


export default CalendarGrid;