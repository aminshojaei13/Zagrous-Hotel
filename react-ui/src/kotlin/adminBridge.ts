import * as KotlinLibrary from 'HotelZagrous-app-shared';

const Lib = KotlinLibrary as any;

const bridgeInstance = Lib.createAdminBridge();

export const getAdminBridge = () => bridgeInstance;

export type AdminWebBridge = KotlinLibrary.AdminWebBridge;
export type AdminStateJs = KotlinLibrary.AdminStateJs;
export type AdminRoomJs = KotlinLibrary.AdminRoomJs;
export type AdminFoodItemJs = KotlinLibrary.AdminFoodItemJs;
export type AdminMenuConfigJs = KotlinLibrary.AdminMenuConfigJs;
export type AdminReservationJs = KotlinLibrary.AdminReservationJs;
export type AdminGuestMealSelectionJs = KotlinLibrary.AdminGuestMealSelectionJs;
export type DailyReportSummaryJs = KotlinLibrary.DailyReportSummaryJs;
export type DailyReportFoodItemJs = KotlinLibrary.DailyReportFoodItemJs;
