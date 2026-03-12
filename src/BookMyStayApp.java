import java.util.*;

class Reservation {
    String guestName;
    String roomType;
    int nights;
    String roomId;

    public Reservation(String guestName, String roomType, int nights, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
        this.roomId = roomId;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Room: " + roomType + ", Nights: " + nights + ", Room ID: " + roomId);
    }
}

class BookingHistory {
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings); // return a copy to prevent modification
    }

    public void displayAllReservations() {
        if (confirmedBookings.isEmpty()) {
            System.out.println("No confirmed reservations yet.");
            return;
        }
        System.out.println("All Confirmed Reservations:");
        for (Reservation r : confirmedBookings) {
            r.displayReservation();
        }
        System.out.println();
    }
}

class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    public void generateSummaryReport() {
        List<Reservation> reservations = history.getAllReservations();
        System.out.println("Booking Summary Report:");
        Map<String, Integer> roomCount = new HashMap<>();
        for (Reservation r : reservations) {
            roomCount.put(r.roomType, roomCount.getOrDefault(r.roomType, 0) + 1);
        }
        for (String roomType : roomCount.keySet()) {
            System.out.println(roomType + " -> " + roomCount.get(roomType) + " bookings");
        }
        System.out.println("Total Reservations: " + reservations.size() + "\n");
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService(history);

        Reservation r1 = new Reservation("Alice", "Single Room", 2, "SI1");
        Reservation r2 = new Reservation("Bob", "Double Room", 3, "DO2");
        Reservation r3 = new Reservation("Charlie", "Suite Room", 1, "SU3");

        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        System.out.println("Book My Stay - Hotel Booking System v8.1");
        System.out.println("========================================\n");

        history.displayAllReservations();
        reportService.generateSummaryReport();
    }
}