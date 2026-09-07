const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

try {
  const reservation = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation;
  const bridge = reservation.createReservationBridge();

  console.log("Bridge created");

  const state = bridge.getCurrentState();
  console.log("Initial State isLoggedIn:", state.isLoggedIn);

  // Test new methods exist
  const methods = [
    'changeFood',
    'changeBreakfastCount',
    'confirmReservation',
    'toggleLanguage',
    'getDayType'
  ];

  methods.forEach(m => {
    if (typeof bridge[m] === 'function') {
      console.log(`Method ${m}: OK`);
    } else {
      throw new Error(`Method ${m} missing!`);
    }
  });

  const dayType = bridge.getDayType("1403/01/01");
  console.log("Sample DayType:", dayType);

  process.exit(0);
} catch (e) {
  console.error("Verification failed:", e);
  process.exit(1);
}
