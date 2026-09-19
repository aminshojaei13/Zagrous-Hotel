<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class MenuConfig extends Model {
    public $timestamps = false;
    protected $guarded = [];

    protected $hidden = ['day_type', 'food_type', 'is_enabled'];
    protected $appends = ['dayType', 'foodType', 'isEnabled'];

    public function getDayTypeAttribute() { return $this->attributes['day_type']; }
    public function getFoodTypeAttribute() { return $this->attributes['food_type']; }
    public function getIsEnabledAttribute() { return (bool)$this->attributes['is_enabled']; }
}
