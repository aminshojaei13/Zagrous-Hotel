import React, { useState, useEffect } from 'react';
import { AdminFoodItemJs } from '../../kotlin/adminBridge';

interface FoodFormProps {
  initialFood?: AdminFoodItemJs;
  defaultType: string;
  defaultDayType: string;
  onSubmit: (food: any) => void;
  onCancel: () => void;
  isLoading: boolean;
}

export const FoodForm: React.FC<FoodFormProps> = ({
  initialFood,
  defaultType,
  defaultDayType,
  onSubmit,
  onCancel,
  isLoading
}) => {
  const [formData, setFormData] = useState({
    id: '',
    name: '',
    nameAr: '',
    type: defaultType,
    dayType: defaultDayType,
    isActive: true,
    isVisibleToUsers: true,
    displayOrder: 0
  });

  useEffect(() => {
    if (initialFood) {
      setFormData({
        id: initialFood.id,
        name: initialFood.name,
        nameAr: initialFood.nameAr || '',
        type: initialFood.type,
        dayType: initialFood.dayType,
        isActive: initialFood.isActive,
        isVisibleToUsers: initialFood.isVisibleToUsers,
        displayOrder: initialFood.displayOrder
      });
    } else {
      setFormData({
        id: '',
        name: '',
        nameAr: '',
        type: defaultType,
        dayType: defaultDayType,
        isActive: true,
        isVisibleToUsers: true,
        displayOrder: 0
      });
    }
  }, [initialFood, defaultType, defaultDayType]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} style={{
      padding: '20px',
      border: '1px solid #ccc',
      borderRadius: '8px',
      backgroundColor: '#f9f9f9',
      display: 'flex',
      flexDirection: 'column',
      gap: '12px'
    }}>
      <h3>{initialFood ? 'Edit Food' : 'Add Food'}</h3>

      <div className="form-group">
        <label>Name (Persian)</label>
        <input
          type="text"
          value={formData.name}
          onChange={e => setFormData({...formData, name: e.target.value})}
          required
        />
      </div>

      <div className="form-group">
        <label>Name (Arabic)</label>
        <input
          type="text"
          value={formData.nameAr}
          onChange={e => setFormData({...formData, nameAr: e.target.value})}
        />
      </div>

      <div className="form-group">
        <label>Display Order</label>
        <input
          type="number"
          value={formData.displayOrder}
          onChange={e => setFormData({...formData, displayOrder: parseInt(e.target.value) || 0})}
        />
      </div>

      <div style={{ display: 'flex', gap: '10px' }}>
        <button type="submit" disabled={isLoading} style={{ flex: 1 }}>
          {isLoading ? 'Saving...' : 'Save'}
        </button>
        <button type="button" onClick={onCancel} style={{ flex: 1, backgroundColor: '#5f6368' }}>
          Cancel
        </button>
      </div>
    </form>
  );
};
