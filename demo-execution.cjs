const KotlinLibrary = require('./app/shared/build/dist/js/productionLibrary/HotelZagrous-app-shared.js');

global.window = { location: { protocol: 'http:', hostname: 'localhost' } };

async function runDemo() {
  console.log("--- Hotel Zagrous Runtime Demo ---");

  try {
    const admin = KotlinLibrary.com.braveboy.hotelzagrous.app.shared.features.admin;
    const bridge = admin.createAdminBridge();

    let currentState = bridge.getCurrentState();
    console.log("1. Bridge Initialized. Current Loading State:", currentState.isLoading);

    // Subscribe to catch the data when it arrives from the server
    bridge.subscribe((newState) => {
      if (newState.rooms.length > 0) {
        console.log("\n2. Data Received from Backend!");
        console.log("Total Rooms in Database:", newState.rooms.length);
        console.log("First Room Number:", newState.rooms[0].roomNumber);
        console.log("Guest Name:", newState.rooms[0].guestName);

        console.log("\n3. Automated Report Calculation (Kotlin Logic):");
        const report = newState.dailyReportSummary;
        console.log("Selected Date:", report.date || "Today");
        console.log("Total Breakfasts Grouped:", report.totalBreakfast);
        console.log("Total Lunch Portions:", report.totalLunch);

        console.log("\n--- DEMO SUCCESSFUL ---");
        console.log("React UI is now ready to render these objects.");
        process.exit(0);
      }
    });

    console.log("Fetching real data from http://localhost:8092/api/rooms ...");
    bridge.loadData();

    // Timeout if server doesn't respond in 5s
    setTimeout(() => {
        console.log("Demo timed out (Server may be empty or slow)");
        process.exit(0);
    }, 5000);

  } catch (e) {
    console.error("Demo failed:", e);
    process.exit(1);
  }
}

runDemo();
