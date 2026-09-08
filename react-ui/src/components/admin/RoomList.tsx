import React from 'react';
import { AdminRoomJs } from '../../kotlin/adminBridge';

interface RoomListProps {
  rooms: AdminRoomJs[];
  onDelete: (id: string) => void;
  onEdit: (room: AdminRoomJs) => void;
}

export const RoomList: React.FC<RoomListProps> = ({ rooms, onDelete, onEdit }) => {
  if (rooms.length === 0) {
    return <div style={{ padding: '20px', textAlign: 'center', color: '#666' }}>No rooms found.</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {rooms.map((room) => (
        <div key={room.id || room.roomNumber} style={{
          padding: '16px',
          border: '1px solid #ddd',
          borderRadius: '8px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          backgroundColor: 'white'
        }}>
          <div>
            <div style={{ fontWeight: 'bold', fontSize: '18px' }}>Room {room.roomNumber}</div>
            <div style={{ fontSize: '14px', color: '#555' }}>
              Guest: {room.guestName || '(None)'} | Count: {room.guestCount}
            </div>
            <div style={{ fontSize: '12px', color: '#888' }}>
              {room.checkInDate} - {room.checkOutDate}
            </div>
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button onClick={() => onEdit(room)} style={{ width: 'auto', padding: '6px 12px', fontSize: '14px' }}>Edit</button>
            <button onClick={() => onDelete(room.id || room.roomNumber)} style={{ width: 'auto', padding: '6px 12px', fontSize: '14px', backgroundColor: '#d93025' }}>Delete</button>
          </div>
        </div>
      ))}
    </div>
  );
};
