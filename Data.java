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
        Data.users.put("S003", "student");
        Data.users.put("S004", "student");
        Data.users.put("P001", "professor");
        Data.users.put("P002", "professor");
        Data.users.put("D001", "dean");

        Data.courses.put("CS2413", "Information Security");
        Data.courses.put("CS2063", "Intro to Mobile App Development");
        Data.courses.put("CS1073", "Intro to Java 1");
        Data.courses.put("CS1083", "Intro to Java 2");

        Data.professors.put("CS2413", "P001");
        Data.professors.put("CS2063", "P001");
        Data.professors.put("CS1073", "P002");
        Data.professors.put("CS1083", "P002");

        // create a list of enrolled classes for S001
        Data.enrollments.put(
            "S001",
            new ArrayList<>(List.of("CS2413", "CS1073"))
        );
        // create a list of enrolled classes for S002
        Data.enrollments.put(
            "S002",
            new ArrayList<>(List.of("CS2413", "CS2063"))
        );
        // create a list of enrolled classes for S003
        Data.enrollments.put(
            "S003",
            new ArrayList<>(List.of("CS1083", "CS2063"))
        );
        // create a list of enrolled classes for S004
        Data.enrollments.put(
            "S004",
            new ArrayList<>(List.of("CS2413", "CS1083", "CS2063"))
        );

        // add grade for class for student S001
        Data.grades.put(
            "S001",
            new HashMap<>(Map.of("CS2413", 85.0, "CS1073", 76.0))
        );
        // add grade for class for student S002
        Data.grades.put(
            "S002",
            new HashMap<>(Map.of("CS2413", 80.7, "CS2063", 92.0))
        );
        // add grade for class for student S003 (No grade for CS2063 to allow Demo to do it)
        Data.grades.put(
            "S003",
            new HashMap<>(Map.of("CS1083", 82.3))
        );
        // add grade for class for student S004
        Data.grades.put(
            "S004",
            new HashMap<>(Map.of("CS2413", 96.6, "CS1083", 90.1, "CS2063", 88.0))
        );
    }
}