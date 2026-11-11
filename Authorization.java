// TODO (authorization owner): Implement RBAC0 authorization and resource-level checks.
// This is intentionally NOT a complete security implementation.
// Discuss and finalize API signatures with Main.java and Tests.java owners.
public class Authorization {
    private Authorization() { }

    // A useful shared predicate to implement first. Unknown user/action -> false.
    public static boolean hasPermission(String actorId, String actionName) {
        // TODO: Look up the actor's role in Data.users and check an allow-list
        // of permissions assigned to that role.
        return false; // Fail closed until implemented.
    }

    // TODO: Add protected entry points that check both role and record scope
    // BEFORE calling Actions methods, e.g.:
    // viewGrades(actorId, studentId, courseId)
    // modifyGrade(actorId, studentId, courseId, newGrade)
    // createCourse(actorId, courseId, courseName)
    // assignProfessor(actorId, courseId, professorId)
    //
    // Students: only own grades.
    // Professors: grades only for courses they teach.
    // Deans: any enrolled student's grades.
    // All protected requests should be denied by default.
}
