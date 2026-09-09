import {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';

import {
  getProjectScheduleCalendar,
} from '../../api/scheduleApi';

import type {
  ScheduleResponse,
} from '../../types/schedule';

import '../../styles/Calendar.css';


interface ScheduleCalendarProps {

  projectId: number;

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
 * Date Utility
 * =====================================================
 */


/**
 * Date를 YYYY-MM-DD 형식으로 변환한다.
 */
const formatDate =
  (
    date: Date,
  ): string => {

    const year =
      date.getFullYear();


    const month =
      String(
        date.getMonth() + 1,
      ).padStart(
        2,
        '0',
      );


    const day =
      String(
        date.getDate(),
      ).padStart(
        2,
        '0',
      );


    return `${year}-${month}-${day}`;

  };


/**
 * 날짜 문자열을 Local Date로 변환한다.
 *
 * new Date('YYYY-MM-DD') 사용 시
 * Timezone 문제가 발생할 수 있으므로
 * 직접 Date 객체를 생성한다.
 */
const parseDate =
  (
    value: string,
  ): Date => {

    const [
      year,
      month,
      day,
    ] =
      value
        .split('-')
        .map(Number);


    return new Date(
      year,
      month - 1,
      day,
    );

  };


/**
 * 두 날짜가 같은 날짜인지 확인한다.
 */
const isSameDate =
  (
    first: Date,
    second: Date,
  ): boolean => {

    return (
      first.getFullYear() ===
        second.getFullYear() &&

      first.getMonth() ===
        second.getMonth() &&

      first.getDate() ===
        second.getDate()
    );

  };


/**
 * Date가 두 날짜 사이에 포함되는지 확인한다.
 */
const isDateInRange =
  (
    date: Date,
    startDate: Date,
    endDate: Date,
  ): boolean => {

    const target =
      new Date(
        date.getFullYear(),
        date.getMonth(),
        date.getDate(),
      );


    const start =
      new Date(
        startDate.getFullYear(),
        startDate.getMonth(),
        startDate.getDate(),
      );


    const end =
      new Date(
        endDate.getFullYear(),
        endDate.getMonth(),
        endDate.getDate(),
      );


    return (
      target >= start &&
      target <= end
    );

  };


/**
 * =====================================================
 * Schedule Calendar
 * =====================================================
 *
 * 프로젝트 Schedule Calendar를 표시한다.
 *
 * API:
 *
 * GET
 * /api/projects/{projectId}/schedules/calendar
 *
 * Query:
 *
 * startDate
 * endDate
 */
const ScheduleCalendar = (
  {
    projectId,
    onSelect,
  }: ScheduleCalendarProps,
) => {


  /**
   * =====================================================
   * State
   * =====================================================
   */


  /**
   * 현재 Calendar 기준 월
   */
  const [
    currentDate,
    setCurrentDate,
  ] = useState(
    () => new Date(),
  );


  /**
   * Calendar Schedule 목록
   */
  const [
    schedules,
    setSchedules,
  ] = useState<
    ScheduleResponse[]
  >([]);


  /**
   * Loading 상태
   */
  const [
    loading,
    setLoading,
  ] = useState(false);


  /**
   * Error 상태
   */
  const [
    error,
    setError,
  ] = useState('');


  /**
   * =====================================================
   * Calendar Range
   * =====================================================
   */


  /**
   * 현재 월 시작일
   */
  const calendarStartDate =
    useMemo(
      () =>
        new Date(
          currentDate.getFullYear(),
          currentDate.getMonth(),
          1,
        ),
      [
        currentDate,
      ],
    );


  /**
   * 현재 월 종료일
   */
  const calendarEndDate =
    useMemo(
      () =>
        new Date(
          currentDate.getFullYear(),
          currentDate.getMonth() + 1,
          0,
        ),
      [
        currentDate,
      ],
    );


  /**
   * =====================================================
   * Calendar Grid
   * =====================================================
   */


  /**
   * 월 시작 요일
   */
  const firstDayOfWeek =
    calendarStartDate.getDay();


  /**
   * 월 마지막 날짜
   */
  const lastDate =
    calendarEndDate.getDate();


  /**
   * Calendar Cell 목록
   */
  const calendarDays =
    useMemo(
      () => {

        const days:
          Array<
            Date | null
          > = [];


        /**
         * 월 시작 전 빈 Cell
         */
        for (
          let index = 0;
          index < firstDayOfWeek;
          index += 1
        ) {

          days.push(
            null,
          );

        }


        /**
         * 현재 월 날짜
         */
        for (
          let day = 1;
          day <= lastDate;
          day += 1
        ) {

          days.push(
            new Date(
              currentDate.getFullYear(),
              currentDate.getMonth(),
              day,
            ),
          );

        }


        /**
         * 마지막 주 빈 Cell
         */
        while (
          days.length % 7 !== 0
        ) {

          days.push(
            null,
          );

        }


        return days;

      },
      [
        currentDate,
        firstDayOfWeek,
        lastDate,
      ],
    );


  /**
   * =====================================================
   * Schedule Calendar 조회
   * =====================================================
   */
  const loadCalendar =
    useCallback(
      async () => {

        setLoading(true);

        setError('');


        try {

          const data =
            await getProjectScheduleCalendar(
              projectId,
              {
                startDate:
                  formatDate(
                    calendarStartDate,
                  ),

                endDate:
                  formatDate(
                    calendarEndDate,
                  ),
              },
            );


          setSchedules(
            data,
          );

        } catch (err) {

          console.error(
            'Failed to load schedule calendar.',
            err,
          );


          setError(
            'Schedule Calendar 정보를 불러오지 못했습니다.',
          );

        } finally {

          setLoading(false);

        }

      },
      [
        projectId,
        calendarStartDate,
        calendarEndDate,
      ],
    );


  /**
   * =====================================================
   * Calendar 조회
   * =====================================================
   */
  useEffect(
    () => {

      void loadCalendar();

    },
    [
      loadCalendar,
    ],
  );


  /**
   * =====================================================
   * Calendar Navigation
   * =====================================================
   */


  /**
   * 이전 월
   */
  const handlePreviousMonth =
    () => {

      setCurrentDate(
        (current) =>
          new Date(
            current.getFullYear(),
            current.getMonth() - 1,
            1,
          ),
      );

    };


  /**
   * 다음 월
   */
  const handleNextMonth =
    () => {

      setCurrentDate(
        (current) =>
          new Date(
            current.getFullYear(),
            current.getMonth() + 1,
            1,
          ),
      );

    };


  /**
   * 오늘
   */
  const handleToday =
    () => {

      const today =
        new Date();


      setCurrentDate(
        new Date(
          today.getFullYear(),
          today.getMonth(),
          1,
        ),
      );

    };


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
   * Month Title
   * =====================================================
   */
  const monthTitle =
    `${currentDate.getFullYear()}.${
      String(
        currentDate.getMonth() + 1,
      ).padStart(
        2,
        '0',
      )
    }`;


  /**
   * =====================================================
   * Render
   * =====================================================
   */
  return (

    <div
      className="schedule-calendar"
    >


      {/* ================================================
          Calendar Header
          ================================================ */}

      <div
        className="schedule-calendar__header"
      >


        <div
          className="schedule-calendar__title-area"
        >

          <div>

            <div
              className="schedule-calendar__eyebrow"
            >
              PROJECT SCHEDULE
            </div>


            <h2
              className="schedule-calendar__title"
            >
              Schedule Calendar
            </h2>

          </div>

        </div>


        <div
          className="schedule-calendar__navigation"
        >

          <button
            type="button"
            className="schedule-calendar__nav-button"
            onClick={
              handlePreviousMonth
            }
            aria-label="Previous month"
          >
            ‹
          </button>


          <button
            type="button"
            className="schedule-calendar__today-button"
            onClick={
              handleToday
            }
          >
            Today
          </button>


          <div
            className="schedule-calendar__month"
          >
            {monthTitle}
          </div>


          <button
            type="button"
            className="schedule-calendar__nav-button"
            onClick={
              handleNextMonth
            }
            aria-label="Next month"
          >
            ›
          </button>

        </div>

      </div>


      {/* ================================================
          Error
          ================================================ */}

      {error && (

        <div
          className="schedule-calendar__error"
          role="alert"
        >

          {error}

        </div>

      )}


      {/* ================================================
          Loading
          ================================================ */}

      {loading && (

        <div
          className="schedule-calendar__loading"
        >

          <div
            className="spinner"
          />


          <span>
            Calendar 정보를 불러오는 중입니다...
          </span>

        </div>

      )}


      {/* ================================================
          Calendar
          ================================================ */}

      {!loading && (

        <div
          className="schedule-calendar__container"
        >


          {/* ==============================================
              Day Header
              ============================================== */}

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


          {/* ==============================================
              Calendar Days
              ============================================== */}

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
                      key={`empty-${index}`}
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


                /**
                 * Today
                 */
                const today =
                  new Date();


                const isToday =
                  isSameDate(
                    date,
                    today,
                  );


                /**
                 * Day Of Week
                 */
                const dayOfWeek =
                  date.getDay();


                return (

                  <div
                    key={
                      formatDate(
                        date,
                      )
                    }
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


                    {/* ====================================
                        Day Number
                        ==================================== */}

                    <div
                      className="schedule-calendar__day-header"
                    >

                      <span
                        className="schedule-calendar__day-number"
                      >
                        {date.getDate()}
                      </span>

                    </div>


                    {/* ====================================
                        Schedule List
                        ==================================== */}

                    <div
                      className="schedule-calendar__schedule-list"
                    >

                      {daySchedules.map(
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


                          const isStartDate =
                            isSameDate(
                              date,
                              startDate,
                            );


                          const isEndDate =
                            isSameDate(
                              date,
                              endDate,
                            );


                          return (

                            <button
                              key={
                                `${schedule.id}-${formatDate(date)}`
                              }
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

                        },
                      )}

                    </div>

                  </div>

                );

              },
            )}

          </div>

        </div>

      )}


      {/* ================================================
          Calendar Summary
          ================================================ */}

      {!loading && (

        <div
          className="schedule-calendar__footer"
        >

          <span>
            Total
          </span>


          <strong>
            {schedules.length}
          </strong>


          <span>
            schedules
          </span>

        </div>

      )}

    </div>

  );

};


export default ScheduleCalendar;