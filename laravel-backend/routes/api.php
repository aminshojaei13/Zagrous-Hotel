<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\HotelController;

Route::delete('/admin/clear-all', [HotelController::class, 'clearAllData']);

Route::get('/rooms', [HotelController::class, 'getRooms']);
Route::get('/rooms/{idOrNumber}', [HotelController::class, 'getRoom']);
Route::post('/rooms', [HotelController::class, 'upsertRoom']);
Route::delete('/rooms/{id}', [HotelController::class, 'deleteRoom']);
Route::put('/rooms/{id}/stay', [HotelController::class, 'updateRoomStay']);

Route::get('/foods', [HotelController::class, 'getFoods']);
Route::post('/foods', [HotelController::class, 'upsertFood']);
Route::delete('/foods/{id}', [HotelController::class, 'deleteFood']);

Route::get('/menu-configs', [HotelController::class, 'getMenuConfigs']);
Route::post('/menu-configs', [HotelController::class, 'upsertMenuConfig']);

Route::get('/reservations', [HotelController::class, 'getReservations']);
Route::get('/rooms/{roomNumber}/reservations', [HotelController::class, 'getReservationsForRoom']);
Route::post('/reservations', [HotelController::class, 'saveReservation']);
