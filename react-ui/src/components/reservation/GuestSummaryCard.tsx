import React from 'react';
import { Card } from '../common/Card';

interface RoomJs {
  roomNumber: string;
  guestName: string;
  guestCount: number;
  checkInDate: string;
  checkOutDate: string;
}

interface GuestSummaryCardProps {
  room: RoomJs;
  isArabic: boolean;
}

export const GuestSummaryCard: React.FC<GuestSummaryCardProps> = ({ room, isArabic }) => {
  const t = {
    welcome: isArabic ? 'أهلاً بك' : 'خوش آمدید',
    guestName: isArabic ? `السيد ${room.guestName}` : `مهمان گرامی، جناب ${room.guestName}`,
    roomNumber: isArabic ? 'رقم الغرفة' : 'شماره اتاق',
    guestCount: isArabic ? 'عدد الضيوف' : 'تعداد نفرات',
    stayPeriod: isArabic ? 'فترة الإقامة' : 'بازه اقامت',
    to: isArabic ? 'إلى' : 'الی',
    persons: (count: number) => isArabic ? `${count} أشخاص` : `${count} نفر`,
  };

  return (
    <Card variant="summary" style={{ padding: '24px', marginBottom: '24px' }}>
      <h2 style={{ margin: '0 0 8px 0', fontSize: '24px' }}>{t.welcome}</h2>
      <p style={{ fontSize: '18px', margin: '0 0 24px 0', opacity: 0.9 }}>{t.guestName}</p>

      <div className="summary-row" style={{ display: 'flex', gap: '24px', flexWrap: 'wrap' }}>
        <div className="detail-item">
          <div style={{ fontSize: '12px', opacity: 0.8, marginBottom: '4px' }}>{t.roomNumber}</div>
          <div style={{ fontSize: '16px', fontWeight: 'bold' }}>{room.roomNumber}</div>
        </div>

        <div className="detail-item">
          <div style={{ fontSize: '12px', opacity: 0.8, marginBottom: '4px' }}>{t.guestCount}</div>
          <div style={{ fontSize: '16px', fontWeight: 'bold' }}>{t.persons(room.guestCount)}</div>
        </div>

        <div className="detail-item">
          <div style={{ fontSize: '12px', opacity: 0.8, marginBottom: '4px' }}>{t.stayPeriod}</div>
          <div style={{ fontSize: '16px', fontWeight: 'bold' }}>
            {room.checkInDate} {t.to} {room.checkOutDate}
          </div>
        </div>
      </div>
    </Card>
  );
};
