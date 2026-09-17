import React from 'react';
import { AdminStateJs } from '../../kotlin/adminBridge';
import { Card } from '../common/Card';
import { Button } from '../common/Button';

interface ReservationListProps {
  state: AdminStateJs;
  onMarkLunch: (room: string, index: number, date: string) => void;
  onMarkDinner: (room: string, index: number, date: string) => void;
}

export const ReservationList: React.FC<ReservationListProps> = ({ state, onMarkLunch, onMarkDinner }) => {
  const reservations = Array.from(state.dailyReservations);
  const foodMap = new Map(Array.from(state.foods).map(f => [f.id, f]));

  if (reservations.length === 0) {
    return (
      <Card variant="outlined" shape="medium" style={{ padding: '40px', textAlign: 'center', color: 'var(--on-surface-variant)' }}>
        هیچ رزروی برای تاریخ {state.selectedReportDate || 'انتخاب شده'} یافت نشد.
      </Card>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {reservations.map((res) => (
        <Card key={res.roomNumber} variant="outlined" shape="medium" style={{ overflow: 'hidden' }}>
          <div style={{ backgroundColor: 'var(--surface-variant)', padding: '12px 20px', fontWeight: 'bold', fontSize: '18px', display: 'flex', justifyContent: 'space-between' }}>
            <span>اتاق {res.roomNumber}</span>
            <span style={{ fontSize: '14px', color: 'var(--primary-color)' }}>صبحانه: {res.breakfastCount} نفر</span>
          </div>

          <div style={{ padding: '12px 20px' }}>
            {Array.from(res.guestMealSelections).map((sel) => (
              <div key={sel.guestIndex} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 0', borderBottom: '1px solid var(--background)' }}>
                <span style={{ fontSize: '14px', fontWeight: 'bold' }}>مهمان {sel.guestIndex + 1}</span>

                <div style={{ display: 'flex', gap: '12px' }}>
                  {sel.lunchFoodId && (
                    <Button
                      variant={sel.lunchDelivered ? 'outlined' : 'primary'}
                      size="small"
                      onClick={() => onMarkLunch(res.roomNumber, sel.guestIndex, res.date)}
                      disabled={sel.lunchDelivered}
                      style={{
                        minWidth: '140px',
                        backgroundColor: sel.lunchDelivered ? '#e6f4ea' : undefined,
                        borderColor: sel.lunchDelivered ? '#137333' : undefined,
                        color: sel.lunchDelivered ? '#137333' : undefined
                      }}
                    >
                      {sel.lunchDelivered ? '✓ ناهار تحویل شد' : `ناهار: ${foodMap.get(sel.lunchFoodId)?.name || '...'}`}
                    </Button>
                  )}

                  {sel.dinnerFoodId && (
                    <Button
                      variant={sel.dinnerDelivered ? 'outlined' : 'primary'}
                      size="small"
                      onClick={() => onMarkDinner(res.roomNumber, sel.guestIndex, res.date)}
                      disabled={sel.dinnerDelivered}
                      style={{
                        minWidth: '140px',
                        backgroundColor: sel.dinnerDelivered ? '#e6f4ea' : undefined,
                        borderColor: sel.dinnerDelivered ? '#137333' : undefined,
                        color: sel.dinnerDelivered ? '#137333' : undefined
                      }}
                    >
                      {sel.dinnerDelivered ? '✓ شام تحویل شد' : `شام: ${foodMap.get(sel.dinnerFoodId)?.name || '...'}`}
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </Card>
      ))}
    </div>
  );
};
