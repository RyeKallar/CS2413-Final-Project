import java.util.*;

// All data is static to make it a global variable, usable by all Classes
public class Data {

    // User ID -> Role
    public static Map<String, String> users = new HashMap<>();

    // Course ID -> Course name
    public static Map<String, String> courses = new HashMap<>();

    // Course ID -> Professor ID
    public static Map<String, String> professors = new HashMap<>();

    // Student ID -> List of enrolled course IDs
    public static Map<String, List<String>> enrollments = new HashMap<>();

    // Student ID -> (Course ID -> Grade)
    public static Map<String, Map<String, Double>> grades = new HashMap<>();

    // static init
    static {
        // Sample data for testing, should be replaced with more
        Data.users.put("S001", "student");
        Data.users.put("S002", "student");
        Data.users.put("P001", "professor");
        Data.users.put("D001", "dean");

        Data.courses.put("CS2413", "Information Security");
        Data.courses.put("CS2063", "Intro to Mobile App Development");

        Data.professors.put("CS2413", "P001");
        Data.professors.put("CS2063", "P001");

        // create a list of enrolled classes for S001
        Data.enrollments.put(
            "S001",
            new ArrayList<>(List.of("CS2413"))
        );

        // create a list of enrolled classes for S002
        Data.enrollments.put(
            "S002",
            new ArrayList<>(List.of("CS2413", "CS2063"))
        );

        // add grade for class for student S001
        Data.grades.put(
            "S001",
            new HashMap<>(Map.of("CS2413", 85.0))
        );

        // add grade for class for student S002
        Data.grades.put(
            "S002",
            new HashMap<>(Map.of("CS2413", 85.0, "CS2063", 92.0))
        );
    }
}