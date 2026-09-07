const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = {
  location: { protocol: 'http:', hostname: 'localhost' }
};

// Mock fetch to return initial data and then success on reservation update
global.fetch = (url) => {
  if (url.includes('/foods')) {
    return Promise.resolve({
      status: 200, ok: true, json: () => Promise.resolve([
        { id: "f1", name: "Kabab", type: "LUNCH", dayType: "EVEN", isActive: true, isVisibleToUsers: true },
        { id: "f2", name: "Joojeh", type: "DINNER", dayType: "EVEN", isActive: true, isVisibleToUsers: true }
      ])
    });
  }
  if (url.includes('/rooms/101')) {
     return Promise.resolve({
       status: 200, ok: true, json: () => Promise.resolve({
         roomNumber: "101", guestName: "Amin", guestCount: 1, identificationId: "ID1",
         checkInDate: "1403/01/01", checkOutDate: "1403/01/02",
         checkInEpochMillis: 1711929600000, checkOutEpochMillis: 1712016000000
       })
     });
  }
  return Promise.resolve({ status: 200, ok: true, json: () => Promise.resolve([]) });
};

async function runTest() {
  try {
    const reservation = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.reservation;
    const bridge = reservation.createReservationBridge();

    let lastState = bridge.getCurrentState();
    bridge.subscribe((s) => lastState = s);

    // Login first
    bridge.updateCredentials("101", "ID1");
    bridge.login();
    await new Promise(r => setTimeout(r, 500));

    console.log("Logged In:", lastState.isLoggedIn);
    console.log("Available Foods Count:", lastState.availableFoods.length);
    console.log("Stay Days:", lastState.stayDays.length);

    if (lastState.stayDays.length > 0) {
      const firstDay = lastState.stayDays[0];
      const kabab = lastState.availableFoods.find(f => f.name === "Kabab");

      console.log(`Selecting ${kabab.name} for ${firstDay}...`);
      bridge.changeFood(firstDay, 0, kabab.id, "LUNCH");

      await new Promise(r => setTimeout(r, 100));

      const res = lastState.tempReservations.find(r => r.date === firstDay);
      const selection = res.guestMealSelections.find(s => s.guestIndex === 0);

      console.log("Selected Food ID in State:", selection.lunchFoodId);

      if (selection.lunchFoodId === kabab.id) {
        console.log("VERIFICATION: PASS");
        process.exit(0);
      }
    }

    console.log("VERIFICATION: FAIL");
    process.exit(1);
  } catch (e) {
    console.error("VERIFICATION: CRASH", e);
    process.exit(1);
  }
}

runTest();
