import React from 'react';
import { useReservationViewModel } from '../../hooks/useReservationViewModel';
import { GuestSummaryCard } from './GuestSummaryCard';

export const ReservationPage: React.FC = () => {
  const { state } = useReservationViewModel();

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

      <section className="stay-info">
        <h3>{state.isArabic ? 'معلومات الإقامة' : 'اطلاعات اقامت'}</h3>
        <p style={{ color: '#5f6368' }}>
          {state.isArabic
            ? `عدد الأيام: ${state.stayDays.length}`
            : `تعداد روزهای اقامت: ${state.stayDays.length}`}
        </p>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
          {state.stayDays.map(day => (
            <span key={day} style={{
              padding: '4px 12px',
              backgroundColor: '#e8f0fe',
              color: 'var(--primary-color)',
              borderRadius: '16px',
              fontSize: '14px'
            }}>
              {day}
            </span>
          ))}
        </div>
      </section>

      {/* Further slices (Food Selection, etc.) will be added here */}
    </div>
  );
};
