import React from 'react';
import { useReservationViewModel } from '../../hooks/useReservationViewModel';
import { GuestSummaryCard } from './GuestSummaryCard';
import { StayDayCard } from './StayDayCard';
import { ConfirmSection } from './ConfirmSection';

export const ReservationPage: React.FC = () => {
  const {
    state,
    changeFood,
    changeBreakfastCount,
    confirmReservation
  } = useReservationViewModel();

  if (!state.room) {
    return (
      <div className="container">
        <div className="error-message">Error: Room information not found.</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', padding: '0 8px' }}>
        <h1 style={{ margin: 0, fontSize: '24px', color: 'var(--primary-color)' }}>
          {state.isArabic ? 'لوحة الضيف' : 'پنل مهمان'}
        </h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '14px', color: 'var(--outline)' }}>
            {state.isArabic ? `غرفة ${state.room.roomNumber}` : `اتاق ${state.room.roomNumber}`}
          </span>
          {state.isLoading && <div className="loading-indicator">در حال بروزرسانی...</div>}
        </div>
      </header>

      {state.error && state.error !== 'reservation_success' && state.error !== 'reservation_failed' && (
        <div className="error-banner">⚠️ {state.error}</div>
      )}

      <GuestSummaryCard room={state.room} isArabic={state.isArabic} />

      <section className="food-selection">
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '16px', padding: '0 8px' }}>
          <span style={{ fontSize: '24px' }}>🍴</span>
          <h3 style={{ margin: 0 }}>{state.isArabic ? 'اختيار الوجبات' : 'انتخاب برنامه غذایی'}</h3>
        </div>

        {state.stayDays.map(date => (
          <StayDayCard
            key={date}
            date={date}
            guestCount={state.room?.guestCount || 0}
            availableFoods={state.availableFoods}
            tempReservations={state.tempReservations}
            isArabic={state.isArabic}
            isLoading={state.isLoading}
            onFoodChange={(d, idx, fId, type) => changeFood(d, idx, fId, type)}
            onBreakfastChange={(d, count) => changeBreakfastCount(d, count)}
          />
        ))}
      </section>

      <ConfirmSection
        state={state}
        onConfirm={confirmReservation}
      />
    </div>
  );
};
