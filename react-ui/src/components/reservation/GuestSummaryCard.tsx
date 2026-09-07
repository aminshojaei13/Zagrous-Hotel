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
    guestName: isArabic ? `السيد ${room.guestName}` : `جناب ${room.guestName}`,
    roomNumber: isArabic ? 'رقم الغرفة' : 'شماره اتاق',
    guestCount: isArabic ? 'عدد الضيوف' : 'تعداد مهمان',
    stayPeriod: isArabic ? 'فترة الإقامة' : 'بازه اقامت',
    to: isArabic ? 'إلى' : 'الی',
    persons: (count: number) => isArabic ? `${count} أشخاص` : `${count} نفر`,
  };

  return (
    <div className="summary-card" dir={isArabic ? 'rtl' : 'rtl'}> {/* Project seems to use RTL for both */}
      <h2>{t.welcome}, {t.guestName}</h2>

      <div className="summary-details">
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
