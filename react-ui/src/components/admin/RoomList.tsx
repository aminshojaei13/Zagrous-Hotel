import React from 'react';
import { AdminRoomJs } from '../../kotlin/adminBridge';
import { Card } from '../common/Card';
import { Button } from '../common/Button';

interface RoomListProps {
  rooms: AdminRoomJs[];
  onDelete: (id: string) => void;
  onEdit: (room: AdminRoomJs) => void;
}

export const RoomList: React.FC<RoomListProps> = ({ rooms, onDelete, onEdit }) => {
  if (rooms.length === 0) {
    return (
      <Card variant="outlined" shape="medium" style={{ padding: '40px', textAlign: 'center', color: 'var(--on-surface-variant)' }}>
        هیچ اتاقی تعریف نشده است.
      </Card>
    );
  }

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }}>
      {rooms.map((room) => (
        <Card key={room.id || room.roomNumber} variant="elevated" shape="medium" style={{ padding: '20px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <div style={{ fontWeight: 'bold', fontSize: '20px', color: 'var(--primary-color)' }}>اتاق {room.roomNumber}</div>
              <div style={{ fontSize: '16px', marginTop: '8px' }}>{room.guestName || '(بدون مهمان)'}</div>
              <div style={{ fontSize: '13px', color: 'var(--on-surface-variant)', marginTop: '4px' }}>
                تعداد نفرات: {room.guestCount} | {room.hasBreakfast ? `صبحانه: ${room.breakfastCount} نفر` : 'بدون صبحانه'}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--outline)', marginTop: '8px' }}>
                {room.checkInDate} الی {room.checkOutDate}
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '8px', marginTop: '20px', borderTop: '1px solid var(--surface-variant)', paddingTop: '16px' }}>
            <Button variant="outlined" size="small" onClick={() => onEdit(room)} style={{ flex: 1 }}>ویرایش</Button>
            <Button variant="outlined" size="small" onClick={() => onDelete(room.id || room.roomNumber)} style={{ flex: 1, color: 'var(--error)', borderColor: 'var(--error)' }}>حذف</Button>
          </div>
        </Card>
      ))}
    </div>
  );
};
