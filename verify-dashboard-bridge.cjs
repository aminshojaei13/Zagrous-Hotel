const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

try {
  const reservation = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation;
  const bridge = reservation.createReservationBridge();

  console.log("Bridge created");

  const state = bridge.getCurrentState();
  console.log("Initial State keys:", Object.keys(state));
  console.log("Stay Days (empty initially):", state.stayDays.length);

  // Test actions exist
  if (typeof bridge.changeFood === 'function' &&
      typeof bridge.changeBreakfastCount === 'function' &&
      typeof bridge.confirmReservation === 'function') {
    console.log("Actions verified");
  } else {
    throw new Error("Actions missing");
  }

  process.exit(0);
} catch (e) {
  console.error("Verification failed:", e);
  process.exit(1);
}
