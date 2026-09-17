import React from 'react';
import { Select } from '../common/Select';

interface BreakfastPickerProps {
  count: number;
  max: number;
  isArabic: boolean;
  disabled: boolean;
  onChange: (count: number) => void;
}

export const BreakfastPicker: React.FC<BreakfastPickerProps> = ({ count, max, isArabic, disabled, onChange }) => {
  const options = [
    { value: 0, label: isArabic ? 'بدون إفطار' : 'بدون صبحانه' },
    ...Array.from({ length: max }, (_, i) => ({
      value: i + 1,
      label: isArabic ? `${i + 1} أشخاص` : `${i + 1} نفر`
    }))
  ];

  return (
    <div style={{ marginBottom: '20px', padding: '12px', background: 'var(--surface-variant)', borderRadius: '12px' }}>
      <Select
        label={isArabic ? 'عدد وجبات الإفطار' : 'تعداد وعده صبحانه'}
        options={options}
        value={count}
        onChange={(e) => onChange(parseInt(e.target.value))}
        disabled={disabled}
        leadingIcon="🍳"
      />
      <div style={{ fontSize: '11px', color: 'var(--on-surface-variant)', marginTop: '8px', paddingRight: '4px' }}>
        {isArabic ? 'بوفيه مفتوح' : 'سلف سرویس'}
      </div>
    </div>
  );
};
