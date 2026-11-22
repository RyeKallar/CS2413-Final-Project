import java.util.*;

public class Main {
    private static final Scanner INPUT = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== University RBAC Demonstration ===");
        while (true) {
            String actorId = chooseUser();
            if (actorId == null) break;
            runSession(actorId);
        }
        System.out.println("Goodbye!");
    }

    private static String chooseUser() {
        while (true) {
            System.out.println("\nAvailable users:");
            Data.users.keySet().stream().sorted().forEach(id ->
                System.out.println("  " + id + " (" + Data.users.get(id) + ")"));
            String id = prompt("User ID (or 0 to quit): ");
            if (id == null || id.equals("0")) return null;
            if (Data.users.containsKey(id)) return id;
            System.out.println("Unknown user ID. Choose one of the listed demo users.");
        }
    }

    private static void runSession(String actorId) {
        while (true) {
            System.out.println("\n=== Acting as " + actorId + " (" + Data.users.get(actorId) + ") ===");      
            System.out.println("1. View my courses");
            System.out.println("2. View a student's grades (all accessible courses)");
            System.out.println("3. View a specific course grade");
            System.out.println("4. Modify or create a grade");
            System.out.println("5. Create a course");
            System.out.println("6. Assign a professor to a course");
            System.out.println("7. Switch user");
            System.out.println("0. Exit");
            String choice = prompt("Option: ");
            if (choice == null || choice.equals("0")) System.exit(0);
            if (choice.equals("7")) return;
            switch (choice) {
                case "1": viewCourses(actorId); break;
                case "2": viewGrades(actorId); break;
                case "3": viewGrade(actorId); break;
                case "4": modifyGrade(actorId); break;
                case "5": createCourse(actorId); break;
                case "6": assignProfessor(actorId); break;
                default: System.out.println("Invalid option; enter 0-7.");
            }
        }
    }

    private static void viewCourses(String actorId) {
        Map<String, String> courses = Authorization.viewCourses(actorId);
        if (courses == null) {
            System.out.println("ACCESS DENIED: cannot view courses.");
        } else if (courses.isEmpty()) {
            System.out.println("No courses to display.");
        } else {
            System.out.println("Courses:");
            courses.keySet().stream().sorted().forEach(id ->
                System.out.println("  " + id + " - " + courses.get(id)));
        }
    }

    private static void viewGrades(String actorId) {
        String studentId = prompt("Student ID: ");
        if (studentId == null) return;
        Map<String, Double> grades = Authorization.viewGrades(actorId, studentId);
        if (grades == null) {
            System.out.println("ACCESS DENIED or invalid student ID.");
        } else if (grades.isEmpty()) {
            System.out.println("No grades accessible for this student.");
        } else {
            System.out.println("Accessible grades for " + studentId + ":");
            grades.keySet().stream().sorted().forEach(courseId ->
                System.out.println("  " + courseId + " - "
                    + Data.courses.getOrDefault(courseId, "Unknown course")
                    + ": " + grades.get(courseId)));
        }
    }

    private static void viewGrade(String actorId) {
        String studentId = prompt("Student ID: ");
        if (studentId == null) return;
        String courseId = prompt("Course ID: ");
        if (courseId == null) return;
        Double grade = Authorization.viewGrade(actorId, studentId, courseId);
        if (grade == null) {
            System.out.println("ACCESS DENIED, invalid request, or no grade recorded.");
        } else {
            System.out.println("Grade for " + studentId + " in " + courseId + ": " + grade);
        }
    }

    private static void modifyGrade(String actorId) {
        if (!Authorization.hasPermission(actorId, "modify_grade")) {
            System.out.println("ACCESS DENIED: your role cannot modify grades.");
            return;
        }
        String studentId = prompt("Student ID: ");
        if (studentId == null) return;
        String courseId = prompt("Course ID: ");
        if (courseId == null) return;
        String input = prompt("New grade (0-100): ");
        if (input == null) return;
        double grade;
        try {
            grade = Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid grade: enter a number from 0 to 100.");
            return;
        }
        boolean success = Authorization.modifyGrade(actorId, studentId, courseId, grade);
        System.out.println(success ? "Grade saved." : "ACCESS DENIED or invalid grade/student/course/enrollment.");
    }

    private static void createCourse(String actorId) {
        if (!Authorization.hasPermission(actorId, "create_course")) {
            System.out.println("ACCESS DENIED: only deans can create courses.");
            return;
        }
        String courseId = prompt("New course ID: ");
        if (courseId == null) return;
        String name = prompt("Course name: ");
        if (name == null) return;
        boolean success = Authorization.createCourse(actorId, courseId, name);
        System.out.println(success ? "Course created." : "Could not create course (invalid or duplicate ID/name).");
    }

    private static void assignProfessor(String actorId) {
        if (!Authorization.hasPermission(actorId, "assign_professor")) {
            System.out.println("ACCESS DENIED: only deans can assign professors.");
            return;
        }
        String courseId = prompt("Course ID: ");
        if (courseId == null) return;
        String professorId = prompt("Professor ID: ");
        if (professorId == null) return;
        boolean success = Authorization.assignProfessor(actorId, courseId, professorId);
        System.out.println(success ? "Professor assigned." : "Could not assign professor (invalid course or professor).");
    }

    private static String prompt(String text) {
        System.out.print(text);
        if (!INPUT.hasNextLine()) return null;
        return INPUT.nextLine().trim();
    }
}

