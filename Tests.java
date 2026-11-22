import java.util.*;

public class Tests {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        System.out.println((condition ? "[PASS] " : "[FAIL] ") + name);

        if (condition) {
            passed++;
        } else {
            failed++;
        }
    }

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("CS2413 RBAC SECURITY TESTS");
        System.out.println("========================================");

        runActionTests();
        runPermissionTests();
        runCourseAuthorizationTests();
        runGradeViewingTests();
        runGradeModificationTests();
        runAdministrativeTests();
        runSecurityExperiment();

        System.out.println("\n========================================");
        System.out.println("FINAL RESULTS");
        System.out.println("========================================");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runActionTests() {

        System.out.println("\n--- ACTION-ONLY TESTS ---");

        check(
            "Student sees an enrolled course",
            Actions.getStudentCourses("S001").containsKey("CS2413")
        );

        check(
            "Student does not see a non-enrolled course",
            !Actions.getStudentCourses("S001").containsKey("CS2063")
        );

        check(
            "Professor sees an assigned course",
            Actions.getProfessorCourses("P001").containsKey("CS2413")
        );

        check(
            "Professor does not see another professor's course",
            !Actions.getProfessorCourses("P001").containsKey("CS1073")
        );

        check(
            "All courses contains CS2063",
            Actions.getAllCourses().containsKey("CS2063")
        );

        check(
            "Existing grade is returned",
            Objects.equals(
                Actions.getGrades("S001", "CS2413"),
                85.0
            )
        );

        check(
            "Unknown student grades are empty",
            Actions.getGrades("UNKNOWN").isEmpty()
        );

        Data.users.put("TEST_ACTION_S", "student");
        Data.users.put("TEST_ACTION_P", "professor");
        Data.courses.put("TEST_ACTION_C", "Temporary Action Course");

        Data.enrollments.put(
            "TEST_ACTION_S",
            new ArrayList<>(List.of("TEST_ACTION_C"))
        );

        try {
            check(
                "Grade can be created for enrolled student",
                Actions.modifyGrade(
                    "TEST_ACTION_S",
                    "TEST_ACTION_C",
                    88.0
                )
            );

            check(
                "Created grade persists",
                Objects.equals(
                    Actions.getGrades(
                        "TEST_ACTION_S",
                        "TEST_ACTION_C"
                    ),
                    88.0
                )
            );

            check(
                "Grade can be updated",
                Actions.modifyGrade(
                    "TEST_ACTION_S",
                    "TEST_ACTION_C",
                    93.0
                )
            );

            check(
                "Updated grade persists",
                Objects.equals(
                    Actions.getGrades(
                        "TEST_ACTION_S",
                        "TEST_ACTION_C"
                    ),
                    93.0
                )
            );

            check(
                "Grade above 100 is rejected",
                !Actions.modifyGrade(
                    "TEST_ACTION_S",
                    "TEST_ACTION_C",
                    101.0
                )
            );

            check(
                "Negative grade is rejected",
                !Actions.modifyGrade(
                    "TEST_ACTION_S",
                    "TEST_ACTION_C",
                    -1.0
                )
            );

            check(
                "NaN grade is rejected",
                !Actions.modifyGrade(
                    "TEST_ACTION_S",
                    "TEST_ACTION_C",
                    Double.NaN
                )
            );

            check(
                "Infinite grade is rejected",
                !Actions.modifyGrade(
                    "TEST_ACTION_S",
                    "TEST_ACTION_C",
                    Double.POSITIVE_INFINITY
                )
            );

            check(
                "Professor can be assigned to valid course",
                Actions.assignProfessor(
                    "TEST_ACTION_C",
                    "TEST_ACTION_P"
                )
            );

            check(
                "Professor assignment persists",
                "TEST_ACTION_P".equals(
                    Data.professors.get("TEST_ACTION_C")
                )
            );

        } finally {
            Data.grades.remove("TEST_ACTION_S");
            Data.enrollments.remove("TEST_ACTION_S");
            Data.users.remove("TEST_ACTION_S");
            Data.professors.remove("TEST_ACTION_C");
            Data.courses.remove("TEST_ACTION_C");
            Data.users.remove("TEST_ACTION_P");
        }
    }

    private static void runPermissionTests() {

        System.out.println("\n--- ROLE PERMISSION TESTS ---");

        check(
            "Student has permission to view courses",
            Authorization.hasPermission(
                "S001",
                "view_courses"
            )
        );

        check(
            "Student has permission to view grades",
            Authorization.hasPermission(
                "S001",
                "view_grades"
            )
        );

        check(
            "Student cannot modify grades",
            !Authorization.hasPermission(
                "S001",
                "modify_grade"
            )
        );

        check(
            "Professor can modify grades",
            Authorization.hasPermission(
                "P001",
                "modify_grade"
            )
        );

        check(
            "Professor cannot create courses",
            !Authorization.hasPermission(
                "P001",
                "create_course"
            )
        );

        check(
            "Dean can create courses",
            Authorization.hasPermission(
                "D001",
                "create_course"
            )
        );

        check(
            "Dean can assign professors",
            Authorization.hasPermission(
                "D001",
                "assign_professor"
            )
        );

        check(
            "Unknown user is denied",
            !Authorization.hasPermission(
                "UNKNOWN",
                "view_grades"
            )
        );

        check(
            "Unknown action is denied",
            !Authorization.hasPermission(
                "D001",
                "delete_university"
            )
        );

        check(
            "Null actor is denied",
            !Authorization.hasPermission(
                null,
                "view_grades"
            )
        );

        check(
            "Null action is denied",
            !Authorization.hasPermission(
                "D001",
                null
            )
        );
    }

    private static void runCourseAuthorizationTests() {

        System.out.println("\n--- COURSE AUTHORIZATION TESTS ---");

        Map<String, String> studentCourses =
            Authorization.viewCourses("S001");

        check(
            "Student sees enrolled CS2413",
            studentCourses != null
                && studentCourses.containsKey("CS2413")
        );

        check(
            "Student does not see non-enrolled CS2063",
            studentCourses != null
                && !studentCourses.containsKey("CS2063")
        );

        Map<String, String> professorCourses =
            Authorization.viewCourses("P001");

        check(
            "Professor sees assigned CS2413",
            professorCourses != null
                && professorCourses.containsKey("CS2413")
        );

        check(
            "Professor does not see P002 course CS1073",
            professorCourses != null
                && !professorCourses.containsKey("CS1073")
        );

        Map<String, String> deanCourses =
            Authorization.viewCourses("D001");

        check(
            "Dean sees all four courses",
            deanCourses != null
                && deanCourses.size() == 4
        );

        check(
            "Unknown user cannot view courses",
            Authorization.viewCourses("UNKNOWN") == null
        );
    }

    private static void runGradeViewingTests() {

        System.out.println("\n--- GRADE VIEWING TESTS ---");

        check(
            "Student can view own grade",
            Objects.equals(
                Authorization.viewGrade(
                    "S001",
                    "S001",
                    "CS2413"
                ),
                85.0
            )
        );

        check(
            "Student cannot view another student's grade",
            Authorization.viewGrade(
                "S001",
                "S002",
                "CS2413"
            ) == null
        );

        check(
            "P001 can view grade in assigned CS2413",
            Objects.equals(
                Authorization.viewGrade(
                    "P001",
                    "S001",
                    "CS2413"
                ),
                85.0
            )
        );

        check(
            "P001 cannot view grade in P002 course CS1073",
            Authorization.viewGrade(
                "P001",
                "S001",
                "CS1073"
            ) == null
        );

        check(
            "P002 can view grade in assigned CS1073",
            Objects.equals(
                Authorization.viewGrade(
                    "P002",
                    "S001",
                    "CS1073"
                ),
                76.0
            )
        );

        check(
            "Dean can view a student's grade",
            Objects.equals(
                Authorization.viewGrade(
                    "D001",
                    "S002",
                    "CS2063"
                ),
                92.0
            )
        );

        Map<String, Double> p1Grades =
            Authorization.viewGrades("P001", "S001");

        check(
            "P001 sees S001 grade for CS2413",
            p1Grades != null
                && p1Grades.containsKey("CS2413")
        );

        check(
            "P001 does not see S001 grade for CS1073",
            p1Grades != null
                && !p1Grades.containsKey("CS1073")
        );

        check(
            "Unknown user cannot view grade",
            Authorization.viewGrade(
                "UNKNOWN",
                "S001",
                "CS2413"
            ) == null
        );
    }

    private static void runGradeModificationTests() {

        System.out.println("\n--- GRADE MODIFICATION TESTS ---");

        double originalGrade =
            Actions.getGrades("S001", "CS2413");

        check(
            "Student cannot modify own grade",
            !Authorization.modifyGrade(
                "S001",
                "S001",
                "CS2413",
                100.0
            )
        );

        check(
            "Denied student modification leaves grade unchanged",
            Objects.equals(
                Actions.getGrades("S001", "CS2413"),
                originalGrade
            )
        );

        try {
            check(
                "P001 can modify grade in assigned CS2413",
                Authorization.modifyGrade(
                    "P001",
                    "S001",
                    "CS2413",
                    87.0
                )
            );

            check(
                "Authorized professor modification persists",
                Objects.equals(
                    Actions.getGrades("S001", "CS2413"),
                    87.0
                )
            );
        } finally {
            Actions.modifyGrade(
                "S001",
                "CS2413",
                originalGrade
            );
        }

        double cs1073Grade =
            Actions.getGrades("S001", "CS1073");

        check(
            "P001 cannot modify grade in P002 course CS1073",
            !Authorization.modifyGrade(
                "P001",
                "S001",
                "CS1073",
                100.0
            )
        );

        check(
            "Denied professor modification leaves grade unchanged",
            Objects.equals(
                Actions.getGrades("S001", "CS1073"),
                cs1073Grade
            )
        );

        check(
            "P001 cannot modify a non-enrolled course",
            !Authorization.modifyGrade(
                "P001",
                "S001",
                "CS2063",
                80.0
            )
        );

        double deanTestGrade =
            Actions.getGrades("S002", "CS2063");

        try {
            check(
                "Dean can modify enrolled student's grade",
                Authorization.modifyGrade(
                    "D001",
                    "S002",
                    "CS2063",
                    94.0
                )
            );

            check(
                "Dean modification persists",
                Objects.equals(
                    Actions.getGrades("S002", "CS2063"),
                    94.0
                )
            );
        } finally {
            Actions.modifyGrade(
                "S002",
                "CS2063",
                deanTestGrade
            );
        }

        check(
            "Invalid grade is rejected through authorization",
            !Authorization.modifyGrade(
                "D001",
                "S002",
                "CS2063",
                101.0
            )
        );
    }

    private static void runAdministrativeTests() {

        System.out.println("\n--- ADMINISTRATIVE AUTHORIZATION TESTS ---");

        String courseId = "TEST_ADMIN_C";

        try {
            check(
                "Student cannot create a course",
                !Authorization.createCourse(
                    "S001",
                    courseId,
                    "Unauthorized Student Course"
                )
            );

            check(
                "Denied student course was not created",
                !Data.courses.containsKey(courseId)
            );

            check(
                "Professor cannot create a course",
                !Authorization.createCourse(
                    "P001",
                    courseId,
                    "Unauthorized Professor Course"
                )
            );

            check(
                "Denied professor course was not created",
                !Data.courses.containsKey(courseId)
            );

            check(
                "Dean can create a course",
                Authorization.createCourse(
                    "D001",
                    courseId,
                    "Dean Created Course"
                )
            );

            check(
                "Dean-created course persists",
                Data.courses.containsKey(courseId)
            );

            check(
                "Student cannot assign professor",
                !Authorization.assignProfessor(
                    "S001",
                    courseId,
                    "P001"
                )
            );

            check(
                "Professor cannot assign professor",
                !Authorization.assignProfessor(
                    "P001",
                    courseId,
                    "P001"
                )
            );

            check(
                "Dean can assign professor",
                Authorization.assignProfessor(
                    "D001",
                    courseId,
                    "P001"
                )
            );

            check(
                "Dean professor assignment persists",
                "P001".equals(
                    Data.professors.get(courseId)
                )
            );

        } finally {
            Data.professors.remove(courseId);
            Data.courses.remove(courseId);
        }
    }

    private static void runSecurityExperiment() {

        System.out.println("\n========================================");
        System.out.println("CONTROLLED BEFORE-AND-AFTER EXPERIMENT");
        System.out.println("========================================");

        double originalGrade =
            Actions.getGrades("S001", "CS2413");

        try {
            System.out.println("\nBEFORE RBAC: UNSECURED ACTION");

            System.out.println(
                "Grade before unauthorized operation: "
                    + Actions.getGrades("S001", "CS2413")
            );

            boolean unsecuredResult =
                Actions.modifyGrade(
                    "S001",
                    "CS2413",
                    100.0
                );

            System.out.println(
                "Actions.modifyGrade returned: "
                    + unsecuredResult
            );

            System.out.println(
                "Grade after unauthorized operation: "
                    + Actions.getGrades("S001", "CS2413")
            );

            check(
                "Unsecured action allows grade modification",
                unsecuredResult
            );

            check(
                "Unsecured grade changed to 100",
                Objects.equals(
                    Actions.getGrades("S001", "CS2413"),
                    100.0
                )
            );

            Actions.modifyGrade(
                "S001",
                "CS2413",
                originalGrade
            );

            System.out.println("\nAFTER RBAC: PROTECTED ACTION");

            System.out.println(
                "Grade before protected attempt: "
                    + Actions.getGrades("S001", "CS2413")
            );

            boolean protectedResult =
                Authorization.modifyGrade(
                    "S001",
                    "S001",
                    "CS2413",
                    100.0
                );

            System.out.println(
                "Authorization.modifyGrade returned: "
                    + protectedResult
            );

            System.out.println(
                "Grade after protected attempt: "
                    + Actions.getGrades("S001", "CS2413")
            );

            check(
                "RBAC denies student grade modification",
                !protectedResult
            );

            check(
                "Protected grade remains unchanged",
                Objects.equals(
                    Actions.getGrades("S001", "CS2413"),
                    originalGrade
                )
            );

        } finally {
            Actions.modifyGrade(
                "S001",
                "CS2413",
                originalGrade
            );
        }
    }
}