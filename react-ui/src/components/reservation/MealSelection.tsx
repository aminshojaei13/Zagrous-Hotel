import React from 'react';
import { Select } from '../common/Select';
import { FoodItemJs } from '../../kotlin/reservationBridge';

interface MealSelectionProps {
  label: string;
  icon: string;
  foods: FoodItemJs[];
  selectedId: string | null;
  isArabic: boolean;
  disabled: boolean;
  onChange: (foodId: string | null) => void;
}

export const MealSelection: React.FC<MealSelectionProps> = ({
  label, icon, foods, selectedId, isArabic, disabled, onChange
}) => {
  const options = [
    { value: 'none', label: isArabic ? 'عدم الاختيار' : 'عدم انتخاب' },
    ...foods.map(f => ({
      value: f.id,
      label: (isArabic && f.nameAr) ? f.nameAr : f.name
    }))
  ];

  const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const val = e.target.value;
    onChange(val === 'none' ? null : val);
  };

  return (
    <Select
      label={label}
      options={options}
      value={selectedId || 'none'}
      onChange={handleChange}
      disabled={disabled}
      leadingIcon={icon}
    />
  );
};
