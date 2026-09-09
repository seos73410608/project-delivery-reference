/**
 * =====================================================
 * Calendar Utility
 * =====================================================
 */


/**
 * Date를 YYYY-MM-DD 형식으로 변환한다.
 */
export const formatDate =
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
export const parseDate =
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
export const isSameDate =
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
export const isDateInRange =
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
 * Calendar Range
 * =====================================================
 */


/**
 * Calendar 기준 월의 시작일을 반환한다.
 *
 * @param currentDate Calendar 기준 날짜
 */
export const getCalendarStartDate =
  (
    currentDate: Date,
  ): Date => {

    return new Date(
      currentDate.getFullYear(),
      currentDate.getMonth(),
      1,
    );

  };


/**
 * Calendar 기준 월의 종료일을 반환한다.
 *
 * @param currentDate Calendar 기준 날짜
 */
export const getCalendarEndDate =
  (
    currentDate: Date,
  ): Date => {

    return new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
      0,
    );

  };


/**
 * =====================================================
 * Calendar Grid
 * =====================================================
 */


/**
 * Calendar Grid 날짜 목록을 생성한다.
 *
 * 현재 월 기준으로
 *
 * - 월 시작 전 빈 Cell
 * - 현재 월 날짜
 * - 월 종료 후 빈 Cell
 *
 * 을 포함한다.
 *
 * 반환되는 배열은
 * 항상 7의 배수가 된다.
 *
 * @param currentDate Calendar 기준 날짜
 */
export const createCalendarDays =
  (
    currentDate: Date,
  ): Array<Date | null> => {

    const days:
      Array<Date | null> = [];


    /**
     * 현재 월 시작일
     */
    const calendarStartDate =
      getCalendarStartDate(
        currentDate,
      );


    /**
     * 현재 월 종료일
     */
    const calendarEndDate =
      getCalendarEndDate(
        currentDate,
      );


    /**
     * 월 시작 요일
     *
     * Sunday = 0
     * Monday = 1
     * ...
     * Saturday = 6
     */
    const firstDayOfWeek =
      calendarStartDate.getDay();


    /**
     * 현재 월 마지막 날짜
     */
    const lastDate =
      calendarEndDate.getDate();


    /**
     * ===============================================
     * 월 시작 전 빈 Cell
     * ===============================================
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
     * ===============================================
     * 현재 월 날짜
     * ===============================================
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
     * ===============================================
     * 마지막 주 빈 Cell
     * ===============================================
     *
     * Calendar Grid를
     * 7일 단위로 맞춘다.
     */
    while (
      days.length % 7 !== 0
    ) {

      days.push(
        null,
      );

    }


    return days;

  };