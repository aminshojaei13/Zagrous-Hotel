import React from 'react';

interface BreakfastPickerProps {
  date: string;
  count: number;
  maxCount: number;
  isArabic: boolean;
  disabled: boolean;
  onChange: (count: number) => void;
}

export const BreakfastPicker: React.FC<BreakfastPickerProps> = ({
  count,
  maxCount,
  isArabic,
  disabled,
  onChange,
}) => {
  const t = {
    label: isArabic ? 'عدد وجبات الإفطار' : 'تعداد صبحانه',
    noBreakfast: isArabic ? 'بدون إفطار' : 'بدون صبحانه',
    persons: (n: number) => isArabic ? `${n} أشخاص` : `${n} نفر`,
  };

  return (
    <div className="breakfast-picker" style={{
      marginTop: '12px',
      paddingTop: '12px',
      borderTop: '1px dashed #eee',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    }}>
      <label style={{ fontSize: '14px', fontWeight: '500' }}>{t.label}</label>

      <select
        value={count}
        onChange={(e) => onChange(parseInt(e.target.value))}
        disabled={disabled}
        style={{
          padding: '6px 12px',
          borderRadius: '4px',
          border: '1px solid #dadce0',
          fontSize: '14px',
          minWidth: '120px'
        }}
      >
        <option value={0}>{t.noBreakfast}</option>
        {Array.from({ length: maxCount }).map((_, i) => (
          <option key={i + 1} value={i + 1}>
            {t.persons(i + 1)}
          </option>
        ))}
      </select>
    </div>
  );
};
