import java.util.*;
import java.util.concurrent.*;

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
    private final Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public synchronized void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (!inventory.containsKey(roomType) || available <= 0) {
            return false;
        }
        inventory.put(roomType, available - 1);
        return true;
    }

    public synchronized void releaseRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public synchronized int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public synchronized void displayInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
    }
}

class BookingService {
    private final RoomInventory inventory;
    private int roomCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public synchronized Reservation processBooking(String guestName, String roomType, int nights) {
        boolean allocated = inventory.allocateRoom(roomType);
        if (!allocated) {
            System.out.println("Booking Failed for " + guestName + ": No availability for " + roomType);
            return null;
        }
        String roomId = roomType.substring(0, 2).toUpperCase() + roomCounter++;
        Reservation res = new Reservation(guestName, roomType, nights, roomId);
        System.out.println("Booking Confirmed: " + guestName + " -> " + roomType + " (Room ID: " + roomId + ")");
        return res;
    }
}

public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 3);
        inventory.addRoomType("Double Room", 2);

        BookingService bookingService = new BookingService(inventory);

        List<String[]> bookingRequests = Arrays.asList(
                new String[]{"Alice", "Single Room", "2"},
                new String[]{"Bob", "Double Room", "3"},
                new String[]{"Charlie", "Single Room", "1"},
                new String[]{"David", "Double Room", "2"},
                new String[]{"Eve", "Single Room", "1"},
                new String[]{"Frank", "Single Room", "1"} // This one should fail if inventory runs out
        );

        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Reservation>> results = new ArrayList<>();

        for (String[] req : bookingRequests) {
            results.add(executor.submit(() -> {
                String guest = req[0];
                String roomType = req[1];
                int nights = Integer.parseInt(req[2]);
                return bookingService.processBooking(guest, roomType, nights);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("\nFinal Room Inventory:");
        inventory.displayInventory();
    }
}