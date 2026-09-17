import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'outlined' | 'text';
  isLoading?: boolean;
  size?: 'small' | 'medium' | 'large';
  icon?: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  isLoading,
  size = 'medium',
  icon,
  className = '',
  disabled,
  ...props
}) => {
  const baseClass = 'm3-btn';
  const variantClass = `m3-btn--${variant}`;
  const sizeClass = `m3-btn--${size}`;

  return (
    <button
      className={`${baseClass} ${variantClass} ${sizeClass} ${className}`}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading ? (
        <span className="m3-btn__loader"></span>
      ) : (
        <>
          {icon && <span className="m3-btn__icon">{icon}</span>}
          {children}
        </>
      )}
    </button>
  );
};
