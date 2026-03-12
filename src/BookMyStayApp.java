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

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void allocateRoom(String roomType) throws Exception {
        int available = inventory.getOrDefault(roomType, 0);
        if (!inventory.containsKey(roomType)) {
            throw new Exception("Invalid room type: " + roomType);
        }
        if (available <= 0) {
            throw new Exception("No availability for room type: " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public void releaseRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
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

    public boolean removeReservation(String roomId) {
        return confirmedBookings.removeIf(r -> r.roomId.equals(roomId));
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings);
    }

    public void displayAllReservations() {
        if (confirmedBookings.isEmpty()) {
            System.out.println("No confirmed reservations.");
            return;
        }
        System.out.println("Confirmed Reservations:");
        for (Reservation r : confirmedBookings) {
            r.displayReservation();
        }
        System.out.println();
    }
}

class CancellationService {
    private RoomInventory inventory;
    private BookingHistory history;
    private Stack<String> releasedRoomIds;

    public CancellationService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
        releasedRoomIds = new Stack<>();
    }

    public void cancelReservation(String roomId) throws Exception {
        List<Reservation> reservations = history.getAllReservations();
        Reservation target = null;
        for (Reservation r : reservations) {
            if (r.roomId.equals(roomId)) {
                target = r;
                break;
            }
        }
        if (target == null) {
            throw new Exception("Reservation with Room ID " + roomId + " does not exist.");
        }
        inventory.releaseRoom(target.roomType);
        releasedRoomIds.push(roomId);
        history.removeReservation(roomId);
        System.out.println("Reservation for Room ID " + roomId + " has been cancelled and inventory restored.");
    }

    public void displayReleasedRooms() {
        if (releasedRoomIds.isEmpty()) {
            System.out.println("No rooms have been released yet.");
            return;
        }
        System.out.println("Recently Released Room IDs (LIFO):");
        for (String id : releasedRoomIds) {
            System.out.println(id);
        }
        System.out.println();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);

        BookingHistory history = new BookingHistory();

        try {
            // Simulate bookings
            Reservation r1 = new Reservation("Alice", "Single Room", 2, "SI1");
            inventory.allocateRoom(r1.roomType);
            history.addReservation(r1);

            Reservation r2 = new Reservation("Bob", "Double Room", 3, "DO2");
            inventory.allocateRoom(r2.roomType);
            history.addReservation(r2);

            System.out.println("Book My Stay - Hotel Booking System v10.1");
            System.out.println("========================================\n");

            history.displayAllReservations();
            System.out.println("Current Inventory:");
            System.out.println("Single Room -> " + inventory.getAvailability("Single Room"));
            System.out.println("Double Room -> " + inventory.getAvailability("Double Room"));
            System.out.println();

            CancellationService cancellationService = new CancellationService(inventory, history);

            // Cancel a booking
            cancellationService.cancelReservation("SI1");

            history.displayAllReservations();
            System.out.println("Updated Inventory:");
            System.out.println("Single Room -> " + inventory.getAvailability("Single Room"));
            System.out.println("Double Room -> " + inventory.getAvailability("Double Room"));
            System.out.println();

            cancellationService.displayReleasedRooms();

            // Attempt to cancel a non-existent booking
            cancellationService.cancelReservation("XX9");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}