import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// Serializable Reservation class
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
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
        System.out.println("Guest: " + guestName + ", Room: " + roomType +
                ", Nights: " + nights + ", Room ID: " + roomId);
    }
}

// Serializable Inventory class
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public synchronized boolean allocateRoom(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (!inventory.containsKey(roomType) || available <= 0) return false;
        inventory.put(roomType, available - 1);
        return true;
    }

    public synchronized void releaseRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public synchronized void displayInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println();
    }
}

// Booking Service
class BookingService implements Serializable {
    private static final long serialVersionUID = 1L;
    private RoomInventory inventory;
    private int roomCounter = 1;
    private List<Reservation> bookingHistory = new ArrayList<>();

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
        bookingHistory.add(res);
        System.out.println("Booking Confirmed: " + guestName + " -> " + roomType + " (Room ID: " + roomId + ")");
        return res;
    }

    public void displayBookingHistory() {
        System.out.println("Booking History:");
        for (Reservation r : bookingHistory) {
            r.displayReservation();
        }
        System.out.println();
    }

    public List<Reservation> getBookingHistory() {
        return bookingHistory;
    }
}

// Persistence Service
class PersistenceService {
    public static void saveState(String filename, RoomInventory inventory, BookingService bookingService) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(inventory);
            oos.writeObject(bookingService);
            System.out.println("System state saved successfully.\n");
        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    public static Object[] restoreState(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingService bookingService = (BookingService) ois.readObject();
            System.out.println("System state restored successfully.\n");
            return new Object[]{inventory, bookingService};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error restoring state, starting fresh: " + e.getMessage());
            return null;
        }
    }
}

public class UseCase12DataPersistenceRecovery {
    private static final String DATA_FILE = "booking_state.ser";

    public static void main(String[] args) throws InterruptedException {
        RoomInventory inventory;
        BookingService bookingService;

        Object[] restored = PersistenceService.restoreState(DATA_FILE);
        if (restored != null) {
            inventory = (RoomInventory) restored[0];
            bookingService = (BookingService) restored[1];
        } else {
            inventory = new RoomInventory();
            inventory.addRoomType("Single Room", 3);
            inventory.addRoomType("Double Room", 2);
            bookingService = new BookingService(inventory);
        }

        List<String[]> bookingRequests = Arrays.asList(
                new String[]{"Alice", "Single Room", "2"},
                new String[]{"Bob", "Double Room", "3"},
                new String[]{"Charlie", "Single Room", "1"}
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);
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
        bookingService.displayBookingHistory();

        PersistenceService.saveState(DATA_FILE, inventory, bookingService);
    }
}