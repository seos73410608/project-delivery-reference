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

import {
  createCalendarDays,
  formatDate,
  getCalendarEndDate,
  getCalendarStartDate,
} from '../../utils/calendarUtils';

import CalendarHeader
  from './CalendarHeader';

import CalendarGrid
  from './CalendarGrid';

import '../../styles/Calendar.css';


interface ScheduleCalendarProps {

  projectId: number;

  onSelect?: (
    schedule: ScheduleResponse,
  ) => void;

}


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
        getCalendarStartDate(
          currentDate,
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
        getCalendarEndDate(
          currentDate,
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
   * Calendar Cell 목록
   */
  const calendarDays =
    useMemo(
      () =>
        createCalendarDays(
          currentDate,
        ),
      [
        currentDate,
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

      <CalendarHeader
        monthTitle={
          monthTitle
        }
        onPreviousMonth={
          handlePreviousMonth
        }
        onNextMonth={
          handleNextMonth
        }
        onToday={
          handleToday
        }
      />


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

          <CalendarGrid
            calendarDays={
              calendarDays
            }
            schedules={
              schedules
            }
            onSelect={
              onSelect
            }
          />

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