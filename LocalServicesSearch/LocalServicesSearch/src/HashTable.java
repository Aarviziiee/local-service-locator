import java.util.ArrayList;
import java.util.List;

/**
 * Custom Hash Table implementation using separate chaining for collision
 * resolution. Used for O(1) average-case lookup of businesses by ID/name,
 * and for keyword-to-business mapping.
 *
 * Generic so it can be reused for: ID->Business, keyword->List<Business>, etc.
 */
public class HashTable<K, V> {

    // A node in the chain (linked list) for collision handling
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V>[] buckets;
    private int capacity;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.buckets = new Node[capacity];
        this.size = 0;
    }

    public HashTable() {
        this(16);
    }

    // Simple hash function: Java's hashCode mapped into bucket range
    private int hash(K key) {
        int h = key.hashCode();
        h = h ^ (h >>> 16); // spread bits to reduce clustering
        return Math.abs(h) % capacity;
    }

    public void put(K key, V value) {
        if ((double) (size + 1) / capacity > LOAD_FACTOR_THRESHOLD) {
            resize();
        }
        int index = hash(key);
        Node<K, V> head = buckets[index];

        // If key already exists, update value
        Node<K, V> curr = head;
        while (curr != null) {
            if (curr.key.equals(key)) {
                curr.value = value;
                return;
            }
            curr = curr.next;
        }

        // Insert new node at head of chain
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = head;
        buckets[index] = newNode;
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        Node<K, V> curr = buckets[index];
        while (curr != null) {
            if (curr.key.equals(key)) {
                return curr.value;
            }
            curr = curr.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int index = hash(key);
        Node<K, V> curr = buckets[index];
        Node<K, V> prev = null;
        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return curr.value;
            }
            prev = curr;
            curr = curr.next;
        }
        return null;
    }

    public List<V> values() {
        List<V> result = new ArrayList<>();
        for (Node<K, V> head : buckets) {
            Node<K, V> curr = head;
            while (curr != null) {
                result.add(curr.value);
                curr = curr.next;
            }
        }
        return result;
    }

    public List<K> keys() {
        List<K> result = new ArrayList<>();
        for (Node<K, V> head : buckets) {
            Node<K, V> curr = head;
            while (curr != null) {
                result.add(curr.key);
                curr = curr.next;
            }
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        capacity *= 2;
        buckets = new Node[capacity];
        size = 0;
        for (Node<K, V> head : oldBuckets) {
            Node<K, V> curr = head;
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }
}
