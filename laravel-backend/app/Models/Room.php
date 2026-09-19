<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Room extends Model {
    protected $keyType = 'string';
    public $incrementing = false;
    protected $guarded = [];

    protected $hidden = [
        'room_number', 'guest_name', 'identification_id', 'guest_count',
        'has_breakfast', 'breakfast_count', 'check_in_date', 'check_out_date',
        'check_in_epoch_millis', 'check_out_epoch_millis', 'created_at', 'updated_at'
    ];

    protected $appends = [
        'roomNumber', 'guestName', 'identificationId', 'guestCount',
        'hasBreakfast', 'breakfastCount', 'checkInDate', 'checkOutDate',
        'checkInEpochMillis', 'checkOutEpochMillis'
    ];

    protected static function booted() {
        static::creating(fn ($room) => $room->id = (string) Str::uuid());
    }

    public function getRoomNumberAttribute() { return $this->attributes['room_number']; }
    public function getGuestNameAttribute() { return $this->attributes['guest_name']; }
    public function getIdentificationIdAttribute() { return $this->attributes['identification_id']; }
    public function getGuestCountAttribute() { return $this->attributes['guest_count']; }
    public function getHasBreakfastAttribute() { return (bool)$this->attributes['has_breakfast']; }
    public function getBreakfastCountAttribute() { return $this->attributes['breakfast_count']; }
    public function getCheckInDateAttribute() { return $this->attributes['check_in_date']; }
    public function getCheckOutDateAttribute() { return $this->attributes['check_out_date']; }
    public function getCheckInEpochMillisAttribute() { return $this->attributes['check_in_epoch_millis']; }
    public function getCheckOutEpochMillisAttribute() { return $this->attributes['check_out_epoch_millis']; }

    public function reservations() {
        return $this->hasMany(FoodReservation::class, 'room_number', 'room_number');
    }
}
