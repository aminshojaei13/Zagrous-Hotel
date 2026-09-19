<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Room;
use App\Models\FoodItem;
use App\Models\MenuConfig;
use App\Models\FoodReservation;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class HotelController extends Controller {

    private function normalize($digits) {
        if (!$digits) return $digits;
        $map = [
            '۰'=>'0','۱'=>'1','۲'=>'2','۳'=>'3','۴'=>'4','۵'=>'5','۶'=>'6','۷'=>'7','۸'=>'8','۹'=>'9',
            '٠'=>'0','١'=>'1','٢'=>'2','٣'=>'3','٤'=>'4','٥'=>'5','٦'=>'6','٧'=>'7','٨'=>'8','٩'=>'9'
        ];
        return strtr((string)$digits, $map);
    }

    public function clearAllData() {
        FoodReservation::truncate();
        Room::truncate();
        return response()->json(['message' => 'All data cleared successfully']);
    }

    public function getRooms() {
        return response()->json(Room::orderBy('room_number')->get());
    }

    public function getRoom($idOrNumber) {
        $normalized = $this->normalize($idOrNumber);
        $room = Room::where('id', $idOrNumber)
            ->orWhere('room_number', $normalized)
            ->first();

        if (!$room) {
            return response()->json(['message' => 'اتاق یافت نشد'], 404);
        }
        return response()->json($room);
    }

    public function upsertRoom(Request $request) {
        $data = [
            'room_number' => $this->normalize($request->roomNumber),
            'guest_name' => $request->guestName,
            'identification_id' => $this->normalize($request->identificationId ?? ''),
            'guest_count' => $request->guestCount ?? 1,
            'has_breakfast' => $request->hasBreakfast ?? false,
            'breakfast_count' => $request->breakfastCount ?? 0,
            'check_in_date' => $request->checkInDate ?? '',
            'check_out_date' => $request->checkOutDate ?? '',
            'check_in_epoch_millis' => $request->checkInEpochMillis ?? 0,
            'check_out_epoch_millis' => $request->checkOutEpochMillis ?? 0,
            'capacity' => $request->capacity ?? 1,
        ];

        $room = Room::updateOrCreate(
            ['room_number' => $data['room_number']],
            $data
        );
        return response()->json($room, 201);
    }

    public function deleteRoom($id) {
        Room::where('id', $id)->delete();
        return response()->json(null, 200);
    }

    public function updateRoomStay(Request $request, $id) {
        $room = Room::where('id', $id)->first();
        if (!$room) {
            return response()->json(['message' => 'شناسه اتاق یافت نشد'], 404);
        }

        $newRoomNumber = $this->normalize($request->roomNumber);

        if ($room->room_number !== $newRoomNumber) {
            FoodReservation::where('room_number', $room->room_number)
                ->update(['room_number' => $newRoomNumber]);
        }

        $room->update([
            'room_number' => $newRoomNumber,
            'guest_name' => $request->guestName,
            'identification_id' => $this->normalize($request->identificationId),
            'check_in_date' => $request->checkIn,
            'check_out_date' => $request->checkOut,
            'check_in_epoch_millis' => $request->checkInMillis,
            'check_out_epoch_millis' => $request->checkOutMillis,
            'guest_count' => $request->guestCount,
            'has_breakfast' => $request->hasBreakfast,
            'breakfast_count' => $request->breakfastCount,
        ]);

        return response()->json($room);
    }

    public function getFoods() {
        return response()->json(FoodItem::orderBy('day_type')->orderBy('type')->orderBy('display_order')->get());
    }

    public function upsertFood(Request $request) {
        $data = [
            'name' => $request->name,
            'name_ar' => $request->nameAr,
            'type' => $request->type,
            'day_type' => $request->dayType,
            'is_active' => $request->isActive ?? true,
            'is_visible_to_users' => $request->isVisibleToUsers ?? true,
            'display_order' => $request->displayOrder ?? 0,
        ];

        $food = FoodItem::updateOrCreate(
            ['id' => $request->id ?? (string) \Illuminate\Support\Str::uuid()],
            $data
        );
        return response()->json($food);
    }

    public function deleteFood($id) {
        FoodItem::where('id', $id)->delete();
        return response()->json(null, 200);
    }

    public function getMenuConfigs() {
        return response()->json(MenuConfig::all());
    }

    public function upsertMenuConfig(Request $request) {
        $config = MenuConfig::updateOrCreate(
            ['day_type' => $request->dayType, 'food_type' => $request->foodType],
            ['is_enabled' => $request->isEnabled]
        );
        return response()->json($config);
    }

    public function getReservations() {
        return response()->json(FoodReservation::orderBy('date')->orderBy('room_number')->get());
    }

    public function getReservationsForRoom($roomNumber) {
        $normalized = $this->normalize($roomNumber);
        return response()->json(FoodReservation::where('room_number', $normalized)->orderBy('date')->get());
    }

    public function saveReservation(Request $request) {
        $data = [
            'room_number' => $this->normalize($request->roomNumber),
            'date' => $request->date,
            'guest_meal_selections' => $request->guestMealSelections,
            'breakfast_count' => $request->breakfastCount ?? 0,
        ];

        $res = FoodReservation::updateOrCreate(
            ['room_number' => $data['room_number'], 'date' => $data['date']],
            $data
        );
        return response()->json($res, 201);
    }
}
