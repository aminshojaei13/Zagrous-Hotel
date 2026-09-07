import * as KotlinLibrary from 'HotelZagrous-app-shared';

const Lib = KotlinLibrary as any;

const reservationNamespace = Lib.com.braveboy.hotelzagrous.app.shared.features.reservation;

export const createReservationBridge = reservationNamespace.createReservationBridge;

export type ReservationWebBridge = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationWebBridge;
export type ReservationStateJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation.ReservationStateJs;
