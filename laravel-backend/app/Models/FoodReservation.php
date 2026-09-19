<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class FoodReservation extends Model {
    protected $guarded = [];
    protected $casts = [
        'guest_meal_selections' => 'array',
    ];

    protected $hidden = ['room_number', 'guest_meal_selections', 'breakfast_count', 'created_at', 'updated_at'];
    protected $appends = ['roomNumber', 'guestMealSelections', 'breakfastCount'];

    public function getRoomNumberAttribute() { return $this->attributes['room_number']; }
    public function getGuestMealSelectionsAttribute() { return $this->guest_meal_selections; }
    public function getBreakfastCountAttribute() { return $this->attributes['breakfast_count']; }

    public function room() {
        return $this->belongsTo(Room::class, 'room_number', 'room_number');
    }
}
