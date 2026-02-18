import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:src/slideshow.db";
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void createSlidesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS slides ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "image TEXT NOT NULL, "
                    + "description TEXT NOT NULL,"
                    + "url TEXT NOT NULL,"
                    + ");";
        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table slides created (if it didn't exist).");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<Slide> getSlides() {
        List<Slide> slides = new ArrayList<>();
        String sql = "SELECT image, description FROM slides ORDER BY id";
        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                slides.add(new Slide(
                        rs.getString("image"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slides;
    }

        // CREATE - Add new slide
    public static void addSlide(String image, String description, String url) {
        String sql = "INSERT INTO slides (image, description, url) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, image);
            pstmt.setString(2, description);
            pstmt.setString(3, url);
            pstmt.executeUpdate();
            System.out.println("Added new slide: " + image);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE - Update slide by ID
    public static void updateSlide(int id, String image, String description, String url) {
        String sql = "UPDATE slides SET image = ?, description = ?, url = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, image);
            pstmt.setString(2, description);
            pstmt.setString(3, url);
            pstmt.setInt(4, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Updated slide ID: " + id);
            } else {
                System.out.println("Slide ID " + id + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE - Delete slide by ID
    public static void deleteSlide(int id) {
        String sql = "DELETE FROM slides WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deleted slide ID: " + id);
            } else {
                System.out.println("Slide ID " + id + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}