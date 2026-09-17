import React from 'react';
import { Card } from '../common/Card';
import { BreakfastPicker } from './BreakfastPicker';
import { MealSelection } from './MealSelection';
import { FoodItemJs, FoodReservationJs } from '../../kotlin/reservationBridge';

interface StayDayCardProps {
  date: string;
  guestCount: number;
  availableFoods: FoodItemJs[];
  tempReservations: FoodReservationJs[];
  isArabic: boolean;
  isLoading: boolean;
  onFoodChange: (date: string, guestIndex: number, foodId: string | null, type: 'LUNCH' | 'DINNER') => void;
  onBreakfastChange: (date: string, count: number) => void;
}

export const StayDayCard: React.FC<StayDayCardProps> = ({
  date, guestCount, availableFoods, tempReservations, isArabic, isLoading,
  onFoodChange, onBreakfastChange
}) => {
  const reservation = Array.from(tempReservations).find(r => r.date === date);

  // Business logic for locking (Simplified for UI parity - keep as is)
  const isLocked = false;

  // In Compose, dayType is calculated from DateUtils.
  // We'll keep the current simple filtering or assume availableFoods is already filtered by Kotlin (it is).
  const lunchFoods = Array.from(availableFoods).filter(f => f.type === 'LUNCH');
  const dinnerFoods = Array.from(availableFoods).filter(f => f.type === 'DINNER');

  return (
    <Card variant="outlined" shape="large" style={{ padding: '20px', marginBottom: '24px' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' }}>
        <div style={{
          background: 'var(--primary-container)',
          width: '40px', height: '40px',
          borderRadius: '50%',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'var(--primary-color)',
          fontSize: '20px'
        }}>📅</div>
        <h3 style={{ margin: 0, fontSize: '18px', color: 'var(--on-surface)' }}>
          {isArabic ? `التاريخ: ${date}` : `تاریخ: ${date}`}
        </h3>
      </div>

      <BreakfastPicker
        count={reservation?.breakfastCount || 0}
        max={guestCount}
        isArabic={isArabic}
        disabled={isLoading || isLocked}
        onChange={(count) => onBreakfastChange(date, count)}
      />

      <div style={{ height: '1px', background: 'var(--outline-variant)', opacity: 0.3, margin: '24px 0' }}></div>

      {Array.from({ length: guestCount }).map((_, idx) => {
        const selection = Array.from(reservation?.guestMealSelections || []).find(s => s.guestIndex === idx);

        return (
          <div key={idx} style={{ marginBottom: idx === guestCount - 1 ? 0 : '24px' }}>
            <div style={{ fontSize: '14px', fontWeight: 'bold', color: 'var(--primary-color)', marginBottom: '12px' }}>
              {isArabic ? `اختيار الضيف ${idx + 1}:` : `انتخاب مهمان ${idx + 1}:`}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              <MealSelection
                label={isArabic ? 'وجبة الغداء' : 'وعده ناهار'}
                icon="☀️"
                foods={lunchFoods}
                selectedId={selection?.lunchFoodId || null}
                isArabic={isArabic}
                disabled={isLoading || isLocked}
                onChange={(fId) => onFoodChange(date, idx, fId, 'LUNCH')}
              />
              <MealSelection
                label={isArabic ? 'وجبة العشاء' : 'وعده شام'}
                icon="🌙"
                foods={dinnerFoods}
                selectedId={selection?.dinnerFoodId || null}
                isArabic={isArabic}
                disabled={isLoading || isLocked}
                onChange={(fId) => onFoodChange(date, idx, fId, 'DINNER')}
              />
            </div>
          </div>
        );
      })}
    </Card>
  );
};
