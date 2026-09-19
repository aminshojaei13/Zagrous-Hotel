<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class FoodReservation extends Model
{
    protected $guarded = [];

    protected $casts = [
        'guest_meal_selections' => 'array',
    ];

    public function room(): BelongsTo
    {
        return $this->belongsTo(Room::class, 'room_number', 'room_number');
    }
}
