import React from 'react';
import { useReservationViewModel } from '../../hooks/useReservationViewModel';
import { GuestSummaryCard } from './GuestSummaryCard';
import { StayDayCard } from './StayDayCard';

export const ReservationPage: React.FC = () => {
  const { state, changeFood } = useReservationViewModel();

  if (!state.room) {
    return (
      <div className="container">
        <div className="error-message">Error: Room information not found.</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container" style={{ padding: '20px' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ margin: 0, fontSize: '24px', color: 'var(--primary-color)' }}>
          {state.isArabic ? 'لوحة الضيف' : 'پنل مهمان'}
        </h1>
        {state.isLoading && <div className="loading-indicator">Updating...</div>}
      </header>

      {state.error && <div className="error-message">{state.error}</div>}

      <GuestSummaryCard room={state.room} isArabic={state.isArabic} />

      <section className="food-selection">
        <h3 style={{ marginBottom: '16px' }}>{state.isArabic ? 'اختيار الوجبات' : 'انتخاب وعده‌های غذایی'}</h3>

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
          />
        ))}
      </section>
    </div>
  );
};
