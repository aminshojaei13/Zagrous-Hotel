const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

// Mock fetch for Ktor
global.fetch = (url) => {
  if (url.includes('/rooms')) {
    return Promise.resolve({
      status: 200, ok: true, json: () => Promise.resolve([
        { id: "r1", roomNumber: "101", guestName: "Guest 1", guestCount: 2, checkInDate: "1403/01/01", checkOutDate: "1403/01/05" }
      ])
    });
  }
  return Promise.resolve({ status: 200, ok: true, json: () => Promise.resolve([]) });
};

async function runTest() {
  try {
    const admin = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.admin;
    const bridge = admin.createAdminBridge();

    console.log("TEST: Admin Bridge initialized");

    let lastState = bridge.getCurrentState();
    bridge.subscribe((s) => lastState = s);

    console.log("TEST: Loading Admin data...");
    bridge.loadData();

    // Wait for async load
    await new Promise(r => setTimeout(r, 500));

    console.log("Rooms Count:", lastState.rooms.length);
    if (lastState.rooms.length > 0 && lastState.rooms[0].roomNumber === "101") {
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
