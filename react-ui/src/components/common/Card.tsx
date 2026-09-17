import React from 'react';

interface CardProps {
  children: React.ReactNode;
  variant?: 'default' | 'outlined' | 'elevated' | 'summary';
  shape?: 'medium' | 'large';
  className?: string;
  style?: React.CSSProperties;
}

export const Card: React.FC<CardProps> = ({
  children,
  variant = 'default',
  shape = 'large',
  className = '',
  style
}) => {
  return (
    <div
      className={`m3-card m3-card--${variant} m3-card--shape-${shape} ${className}`}
      style={style}
    >
      {children}
    </div>
  );
};
