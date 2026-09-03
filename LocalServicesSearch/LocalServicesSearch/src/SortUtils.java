import java.util.List;

/**
 * Explicit implementations of sorting/searching algorithms (rather than
 * relying on Collections.sort) so the DSA concepts are clearly demonstrated.
 */
public class SortUtils {

    public enum SortField { RATING, NAME }

    /**
     * Quick Sort (Divide and Conquer) on a list of businesses.
     * Descending order for RATING (best-rated first), ascending for NAME.
     */
    public static void quickSort(List<Business> list, SortField field) {
        if (list.size() <= 1) return;
        quickSortRec(list, 0, list.size() - 1, field);
    }

    private static void quickSortRec(List<Business> list, int low, int high, SortField field) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, field);
            quickSortRec(list, low, pivotIndex - 1, field);
            quickSortRec(list, pivotIndex + 1, high, field);
        }
    }

    private static int partition(List<Business> list, int low, int high, SortField field) {
        Business pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(list.get(j), pivot, field) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static int compare(Business a, Business b, SortField field) {
        if (field == SortField.RATING) {
            // descending: higher rating first
            return Double.compare(b.getRating(), a.getRating());
        } else {
            // ascending: alphabetical
            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

    private static void swap(List<Business> list, int i, int j) {
        Business temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Linear search through a list for an exact (case-insensitive) name match.
     * Used as a fallback / simple search demonstrating O(n) search.
     */
    public static Business linearSearchByName(List<Business> list, String name) {
        for (Business b : list) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Binary search on a list already sorted alphabetically by name.
     * Demonstrates O(log n) search on sorted data.
     */
    public static Business binarySearchByName(List<Business> sortedList, String name) {
        int low = 0, high = sortedList.size() - 1;
        String target = name.toLowerCase();
        while (low <= high) {
            int mid = (low + high) / 2;
            String midName = sortedList.get(mid).getName().toLowerCase();
            int cmp = midName.compareTo(target);
            if (cmp == 0) {
                return sortedList.get(mid);
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }
}
