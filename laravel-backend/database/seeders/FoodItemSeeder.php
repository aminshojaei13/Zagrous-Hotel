<?php

namespace Database\Seeders;

use App\Models\FoodItem;
use App\Models\MenuConfig;
use Illuminate\Database\Seeder;

class FoodItemSeeder extends Seeder
{
    public function run(): void
    {
        $defaultFoods = [
            ['name' => 'چلو جوجه', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 0],
            ['name' => 'خورشت قیمه', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 1],
            ['name' => 'چلو کباب نگینی', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 2],
            ['name' => 'مرغ ربی', 'type' => 'LUNCH', 'day_type' => 'EVEN', 'display_order' => 3],
            ['name' => 'شنیسل مرغ', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 0],
            ['name' => 'خوراک لقمه', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 1],
            ['name' => 'رولت گوشت', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 2],
            ['name' => 'میرزا قاسمی', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 3],
            ['name' => 'عدس پلو', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 4],
            ['name' => 'جوجه', 'type' => 'DINNER', 'day_type' => 'EVEN', 'display_order' => 5],
            ['name' => 'چلو کباب کوبیده', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 0],
            ['name' => 'چلو خورشت قرمه سبزی', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 1],
            ['name' => 'چلو کباب نگینی', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 2],
            ['name' => 'چلو مرغ ربی', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 3],
            ['name' => 'چلو جوجه', 'type' => 'LUNCH', 'day_type' => 'ODD', 'display_order' => 4],
            ['name' => 'شنیسل مرغ', 'type' => 'DINNER', 'day_type' => 'ODD', 'display_order' => 0],
            ['name' => 'کوفته تبریزی', 'type' => 'DINNER', 'day_type' => 'ODD', 'display_order' => 1],
            ['name' => 'ماکارانی', 'type' => 'DINNER', 'day_type' => 'ODD', 'display_order' => 2],
            ['name' => 'خوراک لقمه', 'type' => 'DINNER', 'day_type' => 'ODD', 'display_order' => 3],
            ['name' => 'جوجه', 'type' => 'DINNER', 'day_type' => 'ODD', 'display_order' => 4],
            ['name' => 'خورشت قیمه', 'type' => 'LUNCH', 'day_type' => 'FRIDAY', 'display_order' => 0],
            ['name' => 'چلو کباب نگینی', 'type' => 'LUNCH', 'day_type' => 'FRIDAY', 'display_order' => 1],
            ['name' => 'چلو جوجه', 'type' => 'LUNCH', 'day_type' => 'FRIDAY', 'display_order' => 2],
            ['name' => 'چلو مرغ ربی', 'type' => 'LUNCH', 'day_type' => 'FRIDAY', 'display_order' => 3],
            ['name' => 'خوراک لقمه', 'type' => 'DINNER', 'day_type' => 'FRIDAY', 'display_order' => 0],
            ['name' => 'شنیسل مرغ', 'type' => 'DINNER', 'day_type' => 'FRIDAY', 'display_order' => 1],
            ['name' => 'رولت گوشت', 'type' => 'DINNER', 'day_type' => 'FRIDAY', 'display_order' => 2],
            ['name' => 'ماکارانی', 'type' => 'DINNER', 'day_type' => 'FRIDAY', 'display_order' => 3],
            ['name' => 'جوجه', 'type' => 'DINNER', 'day_type' => 'FRIDAY', 'display_order' => 4],
        ];

        foreach ($defaultFoods as $food) {
            FoodItem::create($food);
        }

        foreach (['EVEN', 'ODD', 'FRIDAY'] as $dayType) {
            foreach (['LUNCH', 'DINNER'] as $foodType) {
                MenuConfig::create([
                    'day_type' => $dayType,
                    'food_type' => $foodType,
                    'is_enabled' => true,
                ]);
            }
        }
    }
}
