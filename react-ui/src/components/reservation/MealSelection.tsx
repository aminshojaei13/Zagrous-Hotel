import React from 'react';
import { FoodItemJs } from '../../kotlin/reservationBridge';

interface MealSelectionProps {
  label: string;
  foods: FoodItemJs[];
  selectedId: string | null | undefined;
  isArabic: boolean;
  disabled: boolean;
  onSelect: (foodId: string | null) => void;
}

export const MealSelection: React.FC<MealSelectionProps> = ({
  label,
  foods,
  selectedId,
  isArabic,
  disabled,
  onSelect,
}) => {
  return (
    <div className="meal-selection" style={{ flex: 1 }}>
      <label style={{ fontSize: '12px', color: '#5f6368', display: 'block', marginBottom: '4px' }}>
        {label}
      </label>
      <select
        value={selectedId || ''}
        onChange={(e) => onSelect(e.target.value || null)}
        disabled={disabled}
        style={{
          width: '100%',
          padding: '8px',
          borderRadius: '4px',
          border: '1px solid #dadce0',
          backgroundColor: disabled ? '#f1f3f4' : 'white',
          fontSize: '14px'
        }}
      >
        <option value="">{isArabic ? 'لم يتم الاختيار' : 'انتخاب نشده'}</option>
        {foods.map((food) => (
          <option key={food.id} value={food.id}>
            {isArabic && food.nameAr ? food.nameAr : food.name}
          </option>
        ))}
      </select>
    </div>
  );
};
