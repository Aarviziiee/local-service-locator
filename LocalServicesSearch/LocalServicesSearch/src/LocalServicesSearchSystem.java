import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * LocalServicesSearchSystem
 * ---------------------------------------------------------------
 * A Java console application simulating a "Just Dial"-style local
 * business directory and search system, built to demonstrate core
 * Data Structures & Algorithms concepts:
 *
 *   - Hash Table (custom, chaining)   -> O(1) avg lookup by ID, name, keyword
 *   - Binary Search Tree (custom)      -> sorted traversal, prefix auto-suggest
 *   - Stack (custom, linked list)      -> recent search history (last 5)
 *   - Queue (custom, linked list)      -> full search activity log
 *   - Sorting (Quick Sort)             -> rank by rating / alphabetical
 *   - Searching (Linear & Binary)      -> exact-match lookups
 *
 * Run: java LocalServicesSearchSystem
 */
public class LocalServicesSearchSystem {

    // ---- Core data structures ----
    private final HashTable<String, Business> businessById = new HashTable<>();
    private final HashTable<String, Business> businessByName = new HashTable<>();
    private final HashTable<String, List<Business>> keywordIndex = new HashTable<>();
    private final HashTable<String, Integer> categorySearchCount = new HashTable<>();
    private final BusinessBST businessBST = new BusinessBST();
    private final RecentSearchStack recentSearches = new RecentSearchStack(5);
    private final SearchLogQueue searchLog = new SearchLogQueue();

    private final Scanner scanner = new Scanner(System.in);
    private int nextIdCounter = 1;

    public static void main(String[] args) {
        LocalServicesSearchSystem system = new LocalServicesSearchSystem();
        system.loadSampleData();
        system.run();
    }

    // ================= MAIN MENU LOOP =================

    private void run() {
        boolean running = true;
        printBanner();
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addBusiness(); break;
                case "2": updateBusiness(); break;
                case "3": deleteBusiness(); break;
                case "4": viewAllBusinesses(); break;
                case "5": searchByName(); break;
                case "6": searchByCategory(); break;
                case "7": searchByKeyword(); break;
                case "8": searchByPrefix(); break;
                case "9": showRecentSearches(); break;
                case "10": filterAndSort(); break;
                case "11": showSearchLog(); break;
                case "12": showTrendAnalysis(); break;
                case "0":
                    running = false;
                    System.out.println("\nThank you for using Local Services Search System. Goodbye!\n");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid menu option.\n");
            }
        }
        scanner.close();
    }

    private void printBanner() {
        System.out.println("=================================================");
        System.out.println("   LOCAL SERVICES SEARCH SYSTEM (Just Dial Sim) ");
        System.out.println("=================================================");
    }

    private void printMenu() {
        System.out.println("--------------------- MENU ----------------------");
        System.out.println(" 1.  Add Business");
        System.out.println(" 2.  Update Business");
        System.out.println(" 3.  Delete Business");
        System.out.println(" 4.  View All Businesses");
        System.out.println(" 5.  Search by Name");
        System.out.println(" 6.  Search by Category");
        System.out.println(" 7.  Search by Keyword");
        System.out.println(" 8.  Search by Prefix (Auto-suggest)");
        System.out.println(" 9.  Show Recent Searches (Last 5)");
        System.out.println("10.  Filter & Sort Results");
        System.out.println("11.  Show Search Log History");
        System.out.println("12.  Trend Analysis");
        System.out.println(" 0.  Exit");
        System.out.println("--------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    // ================= 1. ADD BUSINESS =================

    private void addBusiness() {
        System.out.println("\n--- Add New Business ---");

        String id = "B" + (nextIdCounter++);
        if (businessById.containsKey(id)) {
            System.out.println("Duplicate ID generated, please try again.");
            return;
        }

        System.out.print("Enter Business Name: ");
        String name = scanner.nextLine().trim();

        if (businessByName.containsKey(name.toLowerCase())) {
            System.out.println("A business with this name already exists. Entry rejected to avoid duplicates.\n");
            nextIdCounter--; // roll back unused ID
            return;
        }

        System.out.print("Enter Category (e.g., Plumber, Salon, Electrician): ");
        String category = scanner.nextLine().trim();

        System.out.print("Enter Keywords (comma-separated, e.g., pipe repair, leak fix): ");
        String keywordLine = scanner.nextLine().trim();
        List<String> keywords = new ArrayList<>();
        if (!keywordLine.isEmpty()) {
            for (String k : keywordLine.split(",")) {
                keywords.add(k.trim());
            }
        }

        System.out.print("Enter Location: ");
        String location = scanner.nextLine().trim();

        double rating = readDouble("Enter Rating (0.0 - 5.0): ", 0.0, 5.0);

        System.out.print("Enter Contact Number: ");
        String contact = scanner.nextLine().trim();

        Business business = new Business(id, name, category, keywords, location, rating, contact);

        // Insert into all data structures
        businessById.put(id, business);
        businessByName.put(name.toLowerCase(), business);
        businessBST.insert(business);
        for (String keyword : keywords) {
            indexKeyword(keyword, business);
        }

        System.out.println("Business added successfully with ID: " + id + "\n");
    }

    private void indexKeyword(String keyword, Business business) {
        String key = keyword.toLowerCase();
        List<Business> list = keywordIndex.get(key);
        if (list == null) {
            list = new ArrayList<>();
            keywordIndex.put(key, list);
        }
        list.add(business);
    }

    // ================= 2. UPDATE BUSINESS =================

    private void updateBusiness() {
        System.out.println("\n--- Update Business ---");
        System.out.print("Enter Business ID to update: ");
        String id = scanner.nextLine().trim();
        Business business = businessById.get(id);

        if (business == null) {
            System.out.println("No business found with ID: " + id + "\n");
            return;
        }

        System.out.println("Current details:\n  " + business);
        System.out.println("Leave field blank to keep current value.");

        System.out.print("New Category [" + business.getCategory() + "]: ");
        String category = scanner.nextLine().trim();
        if (!category.isEmpty()) business.setCategory(category);

        System.out.print("New Location [" + business.getLocation() + "]: ");
        String location = scanner.nextLine().trim();
        if (!location.isEmpty()) business.setLocation(location);

        System.out.print("New Rating [" + business.getRating() + "] (or blank): ");
        String ratingStr = scanner.nextLine().trim();
        if (!ratingStr.isEmpty()) {
            try {
                double r = Double.parseDouble(ratingStr);
                if (r >= 0.0 && r <= 5.0) business.setRating(r);
                else System.out.println("Rating out of range, kept original value.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, kept original value.");
            }
        }

        System.out.print("New Contact [" + business.getContact() + "]: ");
        String contact = scanner.nextLine().trim();
        if (!contact.isEmpty()) business.setContact(contact);

        System.out.println("Business updated successfully.\n");
    }

    // ================= 3. DELETE BUSINESS =================

    private void deleteBusiness() {
        System.out.println("\n--- Delete Business ---");
        System.out.print("Enter Business ID to delete: ");
        String id = scanner.nextLine().trim();
        Business business = businessById.get(id);

        if (business == null) {
            System.out.println("No business found with ID: " + id + "\n");
            return;
        }

        businessById.remove(id);
        businessByName.remove(business.getName().toLowerCase());
        businessBST.delete(business.getName());
        for (String keyword : business.getKeywords()) {
            List<Business> list = keywordIndex.get(keyword.toLowerCase());
            if (list != null) list.remove(business);
        }

        System.out.println("Business \"" + business.getName() + "\" deleted successfully.\n");
    }

    // ================= 4. VIEW ALL =================

    private void viewAllBusinesses() {
        System.out.println("\n--- All Registered Businesses (sorted by name via BST in-order) ---");
        List<Business> all = businessBST.inOrder();
        if (all.isEmpty()) {
            System.out.println("No businesses registered yet.\n");
            return;
        }
        printBusinessList(all);
        System.out.println("Total businesses: " + all.size() + "\n");
    }

    // ================= 5. SEARCH BY NAME =================

    private void searchByName() {
        System.out.println("\n--- Search by Name ---");
        System.out.print("Enter business name: ");
        String name = scanner.nextLine().trim();

        Business result = businessByName.get(name.toLowerCase());
        logSearch(name, "NAME");

        if (result != null) {
            result.incrementViewCount();
            System.out.println("Result Found:\n  " + result + "\n");
        } else {
            System.out.println("No Match Found for \"" + name + "\".\n");
        }
    }

    // ================= 6. SEARCH BY CATEGORY =================

    private void searchByCategory() {
        System.out.println("\n--- Search by Category ---");
        System.out.print("Enter category (e.g., Plumber, Salon): ");
        String category = scanner.nextLine().trim();

        List<Business> matches = new ArrayList<>();
        for (Business b : businessById.values()) {
            if (b.getCategory().equalsIgnoreCase(category)) {
                matches.add(b);
            }
        }

        logSearch(category, "CATEGORY");
        incrementCategoryCount(category);

        if (matches.isEmpty()) {
            System.out.println("No Match Found for category \"" + category + "\".\n");
        } else {
            System.out.println("Result Found (" + matches.size() + " business(es)):");
            for (Business b : matches) b.incrementViewCount();
            printBusinessList(matches);
            System.out.println();
        }
    }

    private void incrementCategoryCount(String category) {
        String key = category.toLowerCase();
        Integer count = categorySearchCount.get(key);
        categorySearchCount.put(key, (count == null) ? 1 : count + 1);
    }

    // ================= 7. SEARCH BY KEYWORD =================

    private void searchByKeyword() {
        System.out.println("\n--- Search by Keyword ---");
        System.out.print("Enter keyword (e.g., pipe repair): ");
        String keyword = scanner.nextLine().trim();

        List<Business> matches = keywordIndex.get(keyword.toLowerCase());
        logSearch(keyword, "KEYWORD");

        if (matches == null || matches.isEmpty()) {
            System.out.println("No Match Found for keyword \"" + keyword + "\".\n");
        } else {
            System.out.println("Result Found (" + matches.size() + " business(es)):");
            for (Business b : matches) b.incrementViewCount();
            printBusinessList(matches);
            System.out.println();
        }
    }

    // ================= 8. SEARCH BY PREFIX (auto-suggest) =================

    private void searchByPrefix() {
        System.out.println("\n--- Search by Prefix (Auto-suggest) ---");
        System.out.print("Enter prefix (e.g., 'Gla' for GlamLook): ");
        String prefix = scanner.nextLine().trim();

        List<Business> matches = businessBST.prefixSearch(prefix);
        logSearch(prefix, "PREFIX");

        if (matches.isEmpty()) {
            System.out.println("No suggestions found for prefix \"" + prefix + "\".\n");
        } else {
            System.out.println("Suggestions (" + matches.size() + "):");
            printBusinessList(matches);
            System.out.println();
        }
    }

    private void logSearch(String query, String type) {
        searchLog.enqueue(query, type);
        recentSearches.push(query);
    }

    // ================= 9. RECENT SEARCHES (STACK) =================

    private void showRecentSearches() {
        System.out.println("\n--- Recent Searches (Last " + 5 + ", Most Recent First) ---");
        List<String> recent = recentSearches.getRecentSearches();
        if (recent.isEmpty()) {
            System.out.println("No searches performed yet.\n");
            return;
        }
        int i = 1;
        for (String query : recent) {
            System.out.println(i++ + ". " + query);
        }
        System.out.println();
    }

    // ================= 10. FILTER & SORT =================

    private void filterAndSort() {
        System.out.println("\n--- Filter & Sort Results ---");
        System.out.print("Filter by Category (blank = all): ");
        String category = scanner.nextLine().trim();
        System.out.print("Filter by Location (blank = all): ");
        String location = scanner.nextLine().trim();

        List<Business> filtered = new ArrayList<>();
        for (Business b : businessById.values()) {
            boolean matchCategory = category.isEmpty() || b.getCategory().equalsIgnoreCase(category);
            boolean matchLocation = location.isEmpty() || b.getLocation().equalsIgnoreCase(location);
            if (matchCategory && matchLocation) {
                filtered.add(b);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("No businesses match the given filters.\n");
            return;
        }

        System.out.println("Sort by: 1) Rating (High to Low)   2) Name (A-Z)");
        System.out.print("Enter choice: ");
        String sortChoice = scanner.nextLine().trim();

        SortUtils.SortField field = sortChoice.equals("2")
                ? SortUtils.SortField.NAME
                : SortUtils.SortField.RATING;
        SortUtils.quickSort(filtered, field);

        System.out.println("\nFiltered & Sorted Results (" + filtered.size() + "):");
        printBusinessList(filtered);
        System.out.println();
    }

    // ================= 11. SEARCH LOG (QUEUE) =================

    private void showSearchLog() {
        System.out.println("\n--- Full Search Log (Order of Entry) ---");
        List<SearchLogQueue.LogEntry> logs = searchLog.getAllLogs();
        if (logs.isEmpty()) {
            System.out.println("No search activity recorded yet.\n");
            return;
        }
        int i = 1;
        for (SearchLogQueue.LogEntry entry : logs) {
            System.out.println(i++ + ". " + entry);
        }
        System.out.println("Total searches logged: " + logs.size() + "\n");
    }

    // ================= 12. TREND ANALYSIS =================

    private void showTrendAnalysis() {
        System.out.println("\n--- Trend Analysis ---");

        // Most searched categories
        List<String> categoryKeys = categorySearchCount.keys();
        if (categoryKeys.isEmpty()) {
            System.out.println("No category searches recorded yet.");
        } else {
            System.out.println("Category search frequency:");
            // simple selection sort by count, descending (small list, fine for demo)
            List<String> sortedCategories = new ArrayList<>(categoryKeys);
            for (int i = 0; i < sortedCategories.size(); i++) {
                int maxIdx = i;
                for (int j = i + 1; j < sortedCategories.size(); j++) {
                    int cj = categorySearchCount.get(sortedCategories.get(j));
                    int cmax = categorySearchCount.get(sortedCategories.get(maxIdx));
                    if (cj > cmax) maxIdx = j;
                }
                String tmp = sortedCategories.get(i);
                sortedCategories.set(i, sortedCategories.get(maxIdx));
                sortedCategories.set(maxIdx, tmp);
            }
            for (String cat : sortedCategories) {
                System.out.println("  " + cat + " -> " + categorySearchCount.get(cat) + " search(es)");
            }
        }

        // Most viewed businesses
        System.out.println("\nMost Frequently Viewed Businesses:");
        List<Business> all = new ArrayList<>(businessById.values());
        all.sort((a, b) -> b.getViewCount() - a.getViewCount());
        boolean anyViewed = false;
        for (Business b : all) {
            if (b.getViewCount() > 0) {
                System.out.println("  " + b.getName() + " -> " + b.getViewCount() + " view(s)");
                anyViewed = true;
            }
        }
        if (!anyViewed) {
            System.out.println("  No businesses have been viewed yet.");
        }
        System.out.println();
    }

    // ================= HELPERS =================

    private void printBusinessList(List<Business> list) {
        for (Business b : list) {
            System.out.println("  " + b);
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) return val;
                System.out.println("Please enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, please try again.");
            }
        }
    }

    // ================= SAMPLE DATA =================

    private void loadSampleData() {
        addSample("Mehta Plumbing Services", "Plumber",
                Arrays.asList("pipe repair", "leak fix", "tap installation"),
                "Andheri East", 4.5, "9876543210");

        addSample("GlamLook Salon", "Salon",
                Arrays.asList("haircut", "facial", "makeup"),
                "Bandra", 4.7, "9876501234");

        addSample("TechFix Electronics", "Electrician",
                Arrays.asList("wiring", "switchboard repair", "fan installation"),
                "Kandivali", 4.2, "9876512345");

        addSample("FreshBites Cafe", "Cafe",
                Arrays.asList("coffee", "breakfast", "fast food"),
                "Andheri West", 4.0, "9876523456");

        addSample("Royal Plumbing Co", "Plumber",
                Arrays.asList("pipe repair", "bathroom fitting"),
                "Bandra", 3.9, "9876534567");

        addSample("Glow Beauty Parlour", "Salon",
                Arrays.asList("haircut", "bridal makeup"),
                "Andheri East", 4.8, "9876545678");
    }

    private void addSample(String name, String category, List<String> keywords,
                            String location, double rating, String contact) {
        String id = "B" + (nextIdCounter++);
        Business b = new Business(id, name, category, keywords, location, rating, contact);
        businessById.put(id, b);
        businessByName.put(name.toLowerCase(), b);
        businessBST.insert(b);
        for (String k : keywords) indexKeyword(k, b);
    }
}
