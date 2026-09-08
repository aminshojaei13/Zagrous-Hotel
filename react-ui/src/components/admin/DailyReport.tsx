import React from 'react';
import { AdminStateJs } from '../../kotlin/adminBridge';

interface DailyReportProps {
  state: AdminStateJs;
  onSelectDate: (date: string) => void;
}

export const DailyReport: React.FC<DailyReportProps> = ({ state, onSelectDate }) => {
  const reportDate = state.selectedReportDate || "";
  const summary = state.dailyReportSummary;

  return (
    <div style={{ marginTop: '20px' }}>
      <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '24px' }}>
        <input
          type="text"
          value={reportDate}
          onChange={(e) => onSelectDate(e.target.value)}
          placeholder="YYYY/MM/DD"
          style={{ width: '150px', padding: '8px' }}
        />
        <div style={{ fontSize: '14px', fontWeight: 'bold' }}>Daily Report Summary</div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '30px' }}>
        <div className="report-box" style={{ padding: '16px', border: '1px solid #eee', borderRadius: '8px' }}>
          <h4 style={{ margin: '0 0 10px 0' }}>Breakfast</h4>
          <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary-color)' }}>{summary.totalBreakfast}</div>
          <div style={{ fontSize: '12px', color: '#888' }}>Total persons</div>
        </div>

        <div className="report-box" style={{ padding: '16px', border: '1px solid #eee', borderRadius: '8px' }}>
          <h4 style={{ margin: '0 0 10px 0' }}>Lunch</h4>
          {Array.from(summary.lunchOrders).map((item) => (
            <div key={item.foodId} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px', marginBottom: '4px' }}>
              <span>{item.foodName}</span>
              <span style={{ fontWeight: 'bold' }}>{item.quantity}</span>
            </div>
          ))}
          <div style={{ marginTop: '8px', paddingTop: '8px', borderTop: '1px solid #eee', fontWeight: 'bold' }}>
            Total: {summary.totalLunch}
          </div>
        </div>

        <div className="report-box" style={{ padding: '16px', border: '1px solid #eee', borderRadius: '8px' }}>
          <h4 style={{ margin: '0 0 10px 0' }}>Dinner</h4>
          {Array.from(summary.dinnerOrders).map((item) => (
            <div key={item.foodId} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px', marginBottom: '4px' }}>
              <span>{item.foodName}</span>
              <span style={{ fontWeight: 'bold' }}>{item.quantity}</span>
            </div>
          ))}
          <div style={{ marginTop: '8px', paddingTop: '8px', borderTop: '1px solid #eee', fontWeight: 'bold' }}>
            Total: {summary.totalDinner}
          </div>
        </div>
      </div>
    </div>
  );
};
