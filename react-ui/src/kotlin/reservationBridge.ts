import * as KotlinLibrary from 'HotelZagrous-app-shared';

const Lib = KotlinLibrary as any;

const reservationNamespace = Lib.com.braveboy.hotelzagrous.app.shared.features.reservation;

const bridgeInstance = reservationNamespace.createReservationBridge();

export const getReservationBridge = () => bridgeInstance;

export type ReservationWebBridge = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationWebBridge;
export type ReservationStateJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationStateJs;
export type FoodItemJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.FoodItemJs;
export type FoodReservationJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.FoodReservationJs;
export type GuestMealSelectionJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.GuestMealSelectionJs;
export type RoomJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.RoomJs;
