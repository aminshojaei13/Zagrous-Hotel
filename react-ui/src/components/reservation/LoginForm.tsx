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
      <div className="login-card logged-in-container">
        <h1>Welcome!</h1>
        <p className="success-message">Successfully logged into Room {state.roomNumber}</p>
        <p>Your reservation details will appear here soon.</p>
        {/* Logout placeholder - no business logic for logout yet */}
        <button style={{ marginTop: '20px', backgroundColor: '#5f6368' }} disabled>
          Logout (Coming Soon)
        </button>
      </div>
    );
  }

  return (
    <div className="login-card">
      <h1>Hotel Zagrous</h1>
      <p style={{ textAlign: 'center', marginBottom: '30px', color: '#5f6368' }}>Guest Reservation System</p>

      {state.error && (
        <div className="error-message">
          {state.error === 'error_room_not_found' ? 'Room not found' :
           state.error === 'error_id_mismatch' ? 'Identification ID does not match' :
           state.error === 'error_fill_fields' ? 'Please fill all fields' :
           state.error === 'error_connection' ? 'Connection error' : state.error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="roomNumber">Room Number</label>
          <input
            id="roomNumber"
            type="text"
            value={roomNumber}
            onChange={(e) => setRoomNumber(e.target.value)}
            disabled={state.isLoading}
            placeholder="e.g. 101"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="id">Identification ID</label>
          <input
            id="id"
            type="password"
            value={id}
            onChange={(e) => setId(e.target.value)}
            disabled={state.isLoading}
            placeholder="Your ID"
            required
          />
        </div>

        <button type="submit" disabled={state.isLoading || !roomNumber || !id}>
          {state.isLoading ? 'Logging in...' : 'Login'}
        </button>

        {state.isLoading && (
          <div className="loading-indicator">
            Connecting to server...
          </div>
        )}
      </form>
    </div>
  );
};
