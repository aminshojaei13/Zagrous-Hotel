import React, { useState } from 'react';
import { useReservationViewModel } from '../hooks/useReservationViewModel';

export const ReservationDemo: React.FC = () => {
  const { state, updateCredentials, login } = useReservationViewModel();

  const [roomInput, setRoomInput] = useState('');
  const [idInput, setIdInput] = useState('');

  const handleUpdate = () => {
    updateCredentials(roomInput, idInput);
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h2>Hotel Zagrous - Reservation PoC (React)</h2>

      <div style={{ marginBottom: '20px', padding: '10px', backgroundColor: '#f9f9f9' }}>
        <h3>Current Kotlin State:</h3>
        <p><strong>Room Number:</strong> {state.roomNumber || '(none)'}</p>
        <p><strong>Loading:</strong> {state.isLoading ? 'Yes' : 'No'}</p>
        <p><strong>Is Logged In:</strong> {state.isLoggedIn ? 'Yes' : 'No'}</p>
        <p><strong>Error:</strong> {state.error || 'None'}</p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxWidth: '300px' }}>
        <input
          type="text"
          placeholder="Room Number"
          value={roomInput}
          onChange={(e) => setRoomInput(e.target.value)}
        />
        <input
          type="text"
          placeholder="Identification ID"
          value={idInput}
          onChange={(e) => setIdInput(e.target.value)}
        />
        <button onClick={handleUpdate}>Update Credentials in VM</button>
        <button onClick={login} disabled={state.isLoading}>Login</button>
      </div>
    </div>
  );
};
