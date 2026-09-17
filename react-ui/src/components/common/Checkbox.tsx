import React from 'react';

interface CheckboxProps {
  label: string;
  checked: boolean;
  onChange: (checked: boolean) => void;
  disabled?: boolean;
}

export const Checkbox: React.FC<CheckboxProps> = ({ label, checked, onChange, disabled }) => {
  return (
    <label className={`m3-checkbox ${disabled ? 'm3-checkbox--disabled' : ''}`}>
      <input
        type="checkbox"
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
        disabled={disabled}
      />
      <span className="m3-checkbox__label">{label}</span>
    </label>
  );
};
