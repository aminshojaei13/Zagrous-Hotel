import React, { useState } from 'react';
import { AdminFoodItemJs } from '../../kotlin/adminBridge';
import { TextField } from '../common/TextField';
import { Button } from '../common/Button';
import { Card } from '../common/Card';

interface FoodFormProps {
  initialFood?: AdminFoodItemJs;
  defaultType: string;
  defaultDayType: string;
  onSubmit: (food: Partial<AdminFoodItemJs>) => void;
  onCancel: () => void;
  isLoading: boolean;
}

export const FoodForm: React.FC<FoodFormProps> = ({
  initialFood, defaultType, defaultDayType, onSubmit, onCancel, isLoading
}) => {
  const [name, setName] = useState(initialFood?.name || '');
  const [nameAr, setNameAr] = useState(initialFood?.nameAr || '');
  const [displayOrder, setDisplayOrder] = useState(initialFood?.displayOrder?.toString() || '0');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      id: initialFood?.id || '',
      name,
      nameAr: nameAr || null,
      type: initialFood?.type || defaultType,
      dayType: initialFood?.dayType || defaultDayType,
      isActive: initialFood?.isActive ?? true,
      isVisibleToUsers: initialFood?.isVisibleToUsers ?? true,
      displayOrder: parseInt(displayOrder) || 0
    });
  };

  return (
    <Card variant="outlined" shape="medium" style={{ padding: '24px', background: 'var(--surface-variant)' }}>
      <form onSubmit={handleSubmit}>
        <h3 style={{ marginTop: 0, marginBottom: '24px' }}>{initialFood ? 'ویرایش غذا' : 'افزودن غذای جدید'}</h3>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
          <TextField
            label="نام غذا (فارسی)"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <TextField
            label="نام غذا (عربی)"
            value={nameAr}
            onChange={(e) => setNameAr(e.target.value)}
          />
        </div>

        <TextField
          label="ترتیب نمایش"
          type="number"
          value={displayOrder}
          onChange={(e) => setDisplayOrder(e.target.value)}
          required
        />

        <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
          <Button type="submit" isLoading={isLoading}>ذخیره</Button>
          <Button type="button" variant="outlined" onClick={onCancel}>انصراف</Button>
        </div>
      </form>
    </Card>
  );
};
