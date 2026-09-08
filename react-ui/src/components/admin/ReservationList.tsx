import React from 'react';
import { AdminStateJs } from '../../kotlin/adminBridge';

interface ReservationListProps {
  state: AdminStateJs;
  onMarkLunch: (room: string, index: number, date: string) => void;
  onMarkDinner: (room: string, index: number, date: string) => void;
}

export const ReservationList: React.FC<ReservationListProps> = ({ state, onMarkLunch, onMarkDinner }) => {
  const reservations = Array.from(state.dailyReservations);
  const foodMap = new Map(Array.from(state.foods).map(f => [f.id, f]));

  if (reservations.length === 0) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>No deliveries found for {state.selectedReportDate}.</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {reservations.map((res) => (
        <div key={res.roomNumber} style={{ border: '1px solid #eee', borderRadius: '8px', overflow: 'hidden' }}>
          <div style={{ backgroundColor: '#f8f9fa', padding: '12px', fontWeight: 'bold' }}>
            Room {res.roomNumber}
          </div>
          <div style={{ padding: '12px' }}>
            {Array.from(res.guestMealSelections).map((sel) => (
              <div key={sel.guestIndex} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderBottom: '1px solid #fafafa' }}>
                <span style={{ fontSize: '14px' }}>Guest {sel.guestIndex + 1}</span>
                <div style={{ display: 'flex', gap: '8px' }}>
                  {sel.lunchFoodId && (
                    <button
                      onClick={() => onMarkLunch(res.roomNumber, sel.guestIndex, res.date)}
                      disabled={sel.lunchDelivered}
                      style={{
                        fontSize: '11px', width: 'auto', padding: '4px 8px',
                        backgroundColor: sel.lunchDelivered ? '#188038' : 'var(--primary-color)'
                      }}
                    >
                      Lunch: {foodMap.get(sel.lunchFoodId)?.name || '...'} {sel.lunchDelivered ? '✓' : ''}
                    </button>
                  )}
                  {sel.dinnerFoodId && (
                    <button
                      onClick={() => onMarkDinner(res.roomNumber, sel.guestIndex, res.date)}
                      disabled={sel.dinnerDelivered}
                      style={{
                        fontSize: '11px', width: 'auto', padding: '4px 8px',
                        backgroundColor: sel.dinnerDelivered ? '#188038' : 'var(--primary-color)'
                      }}
                    >
                      Dinner: {foodMap.get(sel.dinnerFoodId)?.name || '...'} {sel.dinnerDelivered ? '✓' : ''}
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
};
