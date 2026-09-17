import React, { useState } from 'react';
import { useReservationViewModel } from '../../hooks/useReservationViewModel';

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
      <div className="m3-card logged-in-container">
        <h1>خوش آمدید!</h1>
        <p className="success-message">ورود موفق به اتاق {state.roomNumber}</p>
        <p>جزئیات رزرو شما در حال بارگذاری است.</p>
      </div>
    );
  }

  return (
    <div className="m3-card">
      <div className="login-header">
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '24px' }}>
          <img src="/logo.svg" alt="Hotel Zagrous" style={{ width: '100px', height: '100px' }} />
        </div>
        <h1 style={{ fontSize: '28px', fontWeight: 'bold' }}>خوش آمدید</h1>
        <p style={{ color: 'var(--on-surface-variant)' }}>سامانه رزرو غذای هتل زاگرس</p>
      </div>

      {state.error && (
        <div className="error-banner" style={{ padding: '12px', fontSize: '14px', textAlign: 'center' }}>
          {state.error === 'error_room_not_found' ? 'اتاقی با این شماره یافت نشد' :
           state.error === 'error_id_mismatch' ? 'کد شناسایی نامعتبر است' :
           state.error === 'error_fill_fields' ? 'لطفاً تمامی فیلدها را پر کنید' :
           state.error === 'error_connection' ? 'خطا در برقراری ارتباط با سرور' : state.error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="m3-input-group">
          <label htmlFor="roomNumber">شماره اتاق</label>
          <input
            id="roomNumber"
            type="text"
            value={roomNumber}
            onChange={(e) => setRoomNumber(e.target.value)}
            disabled={state.isLoading}
            placeholder="مثلاً 101"
            required
            style={{ textAlign: 'center' }}
          />
        </div>

        <div className="m3-input-group">
          <label htmlFor="id">کد شناسایی</label>
          <input
            id="id"
            type="password"
            value={id}
            onChange={(e) => setId(e.target.value)}
            disabled={state.isLoading}
            placeholder="کد شناسایی"
            required
            style={{ textAlign: 'center' }}
          />
        </div>

        <button type="submit" className="m3-button" disabled={state.isLoading || !roomNumber || !id} style={{ height: '56px' }}>
          {state.isLoading ? 'در حال بررسی...' : 'ورود به سامانه'}
        </button>

        <p style={{ textAlign: 'center', marginTop: '32px', fontSize: '12px', color: 'var(--outline)' }}>
          در صورت بروز مشکل به پذیرش مراجعه فرمایید
        </p>
      </form>
    </div>
  );
};
