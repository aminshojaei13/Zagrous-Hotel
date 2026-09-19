<?php

namespace Database\Seeders;

use App\Models\FoodItem;
use App\Models\MenuConfig;
use Illuminate\Database\Seeder;

class FoodItemSeeder extends Seeder
{
    public function run(): void
    {
        $foods = [
            // Even Days - Lunch
            ['name' => 'چلو جوجه', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 0],
            ['name' => 'خورشت قیمه', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 1],
            ['name' => 'چلو کباب نگینی', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 2],
            ['name' => 'مرغ ربی', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 3],

            // Even Days - Dinner
            ['name' => 'شنیسل مرغ', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 0],
            ['name' => 'خوراک لقمه', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 1],
            ['name' => 'رولت گوشت', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 2],
            ['name' => 'میرزا قاسمی', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 3],
            ['name' => 'عدس پلو', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 4],
            ['name' => 'جوجه', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 5],

            // Odd Days - Lunch
            ['name' => 'چلو کباب کوبیده', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 0],
            ['name' => 'چلو خورشت قرمه سبزی', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 1],
            ['name' => 'چلو کباب نگینی', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 2],
            ['name' => 'چلو مرغ ربی', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 3],
            ['name' => 'چلو جوجه', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 4],
        ];

        foreach ($foods as $food) {
            FoodItem::create($food);
        }

        // Initialize Menu Configs
        $days = ['EVEN', 'ODD', 'FRIDAY'];
        $types = ['LUNCH', 'DINNER'];

        foreach ($days as $day) {
            foreach ($types as $type) {
                MenuConfig::create([
                    'day_type' => $day,
                    'food_type' => $type,
                    'is_enabled' => true
                ]);
            }
        }
    }
}
