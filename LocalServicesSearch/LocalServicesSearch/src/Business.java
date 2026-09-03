import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single local business/service provider entry.
 */
public class    Business {
    private String id;
    private String name;
    private String category;
    private List<String> keywords;
    private String location;
    private double rating;
    private String contact;
    private int viewCount; // for trend analysis

    public Business(String id, String name, String category, List<String> keywords,
                     String location, double rating, String contact) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.keywords = (keywords != null) ? keywords : new ArrayList<>();
        this.location = location;
        this.rating = rating;
        this.contact = contact;
        this.viewCount = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public List<String> getKeywords() { return keywords; }
    public String getLocation() { return location; }
    public double getRating() { return rating; }
    public String getContact() { return contact; }
    public int getViewCount() { return viewCount; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    public void setLocation(String location) { this.location = location; }
    public void setRating(double rating) { this.rating = rating; }
    public void setContact(String contact) { this.contact = contact; }
    public void incrementViewCount() { this.viewCount++; }

    @Override
    public String toString() {
        StringBuilder kw = new StringBuilder();
        for (int i = 0; i < keywords.size(); i++) {
            kw.append(keywords.get(i));
            if (i != keywords.size() - 1) kw.append(", ");
        }
        return String.format(
            "ID: %-5s | Name: %-25s | Category: %-12s | Location: %-15s | Rating: %.1f | Contact: %-12s | Keywords: [%s]",
            id, name, category, location, rating, contact, kw.toString()
        );
    }
}
