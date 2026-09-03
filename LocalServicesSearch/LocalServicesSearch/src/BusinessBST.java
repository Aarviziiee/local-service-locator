import java.util.ArrayList;
import java.util.List;

/**
 * Custom Binary Search Tree, keyed on (lowercased) business name.
 * Supports:
 *  - insertion / deletion
 *  - exact lookup
 *  - in-order traversal (gives businesses sorted alphabetically by name)
 *  - prefix search for auto-suggestions
 */
public class BusinessBST {

    private static class TreeNode {
        String key;          // lowercase business name, used for ordering
        Business business;
        TreeNode left, right;

        TreeNode(String key, Business business) {
            this.key = key;
            this.business = business;
        }
    }

    private TreeNode root;
    private int size;

    public void insert(Business business) {
        String key = business.getName().toLowerCase();
        root = insertRec(root, key, business);
    }

    private TreeNode insertRec(TreeNode node, String key, Business business) {
        if (node == null) {
            size++;
            return new TreeNode(key, business);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRec(node.left, key, business);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, key, business);
        } else {
            // duplicate name -> update reference
            node.business = business;
        }
        return node;
    }

    public Business search(String name) {
        TreeNode node = searchRec(root, name.toLowerCase());
        return (node != null) ? node.business : null;
    }

    private TreeNode searchRec(TreeNode node, String key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node;
        return (cmp < 0) ? searchRec(node.left, key) : searchRec(node.right, key);
    }

    public void delete(String name) {
        root = deleteRec(root, name.toLowerCase());
    }

    private TreeNode deleteRec(TreeNode node, String key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = deleteRec(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, key);
        } else {
            // found node to delete
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }

            // two children: replace with in-order successor (smallest in right subtree)
            // Note: size is decremented once, inside the recursive call below that
            // actually removes the successor node from the right subtree.
            TreeNode successor = findMin(node.right);
            node.key = successor.key;
            node.business = successor.business;
            node.right = deleteRec(node.right, successor.key);
        }
        return node;
    }

    private TreeNode findMin(TreeNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    /** In-order traversal: returns businesses sorted alphabetically by name. */
    public List<Business> inOrder() {
        List<Business> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(TreeNode node, List<Business> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.business);
        inOrderRec(node.right, result);
    }

    /**
     * Prefix search for auto-suggest feature.
     * Performs an in-order traversal of the BST and collects all businesses
     * whose (lowercased) name starts with the given prefix. In-order
     * traversal means results come back already sorted alphabetically.
     * Runs in O(n) over the businesses currently stored, which is more
     * than fast enough for a directory of this scale.
     */
    public List<Business> prefixSearch(String prefix) {
        List<Business> result = new ArrayList<>();
        prefixSearchRec(root, prefix.toLowerCase(), result);
        return result;
    }

    private void prefixSearchRec(TreeNode node, String prefix, List<Business> result) {
        if (node == null) return;

        // Traverse left subtree first (keeps results alphabetically ordered)
        prefixSearchRec(node.left, prefix, result);

        if (node.key.startsWith(prefix)) {
            result.add(node.business);
        }

        // Traverse right subtree
        prefixSearchRec(node.right, prefix, result);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }
}
