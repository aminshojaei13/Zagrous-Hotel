import React from 'react';
import { AdminStateJs } from '../../kotlin/adminBridge';
import { Button } from '../common/Button';
import { Card } from '../common/Card';
import { TextField } from '../common/TextField';

interface DailyReportProps {
  state: AdminStateJs;
  onSelectDate: (date: string) => void;
}

export const DailyReport: React.FC<DailyReportProps> = ({ state, onSelectDate }) => {
  const reportDate = state.selectedReportDate || "";
  const summary = state.dailyReportSummary;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div>
      <Card variant="outlined" shape="medium" className="no-print" style={{ padding: '24px', marginBottom: '24px' }}>
        <div style={{ display: 'flex', gap: '16px', alignItems: 'flex-end' }}>
          <div style={{ width: '200px' }}>
            <TextField
              label="تاریخ گزارش"
              value={reportDate}
              onChange={(e) => onSelectDate(e.target.value)}
              placeholder="YYYY/MM/DD"
              leadingIcon="📅"
            />
          </div>
          <div style={{ flex: 1 }}></div>
          <Button
            variant="outlined"
            onClick={handlePrint}
            style={{ width: 'auto' }}
          >
            🖨️ چاپ گزارش برای آشپزخانه
          </Button>
        </div>
      </Card>

      {/* Printable Area */}
      <div className="print-only printable-report">
        <div style={{ textAlign: 'center', marginBottom: '32px', borderBottom: '2px solid black', paddingBottom: '16px' }}>
          <h1 style={{ margin: '0 0 8px 0' }}>گزارش آماری غذای هتل زاگرس</h1>
          <div style={{ fontSize: '18px' }}>تاریخ: {reportDate}</div>
        </div>

        <table className="report-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th style={{ border: '1px solid black', padding: '12px', backgroundColor: '#f2f2f2' }}>وعده</th>
              <th style={{ border: '1px solid black', padding: '12px', backgroundColor: '#f2f2f2' }}>نام غذا / منو</th>
              <th style={{ border: '1px solid black', padding: '12px', backgroundColor: '#f2f2f2' }}>تعداد نهایی</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontWeight: 'bold' }}>صبحانه</td>
              <td style={{ border: '1px solid black', padding: '12px' }}>بوفه سلف سرویس</td>
              <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontSize: '18px', fontWeight: 'bold' }}>{summary.totalBreakfast}</td>
            </tr>

            {summary.lunchOrders.length > 0 ? (
              Array.from(summary.lunchOrders).map((item, index) => (
                <tr key={`lunch-${item.foodId}`}>
                  {index === 0 && <td rowSpan={summary.lunchOrders.length + 1} style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontWeight: 'bold' }}>ناهار</td>}
                  <td style={{ border: '1px solid black', padding: '12px' }}>{item.foodName}</td>
                  <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center' }}>{item.quantity}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontWeight: 'bold' }}>ناهار</td>
                <td style={{ border: '1px solid black', padding: '12px' }}>-</td>
                <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center' }}>0</td>
              </tr>
            )}
            <tr style={{ fontWeight: 'bold', backgroundColor: '#fafafa' }}>
              <td style={{ border: '1px solid black', padding: '12px', textAlign: 'left' }}>جمع کل ناهار:</td>
              <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontSize: '18px' }}>{summary.totalLunch}</td>
            </tr>

            {summary.dinnerOrders.length > 0 ? (
              Array.from(summary.dinnerOrders).map((item, index) => (
                <tr key={`dinner-${item.foodId}`}>
                  {index === 0 && <td rowSpan={summary.dinnerOrders.length + 1} style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontWeight: 'bold' }}>شام</td>}
                  <td style={{ border: '1px solid black', padding: '12px' }}>{item.foodName}</td>
                  <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center' }}>{item.quantity}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontWeight: 'bold' }}>شام</td>
                <td style={{ border: '1px solid black', padding: '12px' }}>-</td>
                <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center' }}>0</td>
              </tr>
            )}
            <tr style={{ fontWeight: 'bold', backgroundColor: '#fafafa' }}>
              <td style={{ border: '1px solid black', padding: '12px', textAlign: 'left' }}>جمع کل شام:</td>
              <td style={{ border: '1px solid black', padding: '12px', textAlign: 'center', fontSize: '18px' }}>{summary.totalDinner}</td>
            </tr>
          </tbody>
        </table>

        <div style={{ marginTop: '40px', display: 'flex', justifyContent: 'space-between' }}>
          <div>امضاء مدیر داخلی: .......................</div>
          <div>امضاء سرآشپز: .......................</div>
        </div>
      </div>

      {/* Screen View (Summary Boxes) */}
      <div className="no-print" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
        <Card variant="elevated" shape="medium" style={{ padding: '24px', textAlign: 'center' }}>
          <h4 style={{ margin: '0 0 16px 0', color: 'var(--on-surface-variant)' }}>مجموع صبحانه</h4>
          <div style={{ fontSize: '48px', fontWeight: 'bold', color: 'var(--primary-color)' }}>{summary.totalBreakfast}</div>
          <div style={{ fontSize: '14px', color: 'var(--outline)', marginTop: '8px' }}>تعداد نفرات</div>
        </Card>

        <Card variant="elevated" shape="medium" style={{ padding: '24px' }}>
          <h4 style={{ margin: '0 0 16px 0', color: 'var(--on-surface-variant)', textAlign: 'center' }}>آمار ناهار</h4>
          {Array.from(summary.lunchOrders).map((item) => (
            <div key={item.foodId} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid var(--background)' }}>
              <span>{item.foodName}</span>
              <span style={{ fontWeight: 'bold' }}>{item.quantity}</span>
            </div>
          ))}
          <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '2px solid var(--primary-container)', fontWeight: 'bold', display: 'flex', justifyContent: 'space-between' }}>
            <span>جمع کل ناهار:</span>
            <span>{summary.totalLunch}</span>
          </div>
        </Card>

        <Card variant="elevated" shape="medium" style={{ padding: '24px' }}>
          <h4 style={{ margin: '0 0 16px 0', color: 'var(--on-surface-variant)', textAlign: 'center' }}>آمار شام</h4>
          {Array.from(summary.dinnerOrders).map((item) => (
            <div key={item.foodId} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid var(--background)' }}>
              <span>{item.foodName}</span>
              <span style={{ fontWeight: 'bold' }}>{item.quantity}</span>
            </div>
          ))}
          <div style={{ marginTop: '16px', paddingTop: '16px', borderTop: '2px solid var(--primary-container)', fontWeight: 'bold', display: 'flex', justifyContent: 'space-between' }}>
            <span>جمع کل شام:</span>
            <span>{summary.totalDinner}</span>
          </div>
        </Card>
      </div>
    </div>
  );
};
