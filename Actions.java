import java.util.*;

public class Actions {

    // VIEW COURSES FUNCTIONS

    // Function to get a specific student's courses
    public static Map<String, String> getStudentCourses(String studentId) {
        Map<String, String> result = new HashMap<>();

        // for each course in a students enrollment list add to result
        for (String courseId :
                Data.enrollments.getOrDefault(studentId, List.of())) {
            result.put(courseId, Data.courses.get(courseId));
        }

        return result;
    }

    // Function to get a specific professor's courses
    public static Map<String, String> getProfessorCourses(String professorId) {
        Map<String, String> result = new HashMap<>();

        // for each course taught by the professor, add to result
        for (String courseId : Data.professors.keySet()) {
            if (professorId.equals(Data.professors.get(courseId))) {
                result.put(courseId, Data.courses.get(courseId));
            }
        }

        return result;
    }

    // Function to get all courses
    public static Map<String, String> getAllCourses() {
        return new HashMap<>(Data.courses);
    }


    // VIEW GRADES FUNCTIONS

    // Function to get grades for specific student
    public static Map<String, Double> getGrades(String studentId) {
        return new HashMap<>(
            Data.grades.getOrDefault(studentId, Map.of())
        );
    }

    // Function to get grade for specific student and course
    public static Double getGrades(String studentId, String courseId) {
        Map<String, Double> studentGrades = Data.grades.get(studentId);

        if (studentGrades == null) {
            return null;
        }

        return studentGrades.get(courseId);
    }

    // MODIFY GRADE FUNCTION

    // Function to modify grade for a specific student's course
    public static boolean modifyGrade(
            String studentId, String courseId, double newGrade) {

        // check student exists
        if (!"student".equals(Data.users.get(studentId))) {
            return false;
        }

        // check course exists
        if (!Data.courses.containsKey(courseId)) {
            return false;
        }

        // check if student's enrolled in course
        if (!Data.enrollments
                .getOrDefault(studentId, List.of())
                .contains(courseId)) {
            return false;
        }

        // check if new grade is within limits and isn't NaN
        if (newGrade < 0 || newGrade > 100 || !Double.isFinite(newGrade)) {
            return false;
        }

        // create new grade or update existing one
        Data.grades
            .computeIfAbsent(studentId, k -> new HashMap<>())
            .put(courseId, newGrade);

        return true;
    }

    // CREATE COURSE FUNCTION

    // Function to create a new course
    public static boolean createCourse(
            String courseId, String courseName) {

        // check both params have a value
        if (courseId == null || courseId.isBlank()
                || courseName == null || courseName.isBlank()) {
            return false;
        }

        // check if course already exits
        if (Data.courses.containsKey(courseId)) {
            return false;
        }

        // add new course to course list
        Data.courses.put(courseId, courseName);
        return true;
    }

    // ASSIGN PROFESSOR FUNCTION

    // Function to assign a professor to an existing course
    public static boolean assignProfessor(
            String courseId, String professorId) {

        // check course exists
        if (!Data.courses.containsKey(courseId)) {
            return false;
        }

        // check professor exisits
        if (!"professor".equals(Data.users.get(professorId))) {
            return false;
        }

        // assign professor to course
        Data.professors.put(courseId, professorId);
        return true;
    }
}