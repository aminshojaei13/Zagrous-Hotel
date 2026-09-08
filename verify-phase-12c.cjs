const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

try {
  const admin = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.admin;
  const bridge = admin.createAdminBridge();

  console.log("Bridge created");

  const state = bridge.getCurrentState();
  console.log("Initial State keys:", Object.keys(state));

  if (Array.isArray(state.reservations)) {
      console.log("Reservations array: OK");
  } else {
      throw new Error("Reservations is not an array");
  }

  process.exit(0);
} catch (e) {
  console.error("Verification failed:", e);
  process.exit(1);
}
