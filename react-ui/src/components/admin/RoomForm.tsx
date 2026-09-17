import React, { useState } from 'react';
import { AdminRoomJs } from '../../kotlin/adminBridge';
import { Card } from '../common/Card';
import { TextField } from '../common/TextField';
import { Button } from '../common/Button';
import { Checkbox } from '../common/Checkbox';

interface RoomFormProps {
  initialRoom?: AdminRoomJs;
  onSubmit: (room: Partial<AdminRoomJs>) => void;
  onCancel: () => void;
  isLoading: boolean;
}

export const RoomForm: React.FC<RoomFormProps> = ({ initialRoom, onSubmit, onCancel, isLoading }) => {
  const [roomNumber, setRoomNumber] = useState(initialRoom?.roomNumber || '');
  const [guestName, setGuestName] = useState(initialRoom?.guestName || '');
  const [identificationId, setIdentificationId] = useState(initialRoom?.identificationId || '');
  const [guestCount, setGuestCount] = useState(initialRoom?.guestCount?.toString() || '1');
  const [hasBreakfast, setHasBreakfast] = useState(initialRoom?.hasBreakfast ?? true);
  const [breakfastCount, setBreakfastCount] = useState(initialRoom?.breakfastCount?.toString() || '1');
  const [checkIn, setCheckIn] = useState(initialRoom?.checkInDate || '');
  const [checkOut, setCheckOut] = useState(initialRoom?.checkOutDate || '');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      id: initialRoom?.id || '',
      roomNumber,
      guestName,
      identificationId,
      guestCount: parseInt(guestCount) || 1,
      hasBreakfast,
      breakfastCount: parseInt(breakfastCount) || 1,
      checkInDate: checkIn,
      checkOutDate: checkOut,
      checkInEpochMillis: initialRoom?.checkInEpochMillis || 0,
      checkOutEpochMillis: initialRoom?.checkOutEpochMillis || 0
    });
  };

  return (
    <Card variant="outlined" shape="medium" style={{ padding: '32px', maxWidth: '600px', margin: '0 auto' }}>
      <form onSubmit={handleSubmit}>
        <h2 style={{ marginTop: 0, marginBottom: '24px' }}>{initialRoom ? 'ویرایش اطلاعات اتاق' : 'افزودن اتاق جدید'}</h2>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <TextField label="شماره اتاق" value={roomNumber} onChange={(e) => setRoomNumber(e.target.value)} required />
          <TextField label="نام مهمان" value={guestName} onChange={(e) => setGuestName(e.target.value)} required />
        </div>

        <TextField label="کد شناسایی (پسورد ورود)" value={identificationId} onChange={(e) => setIdentificationId(e.target.value)} required />

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', alignItems: 'center' }}>
          <TextField label="تعداد نفرات" type="number" value={guestCount} onChange={(e) => setGuestCount(e.target.value)} required />
          <div style={{ paddingTop: '20px' }}>
            <Checkbox label="شامل صبحانه" checked={hasBreakfast} onChange={setHasBreakfast} />
          </div>
        </div>

        {hasBreakfast && (
          <TextField label="تعداد صبحانه" type="number" value={breakfastCount} onChange={(e) => setBreakfastCount(e.target.value)} required />
        )}

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
          <TextField label="تاریخ ورود" placeholder="YYYY/MM/DD" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} required />
          <TextField label="تاریخ خروج" placeholder="YYYY/MM/DD" value={checkOut} onChange={(e) => setCheckOut(e.target.value)} required />
        </div>

        <div style={{ display: 'flex', gap: '12px', marginTop: '32px' }}>
          <Button type="submit" isLoading={isLoading} style={{ flex: 1 }}>ذخیره اطلاعات</Button>
          <Button type="button" variant="outlined" onClick={onCancel} style={{ flex: 1 }}>انصراف</Button>
        </div>
      </form>
    </Card>
  );
};
