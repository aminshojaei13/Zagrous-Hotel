import React from 'react';

interface SwitchProps {
  checked: boolean;
  onChange: (checked: boolean) => void;
  disabled?: boolean;
}

export const Switch: React.FC<SwitchProps> = ({ checked, onChange, disabled }) => {
  return (
    <label className={`m3-switch ${disabled ? 'm3-switch--disabled' : ''}`}>
      <input
        type="checkbox"
        checked={checked}
        onChange={(e) => onChange(e.target.checked)}
        disabled={disabled}
      />
      <span className="m3-switch__slider"></span>
    </label>
  );
};
