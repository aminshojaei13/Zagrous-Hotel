import React, { useState } from 'react';
import { useAdminViewModel } from '../../hooks/useAdminViewModel';
import { FoodList } from './FoodList';
import { FoodForm } from './FoodForm';
import { AdminFoodItemJs } from '../../kotlin/adminBridge';

export const MenuManager: React.FC = () => {
  const { state, upsertFood, deleteFood, updateMenuConfig } = useAdminViewModel();

  const [selectedDayType, setSelectedDayType] = useState('EVEN');
  const [selectedFoodType, setSelectedFoodType] = useState('LUNCH');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingFood, setEditingFood] = useState<AdminFoodItemJs | undefined>(undefined);

  const dayTypes = [
    { id: 'EVEN', label: 'Even' },
    { id: 'ODD', label: 'Odd' },
    { id: 'FRIDAY', label: 'Friday' }
  ];

  const foodTypes = [
    { id: 'LUNCH', label: 'Lunch' },
    { id: 'DINNER', label: 'Dinner' }
  ];

  const currentConfig = Array.from(state.menuConfigs).find(
    c => c.dayType === selectedDayType && c.foodType === selectedFoodType
  );

  const filteredFoods = Array.from(state.foods)
    .filter(f => f.dayType === selectedDayType && f.type === selectedFoodType)
    .sort((a, b) => a.displayOrder - b.displayOrder);

  const handleAddClick = () => {
    setEditingFood(undefined);
    setIsFormOpen(true);
  };

  const handleEditClick = (food: AdminFoodItemJs) => {
    setEditingFood(food);
    setIsFormOpen(true);
  };

  const handleToggleConfig = (enabled: boolean) => {
    updateMenuConfig(selectedDayType, selectedFoodType, enabled);
  };

  const handleUpsert = (foodData: any) => {
    upsertFood(foodData as AdminFoodItemJs);
    setIsFormOpen(false);
  };

  return (
    <div style={{ marginTop: '20px' }}>
      <div style={{ display: 'flex', gap: '8px', marginBottom: '12px' }}>
        {dayTypes.map(dt => (
          <button
            key={dt.id}
            onClick={() => setSelectedDayType(dt.id)}
            style={{
              width: 'auto',
              padding: '6px 12px',
              fontSize: '14px',
              backgroundColor: selectedDayType === dt.id ? 'var(--primary-color)' : '#eee',
              color: selectedDayType === dt.id ? 'white' : 'black',
              cursor: 'pointer'
            }}
          >
            {dt.label}
          </button>
        ))}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '20px' }}>
        <div style={{ display: 'flex', gap: '8px' }}>
          {foodTypes.map(ft => (
            <button
              key={ft.id}
              onClick={() => setSelectedFoodType(ft.id)}
              style={{
                width: 'auto',
                padding: '6px 12px',
                fontSize: '14px',
                backgroundColor: selectedFoodType === ft.id ? '#5f6368' : '#eee',
                color: selectedFoodType === ft.id ? 'white' : 'black',
                cursor: 'pointer'
              }}
            >
              {ft.label}
            </button>
          ))}
        </div>

        <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '14px', fontWeight: 'bold' }}>
          <input
            type="checkbox"
            checked={currentConfig?.isEnabled ?? true}
            onChange={(e) => handleToggleConfig(e.target.checked)}
          />
          Category Enabled
        </label>

        <button
          onClick={handleAddClick}
          style={{ width: 'auto', padding: '6px 12px', fontSize: '14px', marginLeft: 'auto', cursor: 'pointer' }}
        >
          Add Food
        </button>
      </div>

      {isFormOpen ? (
        <FoodForm
          initialFood={editingFood}
          defaultType={selectedFoodType}
          defaultDayType={selectedDayType}
          onSubmit={handleUpsert}
          onCancel={() => setIsFormOpen(false)}
          isLoading={state.isLoading}
        />
      ) : (
        <FoodList
          foods={filteredFoods}
          onDelete={deleteFood}
          onEdit={handleEditClick}
          onToggleActive={(f) => upsertFood({
            id: f.id, name: f.name, nameAr: f.nameAr, type: f.type, dayType: f.dayType,
            isActive: !f.isActive, isVisibleToUsers: f.isVisibleToUsers, displayOrder: f.displayOrder
          } as AdminFoodItemJs)}
          onToggleVisible={(f) => upsertFood({
            id: f.id, name: f.name, nameAr: f.nameAr, type: f.type, dayType: f.dayType,
            isActive: f.isActive, isVisibleToUsers: !f.isVisibleToUsers, displayOrder: f.displayOrder
          } as AdminFoodItemJs)}
        />
      )}
    </div>
  );
};
