import React, { useState } from 'react';
import { useAdminViewModel } from '../../hooks/useAdminViewModel';
import { RoomList } from '../../components/admin/RoomList';
import { RoomForm } from '../../components/admin/RoomForm';
import { AdminRoomJs } from '../../kotlin/adminBridge';

export const AdminPage: React.FC = () => {
  const { state, addRoom, updateRoomStay, deleteRoom, loadData } = useAdminViewModel();
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingRoom, setEditingRoom] = useState<AdminRoomJs | undefined>(undefined);

  const handleAddClick = () => {
    setEditingRoom(undefined);
    setIsFormOpen(true);
  };

  const handleEditClick = (room: AdminRoomJs) => {
    setEditingRoom(room);
    setIsFormOpen(true);
  };

  const handleSubmit = (room: any) => {
    if (editingRoom) {
      updateRoomStay(room);
    } else {
      addRoom(room);
    }
    setIsFormOpen(false);
  };

  return (
    <div className="dashboard-container" style={{ padding: '20px' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ margin: 0, fontSize: '24px', color: 'var(--primary-color)' }}>
          Hotel Zagrous - Admin Panel
        </h1>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={loadData} disabled={state.isLoading} style={{ width: 'auto', padding: '8px 16px' }}>Refresh</button>
          <button onClick={handleAddClick} style={{ width: 'auto', padding: '8px 16px' }}>Add Room</button>
        </div>
      </header>

      {state.error && <div className="error-message">{state.error}</div>}

      {isFormOpen ? (
        <RoomForm
          initialRoom={editingRoom}
          onSubmit={handleSubmit}
          onCancel={() => setIsFormOpen(false)}
          isLoading={state.isLoading}
        />
      ) : (
        <section style={{ marginTop: '20px' }}>
          <h2 style={{ marginBottom: '16px' }}>Room Management</h2>
          <RoomList
            rooms={Array.from(state.rooms)}
            onDelete={deleteRoom}
            onEdit={handleEditClick}
          />
        </section>
      )}
    </div>
  );
};
