import React from 'react';

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  options: { value: string | number; label: string }[];
  error?: string | null;
  leadingIcon?: React.ReactNode;
}

export const Select: React.FC<SelectProps> = ({
  label,
  options,
  error,
  leadingIcon,
  id,
  className = '',
  ...props
}) => {
  return (
    <div className={`m3-select ${error ? 'm3-select--error' : ''} ${className}`}>
      <label htmlFor={id} className="m3-select__label">{label}</label>
      <div className="m3-select__container">
        {leadingIcon && <span className="m3-select__icon">{leadingIcon}</span>}
        <select
          id={id}
          className="m3-select__input"
          {...props}
        >
          {options.map(opt => (
            <option key={opt.value} value={opt.value}>{opt.label}</option>
          ))}
        </select>
        <span className="m3-select__arrow">▼</span>
      </div>
    </div>
  );
};
