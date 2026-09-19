<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Support\Str;

class Room extends Model
{
    protected $keyType = 'string';
    public $incrementing = false;
    protected $guarded = [];

    protected static function booted()
    {
        static::creating(function ($room) {
            if (empty($room->id)) {
                $room->id = (string) Str::uuid();
            }
        });
    }

    public function reservations(): HasMany
    {
        return $this->hasMany(FoodReservation::class, 'room_number', 'room_number');
    }
}
