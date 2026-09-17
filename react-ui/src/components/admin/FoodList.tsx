import React from 'react';
import { AdminFoodItemJs } from '../../kotlin/adminBridge';
import { Card } from '../common/Card';
import { Button } from '../common/Button';
import { Switch } from '../common/Switch';

interface FoodListProps {
  foods: AdminFoodItemJs[];
  onDelete: (id: string) => void;
  onEdit: (food: AdminFoodItemJs) => void;
  onToggleActive: (food: AdminFoodItemJs) => void;
  onToggleVisible: (food: AdminFoodItemJs) => void;
}

export const FoodList: React.FC<FoodListProps> = ({
  foods, onDelete, onEdit, onToggleActive, onToggleVisible
}) => {
  if (foods.length === 0) {
    return <div style={{ padding: '40px', textAlign: 'center', color: 'var(--on-surface-variant)', background: 'var(--surface)', borderRadius: '16px' }}>غذایی در این دسته یافت نشد.</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {foods.map((food) => (
        <Card key={food.id} variant="outlined" shape="medium" style={{ padding: '16px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 'bold', fontSize: '18px' }}>{food.name}</div>
              <div style={{ fontSize: '12px', color: 'var(--on-surface-variant)', marginTop: '4px' }}>
                ترتیب نمایش: {food.displayOrder} | {food.nameAr ? `نام عربی: ${food.nameAr}` : 'فاقد نام عربی'}
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '12px' }}>فعال:</span>
                <Switch checked={food.isActive} onChange={() => onToggleActive(food)} />
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '12px' }}>نمایش به کاربر:</span>
                <Switch checked={food.isVisibleToUsers} onChange={() => onToggleVisible(food)} />
              </div>

              <div style={{ height: '32px', width: '1px', background: 'var(--outline-variant)', margin: '0 8px' }}></div>

              <div style={{ display: 'flex', gap: '8px' }}>
                <Button variant="outlined" size="small" onClick={() => onEdit(food)}>ویرایش</Button>
                <Button variant="outlined" size="small" onClick={() => onDelete(food.id)} style={{ color: 'var(--error)', borderColor: 'var(--error)' }}>حذف</Button>
              </div>
            </div>
          </div>
        </Card>
      ))}
    </div>
  );
};
