import React from 'react';

interface IconButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  icon: React.ReactNode;
}

export const IconButton: React.FC<IconButtonProps> = ({ icon, className = '', ...props }) => {
  return (
    <button className={`m3-icon-button ${className}`} {...props}>
      {icon}
    </button>
  );
};
