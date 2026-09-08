import React, { useState, useEffect } from 'react';
import { AdminRoomJs } from '../../kotlin/adminBridge';

interface RoomFormProps {
  initialRoom?: AdminRoomJs;
  onSubmit: (room: any) => void;
  onCancel: () => void;
  isLoading: boolean;
}

export const RoomForm: React.FC<RoomFormProps> = ({ initialRoom, onSubmit, onCancel, isLoading }) => {
  const [formData, setFormData] = useState({
    roomNumber: '',
    guestName: '',
    identificationId: '',
    guestCount: 1,
    checkInDate: '',
    checkOutDate: '',
  });

  useEffect(() => {
    if (initialRoom) {
      setFormData({
        roomNumber: initialRoom.roomNumber,
        guestName: initialRoom.guestName,
        identificationId: initialRoom.identificationId,
        guestCount: initialRoom.guestCount,
        checkInDate: initialRoom.checkInDate,
        checkOutDate: initialRoom.checkOutDate,
      });
    }
  }, [initialRoom]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      ...initialRoom, // preserve id and millis if editing
      ...formData,
      hasBreakfast: initialRoom?.hasBreakfast ?? false,
      breakfastCount: initialRoom?.breakfastCount ?? 0,
      checkInEpochMillis: initialRoom?.checkInEpochMillis ?? 0,
      checkOutEpochMillis: initialRoom?.checkOutEpochMillis ?? 0,
    });
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
      <h3>{initialRoom ? 'Edit Room' : 'Add Room'}</h3>

      <div className="form-group">
        <label>Room Number</label>
        <input
          type="text"
          value={formData.roomNumber}
          onChange={e => setFormData({...formData, roomNumber: e.target.value})}
          required
        />
      </div>

      <div className="form-group">
        <label>Guest Name</label>
        <input
          type="text"
          value={formData.guestName}
          onChange={e => setFormData({...formData, guestName: e.target.value})}
        />
      </div>

      <div className="form-group">
        <label>Guest Count</label>
        <input
          type="number"
          min="1"
          max="10"
          value={formData.guestCount}
          onChange={e => setFormData({...formData, guestCount: parseInt(e.target.value)})}
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
