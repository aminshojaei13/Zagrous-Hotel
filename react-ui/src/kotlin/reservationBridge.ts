import * as KotlinLibrary from 'HotelZagrous-app-shared';

const Lib = KotlinLibrary as any;

const bridgeInstance = Lib.createReservationBridge();

export const getReservationBridge = () => bridgeInstance;

export type ReservationWebBridge = KotlinLibrary.ReservationWebBridge;
export type ReservationStateJs = KotlinLibrary.ReservationStateJs;
export type FoodItemJs = KotlinLibrary.FoodItemJs;
export type FoodReservationJs = KotlinLibrary.FoodReservationJs;
export type GuestMealSelectionJs = KotlinLibrary.GuestMealSelectionJs;
export type RoomJs = KotlinLibrary.RoomJs;
