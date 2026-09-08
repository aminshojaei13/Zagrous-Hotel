import { useState, useEffect } from 'react';
import { getAdminBridge, AdminStateJs, AdminRoomJs, AdminFoodItemJs } from '../kotlin/adminBridge';

export function useAdminViewModel() {
  const bridge = getAdminBridge();

  const [state, setState] = useState<AdminStateJs>(() => bridge.getCurrentState());

  useEffect(() => {
    const unsubscribe = bridge.subscribe((newState: AdminStateJs) => {
      setState(newState);
    });

    return () => {
      unsubscribe();
    };
  }, [bridge]);

  return {
    state,
    loadData: () => bridge.loadData(),
    addRoom: (room: Omit<AdminRoomJs, 'id'>) => {
      bridge.addRoom(
        room.roomNumber,
        room.guestName,
        room.identificationId,
        room.guestCount,
        room.hasBreakfast,
        room.breakfastCount,
        room.checkInDate,
        room.checkOutDate,
        room.checkInEpochMillis,
        room.checkOutEpochMillis
      );
    },
    updateRoomStay: (room: AdminRoomJs) => {
      bridge.updateRoomStay(
        room.id,
        room.roomNumber,
        room.guestName,
        room.identificationId,
        room.checkInDate,
        room.checkOutDate,
        room.checkInEpochMillis,
        room.checkOutEpochMillis,
        room.guestCount,
        room.hasBreakfast,
        room.breakfastCount
      );
    },
    deleteRoom: (id: string) => bridge.deleteRoom(id),

    upsertFood: (food: AdminFoodItemJs) => {
      bridge.upsertFood(
        food.id,
        food.name,
        food.nameAr,
        food.type,
        food.dayType,
        food.isActive,
        food.isVisibleToUsers,
        food.displayOrder
      );
    },
    deleteFood: (id: string) => bridge.deleteFood(id),
    updateMenuConfig: (dayType: string, foodType: string, isEnabled: boolean) => {
      bridge.updateMenuConfig(dayType, foodType, isEnabled);
    }
  };
}
