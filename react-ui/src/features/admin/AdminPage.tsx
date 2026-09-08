import React, { useState } from 'react';
import { useAdminViewModel } from '../../hooks/useAdminViewModel';
import { RoomList } from '../../components/admin/RoomList';
import { RoomForm } from '../../components/admin/RoomForm';
import { MenuManager } from '../../components/admin/MenuManager';
import { ReservationList } from '../../components/admin/ReservationList';
import { DailyReport } from '../../components/admin/DailyReport';
import { AdminRoomJs } from '../../kotlin/adminBridge';

export const AdminPage: React.FC = () => {
  const {
    state, addRoom, updateRoomStay, deleteRoom, loadData,
    markLunchDelivered, markDinnerDelivered, selectReportDate
  } = useAdminViewModel();
  const [currentTab, setCurrentTab] = useState<'rooms' | 'menu' | 'reservations' | 'report'>('rooms');
  const [isRoomFormOpen, setIsRoomFormOpen] = useState(false);
  const [editingRoom, setEditingRoom] = useState<AdminRoomJs | undefined>(undefined);

  const handleAddRoomClick = () => {
    setEditingRoom(undefined);
    setIsRoomFormOpen(true);
  };

  const handleEditRoomClick = (room: AdminRoomJs) => {
    setEditingRoom(room);
    setIsRoomFormOpen(true);
  };

  const handleRoomSubmit = (room: any) => {
    if (editingRoom) {
      updateRoomStay(room);
    } else {
      addRoom(room);
    }
    setIsRoomFormOpen(false);
  };

  return (
    <div className="dashboard-container" style={{ padding: '20px' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1 style={{ margin: 0, fontSize: '24px', color: 'var(--primary-color)' }}>
          Hotel Zagrous - Admin Panel
        </h1>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={loadData} disabled={state.isLoading} style={{ width: 'auto', padding: '8px 16px' }}>Refresh</button>
        </div>
      </header>

      <nav style={{ display: 'flex', gap: '20px', borderBottom: '1px solid #ddd', marginBottom: '20px' }}>
        <button
          onClick={() => setCurrentTab('rooms')}
          style={{
            backgroundColor: 'transparent',
            color: currentTab === 'rooms' ? 'var(--primary-color)' : '#666',
            border: 'none',
            borderBottom: currentTab === 'rooms' ? '2px solid var(--primary-color)' : 'none',
            padding: '10px 0',
            borderRadius: 0,
            width: 'auto',
            fontWeight: 'bold',
            cursor: 'pointer'
          }}
        >
          Rooms
        </button>
        <button
          onClick={() => setCurrentTab('menu')}
          style={{
            backgroundColor: 'transparent',
            color: currentTab === 'menu' ? 'var(--primary-color)' : '#666',
            border: 'none',
            borderBottom: currentTab === 'menu' ? '2px solid var(--primary-color)' : 'none',
            padding: '10px 0',
            borderRadius: 0,
            width: 'auto',
            fontWeight: 'bold',
            cursor: 'pointer'
          }}
        >
          Menu
        </button>
        <button
          onClick={() => setCurrentTab('reservations')}
          style={{
            backgroundColor: 'transparent',
            color: currentTab === 'reservations' ? 'var(--primary-color)' : '#666',
            border: 'none',
            borderBottom: currentTab === 'reservations' ? '2px solid var(--primary-color)' : 'none',
            padding: '10px 0',
            borderRadius: 0,
            width: 'auto',
            fontWeight: 'bold',
            cursor: 'pointer'
          }}
        >
          Deliveries
        </button>
        <button
          onClick={() => setCurrentTab('report')}
          style={{
            backgroundColor: 'transparent',
            color: currentTab === 'report' ? 'var(--primary-color)' : '#666',
            border: 'none',
            borderBottom: currentTab === 'report' ? '2px solid var(--primary-color)' : 'none',
            padding: '10px 0',
            borderRadius: 0,
            width: 'auto',
            fontWeight: 'bold',
            cursor: 'pointer'
          }}
        >
          Daily Report
        </button>
      </nav>

      {state.error && <div className="error-message">{state.error}</div>}

      {currentTab === 'rooms' && (
        isRoomFormOpen ? (
          <RoomForm
            initialRoom={editingRoom}
            onSubmit={handleRoomSubmit}
            onCancel={() => setIsRoomFormOpen(false)}
            isLoading={state.isLoading}
          />
        ) : (
          <section style={{ marginTop: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <h2 style={{ margin: 0 }}>Room Management</h2>
              <button onClick={handleAddRoomClick} style={{ width: 'auto', padding: '8px 16px' }}>Add Room</button>
            </div>
            <RoomList
              rooms={Array.from(state.rooms)}
              onDelete={deleteRoom}
              onEdit={handleEditRoomClick}
            />
          </section>
        )
      )}

      {currentTab === 'menu' && <MenuManager />}

      {currentTab === 'reservations' && (
        <ReservationList
          state={state}
          onMarkLunch={markLunchDelivered}
          onMarkDinner={markDinnerDelivered}
        />
      )}

      {currentTab === 'report' && (
        <DailyReport
          state={state}
          onSelectDate={selectReportDate}
        />
      )}
    </div>
  );
};
