import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

abstract class Room {
    String type;
    int beds;
    double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 50.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 90.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 150.0);
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

    public void allocateRoom(String roomType) throws InvalidBookingException {
        int available = inventory.getOrDefault(roomType, 0);
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
        if (available <= 0) {
            throw new InvalidBookingException("No availability for room type: " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

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

class BookingService {
    private RoomInventory inventory;
    private int roomCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public Reservation confirmBooking(String guestName, String roomType, int nights) throws InvalidBookingException {
        if (guestName == null || guestName.isBlank()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
        if (nights <= 0) {
            throw new InvalidBookingException("Number of nights must be positive.");
        }
        inventory.allocateRoom(roomType);
        String roomId = roomType.substring(0, 2).toUpperCase() + roomCounter++;
        return new Reservation(guestName, roomType, nights, roomId);
    }
}

public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);

        BookingService bookingService = new BookingService(inventory);

        String[][] testBookings = {
                {"Alice", "Single Room", "2"},
                {"Bob", "Double Room", "3"},
                {"Charlie", "Suite Room", "1"},   // Invalid room type
                {"", "Single Room", "1"},         // Invalid guest name
                {"Eve", "Single Room", "-1"}      // Invalid nights
        };

        System.out.println("Book My Stay - Hotel Booking System v9.1");
        System.out.println("========================================\n");

        for (String[] booking : testBookings) {
            try {
                String guest = booking[0];
                String roomType = booking[1];
                int nights = Integer.parseInt(booking[2]);
                Reservation res = bookingService.confirmBooking(guest, roomType, nights);
                System.out.print("Reservation Confirmed: ");
                res.displayReservation();
            } catch (InvalidBookingException e) {
                System.out.println("Booking Failed: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Booking Failed: Invalid number of nights for guest " + booking[0]);
            }
        }

        System.out.println("\nFinal Room Availability:");
        System.out.println("Single Room -> " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room -> " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room -> " + inventory.getAvailability("Suite Room"));
    }
}