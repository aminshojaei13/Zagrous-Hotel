import React from 'react';
import { Button } from '../common/Button';
import { ErrorBanner } from '../common/ErrorBanner';
import { ReservationStateJs } from '../../kotlin/reservationBridge';

interface ConfirmSectionProps {
  state: ReservationStateJs;
  onConfirm: () => void;
}

export const ConfirmSection: React.FC<ConfirmSectionProps> = ({ state, onConfirm }) => {
  const isArabic = state.isArabic;

  return (
    <section style={{ marginTop: '40px', paddingBottom: '60px' }}>
      {state.error === 'reservation_success' && (
        <ErrorBanner variant="success" message={isArabic ? 'تم الحجز بنجاح' : 'رزرو با موفقیت ثبت شد'} />
      )}

      {state.error === 'reservation_failed' && (
        <ErrorBanner variant="error" message={isArabic ? 'فشل في تسجيل الحجز' : 'خطا در ثبت رزرو'} />
      )}

      <Button
        onClick={onConfirm}
        size="large"
        isLoading={state.isLoading}
        style={{ height: '64px', fontSize: '18px' }}
      >
        <span style={{ fontSize: '24px', marginLeft: '12px' }}>✅</span>
        {isArabic ? 'التأكيد النهائي وحجز الوجبات' : 'ثبت نهایی و تایید رزروها'}
      </Button>

      <div style={{ textAlign: 'center', marginTop: '16px', color: 'var(--on-surface-variant)', fontSize: '12px' }}>
        {isArabic ? 'يرجى مراجعة كافة الاختيارات قبل التأكيد' : 'لطفاً قبل تایید نهایی، تمامی انتخاب‌های خود را بازبینی فرمایید.'}
      </div>
    </section>
  );
};
