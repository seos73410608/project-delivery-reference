interface CalendarHeaderProps {

  monthTitle: string;

  onPreviousMonth: () => void;

  onNextMonth: () => void;

  onToday: () => void;

}


/**
 * =====================================================
 * Calendar Header
 * =====================================================
 *
 * Schedule Calendar Header를 표시한다.
 *
 * - Calendar Title
 * - Previous Month
 * - Today
 * - Current Month
 * - Next Month
 */
const CalendarHeader = (
  {
    monthTitle,
    onPreviousMonth,
    onNextMonth,
    onToday,
  }: CalendarHeaderProps,
) => {

  return (

    <div
      className="schedule-calendar__header"
    >


      {/* ================================================
          Calendar Title
          ================================================ */}

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


      {/* ================================================
          Calendar Navigation
          ================================================ */}

      <div
        className="schedule-calendar__navigation"
      >

        {/* Previous Month */}

        <button
          type="button"
          className="schedule-calendar__nav-button"
          onClick={
            onPreviousMonth
          }
          aria-label="Previous month"
        >
          ‹
        </button>


        {/* Today */}

        <button
          type="button"
          className="schedule-calendar__today-button"
          onClick={
            onToday
          }
        >
          Today
        </button>


        {/* Current Month */}

        <div
          className="schedule-calendar__month"
        >
          {monthTitle}
        </div>


        {/* Next Month */}

        <button
          type="button"
          className="schedule-calendar__nav-button"
          onClick={
            onNextMonth
          }
          aria-label="Next month"
        >
          ›
        </button>

      </div>

    </div>

  );

};


export default CalendarHeader;