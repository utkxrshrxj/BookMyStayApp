import java.util.*;

class Service {
    String name;
    double price;

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void displayService() {
        System.out.println(name + " ($" + price + ")");
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

class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    public void addServiceToReservation(Reservation reservation, Service service) {
        reservationServices.putIfAbsent(reservation.roomId, new ArrayList<>());
        reservationServices.get(reservation.roomId).add(service);
    }

    public double calculateTotalAdditionalCost(Reservation reservation) {
        double total = 0;
        List<Service> services = reservationServices.getOrDefault(reservation.roomId, new ArrayList<>());
        for (Service s : services) {
            total += s.price;
        }
        return total;
    }

    public void displayServices(Reservation reservation) {
        List<Service> services = reservationServices.getOrDefault(reservation.roomId, new ArrayList<>());
        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }
        System.out.println("Add-On Services for Room ID " + reservation.roomId + ":");
        for (Service s : services) {
            s.displayService();
        }
        System.out.println("Total Additional Cost: $" + calculateTotalAdditionalCost(reservation));
        System.out.println();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        Reservation r1 = new Reservation("Alice", "Single Room", 2, "SI1");
        Reservation r2 = new Reservation("Bob", "Double Room", 3, "DO2");

        Service breakfast = new Service("Breakfast", 15.0);
        Service airportPickup = new Service("Airport Pickup", 30.0);
        Service spa = new Service("Spa Access", 50.0);

        AddOnServiceManager manager = new AddOnServiceManager();

        manager.addServiceToReservation(r1, breakfast);
        manager.addServiceToReservation(r1, spa);
        manager.addServiceToReservation(r2, airportPickup);

        System.out.println("Book My Stay - Hotel Booking System v7.1");
        System.out.println("========================================\n");

        r1.displayReservation();
        manager.displayServices(r1);

        r2.displayReservation();
        manager.displayServices(r2);
    }
}