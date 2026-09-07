const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

try {
  const reservation = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation;
  console.log("Namespace found");

  const bridge = reservation.createReservationBridge();
  console.log("Bridge created");

  const state = bridge.getCurrentState();
  console.log("State room number:", state.roomNumber);

  process.exit(0);
} catch (e) {
  console.error("Verification failed:", e);
  process.exit(1);
}
