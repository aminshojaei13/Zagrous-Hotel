import React from 'react';
import { MealSelection } from './MealSelection';
import {
  getReservationBridge,
  FoodItemJs,
  FoodReservationJs
} from '../../kotlin/reservationBridge';

interface StayDayCardProps {
  date: string;
  guestCount: number;
  availableFoods: FoodItemJs[];
  tempReservations: FoodReservationJs[];
  isArabic: boolean;
  isLoading: boolean;
  onFoodChange: (date: string, guestIndex: number, foodId: string | null, type: 'LUNCH' | 'DINNER') => void;
}

export const StayDayCard: React.FC<StayDayCardProps> = ({
  date,
  guestCount,
  availableFoods,
  tempReservations,
  isArabic,
  isLoading,
  onFoodChange,
}) => {
  const bridge = getReservationBridge();
  const dayType = bridge.getDayType(date);

  const reservation = tempReservations.find(r => r.date === date);

  // Filter foods for this specific day and type
  const dayFoods = availableFoods.filter(f => f.dayType === dayType);
  const lunchOptions = dayFoods.filter(f => f.type === 'LUNCH');
  const dinnerOptions = dayFoods.filter(f => f.type === 'DINNER');

  const t = {
    date: isArabic ? `التاريخ: ${date}` : `تاریخ: ${date}`,
    guest: (index: number) => isArabic ? `الضيف ${index}` : `مهمان ${index}`,
    lunch: isArabic ? 'وجبة الغداء' : 'وعده ناهار',
    dinner: isArabic ? 'وجبة العشاء' : 'وعده شام',
  };

  return (
    <div className="stay-day-card" style={{
      backgroundColor: 'white',
      borderRadius: '8px',
      padding: '16px',
      marginBottom: '16px',
      border: '1px solid #e0e0e0',
      boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
    }}>
      <div style={{ fontWeight: 'bold', borderBottom: '1px solid #eee', paddingBottom: '8px', marginBottom: '12px', color: 'var(--primary-color)' }}>
        {t.date}
      </div>

      {Array.from({ length: guestCount }).map((_, index) => {
        const selection = reservation?.guestMealSelections.find(s => s.guestIndex === index);

        return (
          <div key={index} className="guest-row" style={{ marginBottom: index === guestCount - 1 ? 0 : '16px' }}>
            <div style={{ fontSize: '14px', fontWeight: '500', marginBottom: '8px' }}>
              {t.guest(index + 1)}
            </div>
            <div style={{ display: 'flex', gap: '12px' }}>
              <MealSelection
                label={t.lunch}
                foods={lunchOptions}
                selectedId={selection?.lunchFoodId || null}
                isArabic={isArabic}
                disabled={isLoading}
                onSelect={(id) => onFoodChange(date, index, id, 'LUNCH')}
              />
              <MealSelection
                label={t.dinner}
                foods={dinnerOptions}
                selectedId={selection?.dinnerFoodId || null}
                isArabic={isArabic}
                disabled={isLoading}
                onSelect={(id) => onFoodChange(date, index, id, 'DINNER')}
              />
            </div>
          </div>
        );
      })}
    </div>
  );
};
