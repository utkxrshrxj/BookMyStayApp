import java.util.*;

abstract class Room {
    String type;
    int beds;
    double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Number of Beds: " + beds);
        System.out.println("Price per Night: $" + price);
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
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean allocateRoom(String roomType) {
        int available = getAvailability(roomType);
        if (available > 0) {
            inventory.put(roomType, available - 1);
            return true;
        }
        return false;
    }

    public void displayInventory() {
        System.out.println("Current Room Availability:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type) + " rooms available");
        }
        System.out.println();
    }
}

class Reservation {
    String guestName;
    String roomType;
    int nights;
    String roomId;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Room: " + roomType + ", Nights: " + nights + ", Room ID: " + roomId);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
    }

    public Reservation pollRequest() {
        return requestQueue.poll();
    }

    public boolean hasRequests() {
        return !requestQueue.isEmpty();
    }
}

class RoomAllocationService {
    private RoomInventory inventory;
    private HashMap<String, Set<String>> allocatedRooms;
    private int roomCounter;

    public RoomAllocationService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
        roomCounter = 1;
    }

    public boolean allocate(Reservation reservation) {
        if (inventory.allocateRoom(reservation.roomType)) {
            String roomId = reservation.roomType.substring(0, 2).toUpperCase() + roomCounter++;
            allocatedRooms.putIfAbsent(reservation.roomType, new HashSet<>());
            allocatedRooms.get(reservation.roomType).add(roomId);
            reservation.roomId = roomId;
            return true;
        }
        return false;
    }
}

public class UseCase6RoomAllocationService {
    public static void main(String[] args) {
        SingleRoom single = new SingleRoom();
        DoubleRoom doubleR = new DoubleRoom();
        SuiteRoom suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.type, 2);
        inventory.addRoomType(doubleR.type, 1);
        inventory.addRoomType(suite.type, 1);

        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Single Room", 2));
        queue.addRequest(new Reservation("Bob", "Single Room", 3));
        queue.addRequest(new Reservation("Charlie", "Double Room", 1));
        queue.addRequest(new Reservation("David", "Suite Room", 2));
        queue.addRequest(new Reservation("Eve", "Single Room", 1)); // Should fail allocation (only 2 singles available)

        RoomAllocationService allocationService = new RoomAllocationService(inventory);

        System.out.println("Book My Stay - Hotel Booking System v6.1");
        System.out.println("========================================\n");

        while (queue.hasRequests()) {
            Reservation res = queue.pollRequest();
            boolean success = allocationService.allocate(res);
            if (success) {
                System.out.print("Reservation Confirmed: ");
                res.displayReservation();
            } else {
                System.out.println("Reservation Failed (No Availability): Guest: " + res.guestName + ", Room: " + res.roomType);
            }
        }

        System.out.println("\nFinal Inventory Status:");
        inventory.displayInventory();
    }
}