const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

// Mock fetch to return a room
global.fetch = () => Promise.resolve({
  status: 200,
  ok: true,
  headers: new Map(),
  json: () => Promise.resolve({
    roomNumber: "101",
    guestName: "Amin Shojaei",
    guestCount: 2,
    identificationId: "12345",
    checkInDate: "1403/01/01",
    checkOutDate: "1403/01/05",
    checkInEpochMillis: 1711929600000,
    checkOutEpochMillis: 1712275200000
  })
});

async function runTest() {
  try {
    const reservation = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation;
    const bridge = reservation.createReservationBridge();

    let lastState = bridge.getCurrentState();
    bridge.subscribe((s) => lastState = s);

    console.log("Login start...");
    bridge.updateCredentials("101", "12345");
    bridge.login();

    // Wait for mock fetch and state update
    await new Promise(r => setTimeout(r, 500));

    console.log("Logged In:", lastState.isLoggedIn);
    console.log("Guest Name:", lastState.room ? lastState.room.guestName : "NULL");
    console.log("Stay Days:", lastState.stayDays.length);

    if (lastState.isLoggedIn && lastState.room && lastState.room.guestName === "Amin Shojaei" && lastState.stayDays.length > 0) {
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
