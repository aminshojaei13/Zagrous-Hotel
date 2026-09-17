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

  const handleRoomSubmit = (room: any) => {
    if (editingRoom) {
      updateRoomStay(room);
    } else {
      addRoom(room);
    }
    setIsRoomFormOpen(false);
  };

  const navItems = [
    { id: 'rooms', label: 'مدیریت اتاق‌ها', icon: '🛏️' },
    { id: 'reservations', label: 'گزارش رزرو غذا', icon: '🍴' },
    { id: 'report', label: 'گزارش و چاپ روزانه', icon: '📝' },
    { id: 'menu', label: 'مدیریت منوی غذا', icon: '🍱' },
  ];

  return (
    <div className="admin-layout" dir="rtl">
      <aside className="admin-sidebar no-print">
        <div style={{ padding: '0 16px 32px', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '32px' }}>🏨</span>
          <div style={{ fontWeight: 'bold', fontSize: '20px', color: 'var(--primary-color)' }}>هتل زاگرس</div>
        </div>

        <nav>
          {navItems.map(item => (
            <div
              key={item.id}
              className={`nav-item ${currentTab === item.id ? 'active' : ''}`}
              onClick={() => {
                setCurrentTab(item.id as any);
                setIsRoomFormOpen(false);
              }}
            >
              <span>{item.icon}</span>
              <span>{item.label}</span>
            </div>
          ))}
        </nav>

        <div style={{ marginTop: 'auto', padding: '16px' }}>
          <button onClick={loadData} className="m3-button" style={{ fontSize: '14px', padding: '8px' }}>
            بروزرسانی داده‌ها
          </button>
        </div>
      </aside>

      <main className="admin-content">
        <header style={{ marginBottom: '32px' }}>
          <h1 style={{ margin: 0, fontSize: '28px' }}>
            {navItems.find(i => i.id === currentTab)?.label}
          </h1>
        </header>

        {state.error && <div className="error-banner">{state.error}</div>}

        {currentTab === 'rooms' && (
          isRoomFormOpen ? (
            <RoomForm
              initialRoom={editingRoom}
              onSubmit={handleRoomSubmit}
              onCancel={() => setIsRoomFormOpen(false)}
              isLoading={state.isLoading}
            />
          ) : (
            <div>
              <button
                onClick={() => { setEditingRoom(undefined); setIsRoomFormOpen(true); }}
                className="m3-button"
                style={{ width: 'auto', marginBottom: '24px' }}
              >
                + افزودن اتاق جدید
              </button>
              <RoomList
                rooms={Array.from(state.rooms)}
                onDelete={deleteRoom}
                onEdit={(r) => { setEditingRoom(r); setIsRoomFormOpen(true); }}
              />
            </div>
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
      </main>
    </div>
  );
};
