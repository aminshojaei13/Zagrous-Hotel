<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class FoodItem extends Model {
    protected $keyType = 'string';
    public $incrementing = false;
    protected $guarded = [];

    protected $hidden = ['name_ar', 'day_type', 'is_active', 'is_visible_to_users', 'display_order', 'created_at', 'updated_at'];
    protected $appends = ['nameAr', 'dayType', 'isActive', 'isVisibleToUsers', 'displayOrder'];

    protected static function booted() {
        static::creating(fn ($item) => $item->id = (string) Str::uuid());
    }

    public function getNameArAttribute() { return $this->attributes['name_ar']; }
    public function getDayTypeAttribute() { return $this->attributes['day_type']; }
    public function getIsActiveAttribute() { return (bool)$this->attributes['is_active']; }
    public function getIsVisibleToUsersAttribute() { return (bool)$this->attributes['is_visible_to_users']; }
    public function getDisplayOrderAttribute() { return $this->attributes['display_order']; }
}
