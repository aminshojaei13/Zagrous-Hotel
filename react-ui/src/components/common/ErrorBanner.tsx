import React from 'react';

interface ErrorBannerProps {
  message: string;
  variant?: 'error' | 'success';
}

export const ErrorBanner: React.FC<ErrorBannerProps> = ({ message, variant = 'error' }) => {
  return (
    <div className={`m3-banner m3-banner--${variant}`}>
      <span className="m3-banner__icon">
        {variant === 'error' ? '⚠️' : '✅'}
      </span>
      <span className="m3-banner__message">{message}</span>
    </div>
  );
};
