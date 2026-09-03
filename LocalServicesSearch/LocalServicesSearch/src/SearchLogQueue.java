import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Queue implementation (singly linked list based, FIFO) used to log
 * every search request in the order it was made. Supports full traversal
 * for displaying search history and trend analysis.
 */
public class SearchLogQueue {

    public static class LogEntry {
        String query;
        String type; // e.g. "NAME", "CATEGORY", "KEYWORD", "PREFIX"
        String timestamp;

        LogEntry(String query, String type) {
            this.query = query;
            this.type = type;
            this.timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        @Override
        public String toString() {
            return String.format("[%s] (%s) \"%s\"", timestamp, type, query);
        }
    }

    private static class Node {
        LogEntry entry;
        Node next;
        Node(LogEntry entry) { this.entry = entry; }
    }

    private Node front, rear;
    private int size;

    public void enqueue(String query, String type) {
        Node newNode = new Node(new LogEntry(query, type));
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public LogEntry dequeue() {
        if (isEmpty()) return null;
        LogEntry entry = front.entry;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return entry;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    /** Returns the full log in insertion order (oldest first). */
    public List<LogEntry> getAllLogs() {
        List<LogEntry> result = new ArrayList<>();
        Node curr = front;
        while (curr != null) {
            result.add(curr.entry);
            curr = curr.next;
        }
        return result;
    }
}
