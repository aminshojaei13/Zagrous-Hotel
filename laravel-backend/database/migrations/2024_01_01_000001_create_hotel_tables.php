<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::create('rooms', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->string('room_number')->unique();
            $table->integer('capacity')->default(1);
            $table->string('guest_name');
            $table->string('identification_id')->nullable();
            $table->integer('guest_count')->default(1);
            $table->boolean('has_breakfast')->default(false);
            $table->integer('breakfast_count')->default(0);
            $table->string('check_in_date');
            $table->string('check_out_date');
            $table->bigInteger('check_in_epoch_millis');
            $table->bigInteger('check_out_epoch_millis');
            $table->timestamps();
        });

        Schema::create('food_items', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->string('name');
            $table->string('name_ar')->nullable();
            $table->enum('type', ['LUNCH', 'DINNER']);
            $table->enum('day_type', ['EVEN', 'ODD', 'FRIDAY']);
            $table->boolean('is_active')->default(true);
            $table->boolean('is_visible_to_users')->default(true);
            $table->integer('display_order')->default(0);
            $table->timestamps();
        });

        Schema::create('menu_configs', function (Blueprint $table) {
            $table->id();
            $table->enum('day_type', ['EVEN', 'ODD', 'FRIDAY']);
            $table->enum('food_type', ['LUNCH', 'DINNER']);
            $table->boolean('is_enabled')->default(true);
            $table->unique(['day_type', 'food_type']);
        });

        Schema::create('food_reservations', function (Blueprint $table) {
            $table->id();
            $table->string('room_number');
            $table->string('date');
            $table->json('guest_meal_selections');
            $table->integer('breakfast_count')->default(0);
            $table->unique(['room_number', 'date']);
            $table->foreign('room_number')->references('room_number')->on('rooms')->onDelete('cascade');
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('food_reservations');
        Schema::dropIfExists('menu_configs');
        Schema::dropIfExists('food_items');
        Schema::dropIfExists('rooms');
    }
};
