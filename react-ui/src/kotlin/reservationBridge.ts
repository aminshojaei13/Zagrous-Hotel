import * as KotlinLibrary from 'HotelZagrous-app-shared';

// @ts-ignore
const Lib: any = KotlinLibrary.default || KotlinLibrary;

const reservationNamespace = Lib.com.braveboy.hotelzagrous.app.shared.features.reservation;

export const createReservationBridge = reservationNamespace.createReservationBridge;

export type ReservationWebBridge = any;
export type ReservationStateJs = any;
