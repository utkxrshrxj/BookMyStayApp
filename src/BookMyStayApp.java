import java.util.HashMap;

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

    public void updateAvailability(String roomType, int newCount) {
        inventory.put(roomType, newCount);
    }

    public void displayInventory() {
        System.out.println("Current Room Availability:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type) + " rooms available");
        }
        System.out.println();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        SingleRoom single = new SingleRoom();
        DoubleRoom doubleR = new DoubleRoom();
        SuiteRoom suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.type, 5);
        inventory.addRoomType(doubleR.type, 3);
        inventory.addRoomType(suite.type, 2);

        System.out.println("Book My Stay - Hotel Booking System v3.1");
        System.out.println("========================================\n");

        single.displayDetails();
        System.out.println();
        doubleR.displayDetails();
        System.out.println();
        suite.displayDetails();
        System.out.println();

        inventory.displayInventory();
    }
}