import React from 'react';

interface TextFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string | null;
  leadingIcon?: React.ReactNode;
}

export const TextField: React.FC<TextFieldProps> = ({
  label,
  error,
  leadingIcon,
  id,
  className = '',
  ...props
}) => {
  return (
    <div className={`m3-textfield ${error ? 'm3-textfield--error' : ''} ${className}`}>
      <label htmlFor={id} className="m3-textfield__label">{label}</label>
      <div className="m3-textfield__container">
        {leadingIcon && <span className="m3-textfield__icon">{leadingIcon}</span>}
        <input
          id={id}
          className="m3-textfield__input"
          {...props}
        />
      </div>
      {error && <span className="m3-textfield__error-text">{error}</span>}
    </div>
  );
};
