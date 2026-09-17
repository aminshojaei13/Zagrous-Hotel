import React, { useState } from 'react';
import { useReservationViewModel } from '../../hooks/useReservationViewModel';
import { Button } from '../common/Button';
import { TextField } from '../common/TextField';
import { Card } from '../common/Card';
import { ErrorBanner } from '../common/ErrorBanner';

export const LoginForm: React.FC = () => {
  const { state, updateCredentials, login } = useReservationViewModel();

  const [roomNumber, setRoomNumber] = useState('');
  const [id, setId] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!roomNumber || !id) return;

    updateCredentials(roomNumber, id);
    login();
  };

  if (state.isLoggedIn) {
    return (
      <Card className="logged-in-container" style={{ maxWidth: '400px', padding: '32px', textAlign: 'center' }}>
        <h1 style={{ color: 'var(--primary-color)' }}>خوش آمدید!</h1>
        <p style={{ margin: '24px 0' }}>ورود موفق به اتاق {state.roomNumber}</p>
        <p style={{ color: 'var(--on-surface-variant)' }}>جزئیات رزرو شما در حال بارگذاری است.</p>
      </Card>
    );
  }

  return (
    <Card variant="elevated" style={{ maxWidth: '400px', padding: '32px' }}>
      <div style={{ textAlign: 'center', marginBottom: '32px' }}>
        <img src="/logo.svg" alt="Hotel Zagrous" style={{ width: '100px', height: '100px', marginBottom: '16px' }} />
        <h1 style={{ fontSize: '28px', fontWeight: 'bold', margin: '0 0 8px' }}>خوش آمدید</h1>
        <p style={{ color: 'var(--on-surface-variant)', margin: 0 }}>سامانه رزرو غذای هتل زاگرس</p>
      </div>

      {state.error && (
        <ErrorBanner
          message={
            state.error === 'error_room_not_found' ? 'اتاقی با این شماره یافت نشد' :
            state.error === 'error_id_mismatch' ? 'کد شناسایی نامعتبر است' :
            state.error === 'error_fill_fields' ? 'لطفاً تمامی فیلدها را پر کنید' :
            state.error === 'error_connection' ? 'خطا در برقراری ارتباط با سرور' : state.error
          }
        />
      )}

      <form onSubmit={handleSubmit}>
        <TextField
          id="roomNumber"
          label="شماره اتاق"
          type="text"
          value={roomNumber}
          onChange={(e) => setRoomNumber(e.target.value)}
          disabled={state.isLoading}
          placeholder="مثلاً 101"
          required
          style={{ textAlign: 'center' }}
          leadingIcon="🚪"
        />

        <TextField
          id="id"
          label="کد شناسایی"
          type="password"
          value={id}
          onChange={(e) => setId(e.target.value)}
          disabled={state.isLoading}
          placeholder="کد شناسایی"
          required
          style={{ textAlign: 'center' }}
          leadingIcon="🔑"
        />

        <Button
          type="submit"
          size="large"
          isLoading={state.isLoading}
          disabled={!roomNumber || !id}
          style={{ marginTop: '16px' }}
        >
          ورود به سامانه
        </Button>

        <p style={{ textAlign: 'center', marginTop: '32px', fontSize: '12px', color: 'var(--outline)' }}>
          در صورت بروز مشکل به پذیرش مراجعه فرمایید
        </p>
      </form>
    </Card>
  );
};
