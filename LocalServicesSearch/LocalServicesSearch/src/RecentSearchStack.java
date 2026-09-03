import java.util.ArrayList;
import java.util.List;

/**
 * Custom Stack implementation (singly linked list based) used to track the
 * user's recent search queries. Maintains at most MAX_SIZE entries; oldest
 * entries are dropped once the limit is exceeded. Duplicate consecutive
 * pushes are avoided so the same query isn't repeated back-to-back.
 */
public class RecentSearchStack {

    private static class Node {
        String query;
        Node next;
        Node(String query) { this.query = query; }
    }

    private Node top;
    private int size;
    private final int maxSize;

    public RecentSearchStack(int maxSize) {
        this.maxSize = maxSize;
        this.size = 0;
    }

    public RecentSearchStack() {
        this(5);
    }

    public void push(String query) {
        // Avoid pushing the exact same query as the most recent one
        if (top != null && top.query.equalsIgnoreCase(query)) {
            return;
        }

        Node newNode = new Node(query);
        newNode.next = top;
        top = newNode;
        size++;

        // Drop oldest entries beyond maxSize
        if (size > maxSize) {
            trimToMaxSize();
        }
    }

    private void trimToMaxSize() {
        Node curr = top;
        int count = 1;
        while (curr != null && count < maxSize) {
            curr = curr.next;
            count++;
        }
        if (curr != null) {
            curr.next = null; // cut off anything beyond maxSize
        }
        size = maxSize;
    }

    public String pop() {
        if (isEmpty()) return null;
        String val = top.query;
        top = top.next;
        size--;
        return val;
    }

    public String peek() {
        return isEmpty() ? null : top.query;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    /** Returns recent searches, most recent first (natural stack order). */
    public List<String> getRecentSearches() {
        List<String> result = new ArrayList<>();
        Node curr = top;
        while (curr != null) {
            result.add(curr.query);
            curr = curr.next;
        }
        return result;
    }
}
