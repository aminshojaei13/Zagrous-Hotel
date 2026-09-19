<?php

namespace Database\Seeders;

use App\Models\FoodItem;
use App\Models\MenuConfig;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        // Default Food Items and Configs
        $this->call([
            FoodItemSeeder::class,
        ]);
    }
}
