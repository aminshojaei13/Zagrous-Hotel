import React, { useState } from 'react';
import { useAdminViewModel } from '../../hooks/useAdminViewModel';
import { FoodList } from './FoodList';
import { FoodForm } from './FoodForm';
import { AdminFoodItemJs } from '../../kotlin/adminBridge';
import { FilterChip } from '../common/FilterChip';
import { Switch } from '../common/Switch';
import { Button } from '../common/Button';

export const MenuManager: React.FC = () => {
  const { state, upsertFood, deleteFood, updateMenuConfig } = useAdminViewModel();

  const [selectedDayType, setSelectedDayType] = useState('EVEN');
  const [selectedFoodType, setSelectedFoodType] = useState('LUNCH');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingFood, setEditingFood] = useState<AdminFoodItemJs | undefined>(undefined);

  const dayTypes = [
    { id: 'EVEN', label: 'زوج' },
    { id: 'ODD', label: 'فرد' },
    { id: 'FRIDAY', label: 'جمعه' }
  ];

  const foodTypes = [
    { id: 'LUNCH', label: 'ناهار' },
    { id: 'DINNER', label: 'شام' }
  ];

  const currentConfig = Array.from(state.menuConfigs).find(
    c => c.dayType === selectedDayType && c.foodType === selectedFoodType
  );

  const filteredFoods = Array.from(state.foods)
    .filter(f => f.dayType === selectedDayType && f.type === selectedFoodType)
    .sort((a, b) => a.displayOrder - b.displayOrder);

  const handleToggleConfig = (enabled: boolean) => {
    updateMenuConfig(selectedDayType, selectedFoodType, enabled);
  };

  return (
    <div>
      <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
        {dayTypes.map(dt => (
          <FilterChip
            key={dt.id}
            label={dt.label}
            selected={selectedDayType === dt.id}
            onClick={() => setSelectedDayType(dt.id)}
          />
        ))}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '24px', marginBottom: '32px', background: 'var(--surface)', padding: '16px', borderRadius: '16px', border: '1px solid var(--outline-variant)' }}>
        <div style={{ display: 'flex', gap: '8px' }}>
          {foodTypes.map(ft => (
            <FilterChip
              key={ft.id}
              label={ft.label}
              selected={selectedFoodType === ft.id}
              onClick={() => setSelectedFoodType(ft.id)}
            />
          ))}
        </div>

        <div style={{ flex: 1 }}></div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '14px' }}>وضعیت منو:</span>
          <Switch
            checked={currentConfig?.isEnabled ?? true}
            onChange={handleToggleConfig}
          />
          <span style={{ fontSize: '14px', fontWeight: 'bold', color: (currentConfig?.isEnabled ?? true) ? 'green' : 'red' }}>
            {(currentConfig?.isEnabled ?? true) ? 'فعال' : 'غیرفعال'}
          </span>
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2 style={{ margin: 0, fontSize: '20px' }}>لیست غذاها</h2>
        <Button
          variant="primary"
          size="small"
          onClick={() => { setEditingFood(undefined); setIsFormOpen(true); }}
        >
          + افزودن غذای جدید
        </Button>
      </div>

      {isFormOpen ? (
        <FoodForm
          initialFood={editingFood}
          defaultType={selectedFoodType}
          defaultDayType={selectedDayType}
          onSubmit={(f) => { upsertFood(f as AdminFoodItemJs); setIsFormOpen(false); }}
          onCancel={() => setIsFormOpen(false)}
          isLoading={state.isLoading}
        />
      ) : (
        <FoodList
          foods={filteredFoods}
          onDelete={deleteFood}
          onEdit={(f) => { setEditingFood(f); setIsFormOpen(true); }}
          onToggleActive={(f) => upsertFood({ ...f, isActive: !f.isActive } as AdminFoodItemJs)}
          onToggleVisible={(f) => upsertFood({ ...f, isVisibleToUsers: !f.isVisibleToUsers } as AdminFoodItemJs)}
        />
      )}
    </div>
  );
};
