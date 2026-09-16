import React from 'react';

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
    <div className="summary-card">
      <h2>{t.welcome}</h2>
      <p style={{ fontSize: '18px', marginBottom: '20px' }}>{t.guestName}</p>

      <div className="summary-row">
        <div className="detail-item">
          <span className="detail-label">{t.roomNumber}</span>
          <span className="detail-value">{room.roomNumber}</span>
        </div>

        <div className="detail-item">
          <span className="detail-label">{t.guestCount}</span>
          <span className="detail-value">{t.persons(room.guestCount)}</span>
        </div>

        <div className="detail-item">
          <span className="detail-label">{t.stayPeriod}</span>
          <span className="detail-value">
            {room.checkInDate} {t.to} {room.checkOutDate}
          </span>
        </div>
      </div>
    </div>
  );
};
