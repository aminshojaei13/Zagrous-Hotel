import { useState, useEffect, useMemo } from 'react';
import { createReservationBridge, ReservationStateJs } from '../kotlin/reservationBridge';

export function useReservationViewModel() {
  // We create the bridge once. In a real app, you might want to provide this via Context.
  const bridge = useMemo(() => createReservationBridge(), []);

  const [state, setState] = useState<ReservationStateJs>(() => bridge.getCurrentState());

  useEffect(() => {
    console.log("Subscribing to Kotlin Bridge...");
    const unsubscribe = bridge.subscribe((newState: ReservationStateJs) => {
      console.log("State updated from Kotlin:", newState);
      setState(newState);
    });

    return () => {
      console.log("Unsubscribing from Kotlin Bridge...");
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
