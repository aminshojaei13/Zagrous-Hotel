import React from 'react';
import { AdminFoodItemJs } from '../../kotlin/adminBridge';

interface FoodListProps {
  foods: AdminFoodItemJs[];
  onDelete: (id: string) => void;
  onEdit: (food: AdminFoodItemJs) => void;
  onToggleActive: (food: AdminFoodItemJs) => void;
  onToggleVisible: (food: AdminFoodItemJs) => void;
}

export const FoodList: React.FC<FoodListProps> = ({
  foods,
  onDelete,
  onEdit,
  onToggleActive,
  onToggleVisible
}) => {
  if (foods.length === 0) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>No foods found for this category.</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {foods.map((food) => (
        <div key={food.id} style={{
          padding: '12px',
          border: '1px solid #ddd',
          borderRadius: '8px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          backgroundColor: 'white'
        }}>
          <div style={{ flex: 1 }}>
            <div style={{ fontWeight: 'bold' }}>{food.name}</div>
            <div style={{ fontSize: '12px', color: '#888' }}>
              Order: {food.displayOrder} | {food.nameAr || 'No Arabic Name'}
            </div>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginRight: '16px' }}>
            <label style={{ fontSize: '12px', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <input
                type="checkbox"
                checked={food.isActive}
                onChange={() => onToggleActive(food)}
              />
              Active
            </label>
            <label style={{ fontSize: '12px', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <input
                type="checkbox"
                checked={food.isVisibleToUsers}
                onChange={() => onToggleVisible(food)}
              />
              Visible
            </label>
          </div>

          <div style={{ display: 'flex', gap: '8px' }}>
            <button onClick={() => onEdit(food)} style={{ width: 'auto', padding: '4px 8px', fontSize: '12px' }}>Edit</button>
            <button onClick={() => onDelete(food.id)} style={{ width: 'auto', padding: '4px 8px', fontSize: '12px', backgroundColor: '#d93025' }}>Delete</button>
          </div>
        </div>
      ))}
    </div>
  );
};
