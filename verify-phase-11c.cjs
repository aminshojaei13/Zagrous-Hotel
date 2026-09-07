const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

// Mock fetch to return initial data and then success on reservation update
global.fetch = (url) => {
  if (url.includes('/foods')) {
    return Promise.resolve({
      status: 200, ok: true, json: () => Promise.resolve([
        { id: "f1", name: "Kabab", type: "LUNCH", dayType: "EVEN", isActive: true, isVisibleToUsers: true }
      ])
    });
  }
  if (url.includes('/rooms/101')) {
     return Promise.resolve({
       status: 200, ok: true, json: () => Promise.resolve({
         roomNumber: "101", guestName: "Amin", guestCount: 1, identificationId: "ID1",
         checkInDate: "1403/01/01", checkOutDate: "1403/01/02",
         checkInEpochMillis: 1711929600000, checkOutEpochMillis: 1712016000000,
         hasBreakfast: true, breakfastCount: 1
       })
     });
  }
  if (url.includes('/reservations')) {
    return Promise.resolve({ status: 200, ok: true, json: () => Promise.resolve({ message: "Success" }) });
  }
  return Promise.resolve({ status: 200, ok: true, json: () => Promise.resolve([]) });
};

async function runTest() {
  try {
    const reservation = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation;
    const bridge = reservation.createReservationBridge();

    let lastState = bridge.getCurrentState();
    bridge.subscribe((s) => lastState = s);

    // Login
    bridge.updateCredentials("101", "ID1");
    bridge.login();
    await new Promise(r => setTimeout(r, 500));

    console.log("Logged In:", lastState.isLoggedIn);
    const firstDay = lastState.stayDays[0];

    console.log(`Setting breakfast count for ${firstDay} to 1...`);
    bridge.changeBreakfastCount(firstDay, 1);
    await new Promise(r => setTimeout(r, 100));

    const resAfterBreakfast = lastState.tempReservations.find(r => r.date === firstDay);
    console.log("Breakfast count in State:", resAfterBreakfast ? resAfterBreakfast.breakfastCount : "NOT FOUND");

    console.log("Confirming Reservation...");
    bridge.confirmReservation();
    await new Promise(r => setTimeout(r, 200));

    console.log("Final Error state (Success Expected):", lastState.error);

    if (lastState.isLoggedIn && resAfterBreakfast && resAfterBreakfast.breakfastCount === 1 && lastState.error === "reservation_success") {
      console.log("VERIFICATION: PASS");
      process.exit(0);
    } else {
      console.log("VERIFICATION: FAIL");
      process.exit(1);
    }
  } catch (e) {
    console.error("VERIFICATION: CRASH", e);
    process.exit(1);
  }
}

runTest();
