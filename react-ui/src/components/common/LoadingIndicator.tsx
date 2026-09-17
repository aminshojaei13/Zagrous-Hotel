import React from 'react';

export const LoadingIndicator: React.FC = () => {
  return (
    <div className="m3-loader-container">
      <div className="m3-loader"></div>
      <div style={{ marginTop: '12px', fontSize: '14px', color: 'var(--primary-color)' }}>در حال دریافت اطلاعات...</div>
    </div>
  );
};
