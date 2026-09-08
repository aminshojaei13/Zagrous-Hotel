import * as KotlinLibrary from 'HotelZagrous-app-shared';

const Lib = KotlinLibrary as any;

const adminNamespace = Lib.com.braveboy.hotelzagrous.app.shared.features.admin;

const bridgeInstance = adminNamespace.createAdminBridge();

export const getAdminBridge = () => bridgeInstance;

export type AdminWebBridge = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.admin.AdminWebBridge;
export type AdminStateJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.admin.AdminStateJs;
export type AdminRoomJs = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.admin.AdminRoomJs;
