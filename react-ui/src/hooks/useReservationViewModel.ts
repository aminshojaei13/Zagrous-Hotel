import { useState, useEffect } from 'react';
import { getReservationBridge, ReservationStateJs } from '../kotlin/reservationBridge';

export function useReservationViewModel() {
  const bridge = getReservationBridge();

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
    },
    changeFood: (date: string, guestIndex: number, foodId: string | null, type: 'LUNCH' | 'DINNER') => {
      bridge.changeFood(date, guestIndex, foodId, type);
    }
  };
}
