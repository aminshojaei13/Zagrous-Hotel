import React from 'react';

interface FilterChipProps {
  label: string;
  selected: boolean;
  onClick: () => void;
  className?: string;
}

export const FilterChip: React.FC<FilterChipProps> = ({
  label,
  selected,
  onClick,
  className = ''
}) => {
  return (
    <button
      type="button"
      className={`m3-filter-chip ${selected ? 'm3-filter-chip--selected' : ''} ${className}`}
      onClick={onClick}
    >
      {selected && <span className="m3-filter-chip__icon">✓</span>}
      <span className="m3-filter-chip__label">{label}</span>
    </button>
  );
};
