<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\RoomController;
use App\Http\Controllers\Api\FoodController;
use App\Http\Controllers\Api\ReservationController;
use App\Http\Controllers\Api\AdminController;

Route::prefix('admin')->group(function () {
    Route::delete('/clear-all', [AdminController::class, 'clearAllData']);
});

// Rooms
Route::get('/rooms', [RoomController::class, 'index']);
Route::get('/rooms/{idOrNumber}', [RoomController::class, 'show']);
Route::post('/rooms', [RoomController::class, 'store']);
Route::delete('/rooms/{room}', [RoomController::class, 'destroy']);
Route::put('/rooms/{room}/stay', [RoomController::class, 'updateStay']);

// Reservations
Route::get('/reservations', [ReservationController::class, 'index']);
Route::post('/reservations', [ReservationController::class, 'store']);
Route::get('/rooms/{roomNumber}/reservations', [ReservationController::class, 'roomReservations']);

// Foods
Route::get('/foods', [FoodController::class, 'index']);
Route::post('/foods', [FoodController::class, 'store']);
Route::delete('/foods/{food}', [FoodController::class, 'destroy']);

// Menu Configs
Route::get('/menu-configs', [FoodController::class, 'menuConfigs']);
Route::post('/menu-configs', [FoodController::class, 'upsertMenuConfig']);
