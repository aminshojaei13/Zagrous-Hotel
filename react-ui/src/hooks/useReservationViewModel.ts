import { useState, useEffect, useMemo } from 'react';
import { createReservationBridge, ReservationStateJs } from '../kotlin/reservationBridge';

export function useReservationViewModel() {
  const bridge = useMemo(() => createReservationBridge(), []);

  const [state, setState] = useState<ReservationStateJs>(() => bridge.getCurrentState());

  useEffect(() => {
    const unsubscribe = bridge.subscribe((newState: ReservationStateJs) => {
      setState(newState);
    });

    return () => {
      unsubscribe();
    };
  }, [bridge]);

  return {
    state,
    updateCredentials: (roomNumber: string, id: string) => {
      bridge.updateCredentials(roomNumber, id);
    },
    login: () => {
      bridge.login();
    }
  };
}
