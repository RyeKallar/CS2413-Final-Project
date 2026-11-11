import java.util.*;

// Action-only smoke tests. These do NOT test authorization / RBAC.
// TODO (testing owner): Expand these and create controlled security experiments.
public class Tests {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        System.out.println((condition ? "[PASS] " : "[FAIL] ") + name);
        if (condition) passed++; else failed++;
    }

    public static void main(String[] args) {
        check("Student sees enrolled course", Actions.getStudentCourses("S001").containsKey("CS2413"));
        check("Professor sees assigned course", Actions.getProfessorCourses("P001").containsKey("CS2413"));
        check("All courses contains CS2063", Actions.getAllCourses().containsKey("CS2063"));
        check("Existing student grade returned", Objects.equals(Actions.getGrades("S001", "CS2413"), 85.0));
        check("Unknown student grades empty", Actions.getGrades("UNKNOWN").isEmpty());

        // Use isolated test IDs to avoid changing the supplied sample records.
        Data.users.put("TEST_S", "student");
        Data.courses.put("TEST_C", "Temporary Course");
        Data.enrollments.put("TEST_S", new ArrayList<>(List.of("TEST_C")));
        try {
            check("Grade created for enrolled student", Actions.modifyGrade("TEST_S", "TEST_C", 88.0));
            check("Created grade persisted", Objects.equals(Actions.getGrades("TEST_S", "TEST_C"), 88.0));
            check("Grade updated", Actions.modifyGrade("TEST_S", "TEST_C", 93.0));
            check("Updated grade persisted", Objects.equals(Actions.getGrades("TEST_S", "TEST_C"), 93.0));
            check("Out-of-range grade rejected", !Actions.modifyGrade("TEST_S", "TEST_C", 101.0));
        } finally {
            Data.grades.remove("TEST_S");
            Data.enrollments.remove("TEST_S");
            Data.users.remove("TEST_S");
            Data.courses.remove("TEST_C");
        }

        check("Create course succeeds", Actions.createCourse("TEST_NEW", "New Test Course"));
        check("Duplicate course denied", !Actions.createCourse("TEST_NEW", "Duplicate"));
        Data.courses.remove("TEST_NEW");

        Data.users.put("TEST_P", "professor");
        Data.courses.put("TEST_ASSIGN", "Assignment Test Course");
        try {
            check("Assign professor succeeds", Actions.assignProfessor("TEST_ASSIGN", "TEST_P"));
            check("Assignment persisted", "TEST_P".equals(Data.professors.get("TEST_ASSIGN")));
        } finally {
            Data.professors.remove("TEST_ASSIGN");
            Data.courses.remove("TEST_ASSIGN");
            Data.users.remove("TEST_P");
        }

        System.out.println("\nActions-only tests: " + passed + " passed, " + failed + " failed.");
        if (failed > 0) System.exit(1);
    }
}
