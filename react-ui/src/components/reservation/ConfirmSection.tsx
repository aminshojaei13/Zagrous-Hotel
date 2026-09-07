import React from 'react';
import { ReservationStateJs } from '../../kotlin/reservationBridge';

interface ConfirmSectionProps {
  state: ReservationStateJs;
  onConfirm: () => void;
}

export const ConfirmSection: React.FC<ConfirmSectionProps> = ({ state, onConfirm }) => {
  const isArabic = state.isArabic;

  const t = {
    title: isArabic ? 'تأكيد الحجز' : 'تایید نهایی رزرو',
    summary: isArabic ? 'ملخص الحجز:' : 'خلاصه رزرو:',
    totalDays: (n: number) => isArabic ? `إجمالي الأيام: ${n}` : `مجموع روزها: ${n}`,
    confirmBtn: isArabic ? 'تأكيد و حفظ' : 'ثبت نهایی و تایید',
    loading: isArabic ? 'جاري الحفظ...' : 'در حال ثبت...',
    success: isArabic ? 'تم الحجز بنجاح' : 'رزرو با موفقیت ثبت شد',
    failed: isArabic ? 'فشل الحجز' : 'خطا در ثبت رزرو',
  };

  const isSuccess = state.error === 'reservation_success';
  const isError = state.error === 'reservation_failed';

  return (
    <div className="confirm-section" style={{
      marginTop: '32px',
      padding: '24px',
      backgroundColor: '#f8f9fa',
      borderRadius: '12px',
      border: '1px solid #dee2e6',
      textAlign: 'center'
    }}>
      <h3 style={{ margin: '0 0 16px 0' }}>{t.title}</h3>

      <p style={{ color: '#5f6368', marginBottom: '24px' }}>
        {t.totalDays(state.stayDays.length)}
      </p>

      {isSuccess && <div className="success-message">{t.success}</div>}
      {isError && <div className="error-message">{t.failed}</div>}
      {state.error && !isSuccess && !isError && <div className="error-message">{state.error}</div>}

      <button
        onClick={onConfirm}
        disabled={state.isLoading || isSuccess}
        style={{
          height: '56px',
          fontSize: '18px',
          backgroundColor: isSuccess ? 'var(--success-color)' : 'var(--primary-color)'
        }}
      >
        {state.isLoading ? t.loading : isSuccess ? '✓' : t.confirmBtn}
      </button>
    </div>
  );
};
