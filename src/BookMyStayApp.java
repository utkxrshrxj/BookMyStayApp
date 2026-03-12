import java.util.LinkedList;
import java.util.Queue;

class Reservation {
    String guestName;
    String roomType;
    int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public void displayRequest() {
        System.out.println("Guest: " + guestName + ", Room: " + roomType + ", Nights: " + nights);
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

    public void displayRequests() {
        System.out.println("Booking Requests in Queue (First-Come-First-Served):");
        for (Reservation r : requestQueue) {
            r.displayRequest();
        }
        System.out.println();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        Reservation r1 = new Reservation("Alice", "Single Room", 2);
        Reservation r2 = new Reservation("Bob", "Double Room", 3);
        Reservation r3 = new Reservation("Charlie", "Suite Room", 1);

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        System.out.println("Book My Stay - Hotel Booking System v5.1");
        System.out.println("========================================\n");

        bookingQueue.displayRequests();
    }
}