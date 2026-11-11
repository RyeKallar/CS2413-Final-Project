# CS2413 — University Role-Based Access Control (RBAC) Demo

## Project purpose
A small Java command-line university system to demonstrate authorization through role-based access control (RBAC0) with additional record-ownership/course-assignment checks. There is no login/password authentication or database in scope. For the demo, the operator selects a predefined identity, which simulates an identity already established elsewhere. This is not secure identity verification.

The project will compare operations with no authorization layer (unsecured baseline) against the same operations routed through authorization (protected version), then report the difference in allowed/denied actions. The baseline must be labeled and kept separate from the protected interface.

## Files and team responsibilities

| File | Owner | Status / responsibilities |
|---|---|---|
| `Actions.java` | Actions developer | **Existing implementation included.** University operations, data validity checks, no role authorization. |
| `Data.java` | Data developer | **Existing sample data included.** Expand sample users/courses/enrollments/grades, define permission data as agreed. |
| `Authorization.java` | Authorization developer | **Template only.** Implement role permission and resource checks; expose safe entry points for protected operations. |
| `Main.java` | CLI developer | **Template only.** Choose predefined demo user; display menu; call authorized operations; show denials. |
| `Tests.java` | Testing developer | **Example action-only test harness included.** Expand isolated unit tests, plus integration/authorization experiments as separate methods or test class. |

## Demo authorization policy (proposed)

| Operation | Student | Professor | Dean |
|---|---|---|---|
| View courses | Courses enrolled in | Courses assigned to teach | All courses |
| View grades | Own grades only | Grades for assigned courses | All grades |
| Modify / create a grade | Not allowed | Only for students enrolled in a course they teach | For any student enrolled in the course |
| Create course | Not allowed | Not allowed | Allowed |
| Assign professor to course | Not allowed | Not allowed | Allowed |

Note: the ownership/course-assignment rules supplement RBAC0; they are not, by themselves, RBAC2 role constraints. Professor and dean grade-viewing behavior needs to be finalized at the interface level (e.g. single student vs. entire class).

## Current data layout (`Data.java`)

All maps are currently static and use IDs as strings:

```java
Map<String, String> users;                 // userId -> role
Map<String, String> courses;               // courseId -> courseName
Map<String, String> professors;            // courseId -> professorId
Map<String, List<String>> enrollments;     // studentId -> course IDs
Map<String, Map<String, Double>> grades;   // studentId -> (courseId -> grade)
```

Sample IDs: `S001`, `S002`, `P001`, `D001`, `CS2413`, `CS2063`. `Data.java` currently assigns `P001` to `CS2413` and `CS2063`, enrolls both students in `CS2413` and `S002` in `CS2063`, and gives both students a grade in `CS2413` and `S002` a grade in `CS2063`. The data developer should add multiple professors and courses so testing can include assigned vs. unassigned professor scenarios.

Because these maps are `public static`, any other Java class can mutate them directly. This is not a protected data boundary. The team must decide how to restrict unsafe direct access in its protected version, or explicitly document that the demonstration only protects requests routed through its authorization interface.

## Existing action methods (`Actions.java`)

| Signature | Behavior |
|---|---|
| `Map<String,String> getStudentCourses(String studentId)` | Returns enrolled course IDs and names |
| `Map<String,String> getProfessorCourses(String professorId)` | Returns assigned course IDs and names |
| `Map<String,String> getAllCourses()` | Returns copy of all courses |
| `Map<String,Double> getGrades(String studentId)` | Returns copy of student's recorded grades |
| `Double getGrades(String studentId, String courseId)` | Returns one grade or `null` if missing |
| `boolean modifyGrade(String studentId, String courseId, double newGrade)` | Updates or creates grade if student/course valid and student enrolled; validates grade 0–100 |
| `boolean createCourse(String courseId, String courseName)` | Creates new course; rejects missing/duplicate values |
| `boolean assignProfessor(String courseId, String professorId)` | Assigns existing professor to existing course; replaces existing assignment |

Action functions do not take the requester ID, except course-list functions that filter by the requested user. Authorization must know the acting user separately and check access before invoking raw actions.

## Integration contract

1. `Main.java` passes **acting user ID**, action name, and target IDs to the authorization API. It must not trust a typed role string as authority.
2. `Authorization.java` reads the actor's role from `Data.users` and checks permissions and student ownership/professor assignment as appropriate. Unknown actors/actions must be denied by default.
3. Only after authorization succeeds should protected operations run in `Actions.java`. Decide how to protect access to underlying mutable records from accidental bypasses.
4. `Actions.java` validates data rules (valid IDs, enrollment, numeric grade); it does not check the role of the requester.
5. All team members use the same IDs, map shapes, method signatures and return conventions. Tests must check the state as well as a success/failure boolean.

### Suggested protected API (agree as a team)

```java
// Example proposals; not yet implemented:
// Authorization.viewGrades(actorId, studentId, courseId)
// Authorization.modifyGrade(actorId, studentId, courseId, newGrade)
// Authorization.createCourse(actorId, courseId, courseName)
// Authorization.assignProfessor(actorId, courseId, professorId)
```

Returning `boolean` or typed result objects is preferable to having the action layer print directly; let the CLI format output.

## Compile and run (no external dependencies)

Place all `.java` files in this folder. From a terminal in the project folder:

```bash
javac *.java
java Tests
java Main
```

`Main.java` currently prints a placeholder message; the CLI has not yet been implemented. A Java 11+ JDK is recommended because the code uses `String.isBlank()`, `List.of()` and `Map.of()`.

## Testing plan

### Action-only tests (independent of RBAC)
- Student/professor/all-course lookup, unknown IDs and copy behavior.
- Get grades (all/specific/missing), update existing and create missing grade for enrolled student.
- Reject nonexistent student/course, non-enrolled course, out-of-range grades, duplicates, invalid professor assignments.
- Consider edge tests for `NaN` and infinity once grade validation is updated.

### Security experiments (require `Authorization.java`)
- Student reads own grades: allow; reads another student's grades: deny.
- Student modifies a grade: deny.
- Professor modifies grade in assigned course for enrolled student: allow.
- Professor modifies grade in unassigned course or for non-enrolled student: deny.
- Dean creates course, assigns professor, modifies enrolled student's grade: allow.
- Unauthorized attempts leave the data unchanged.
- Repeat equivalent requests in intentionally unsecured baseline and secured mode; capture before/after outcomes.

For reliable results, start each experiment from a known state. The `Data` maps currently use static mutable state, so tests need explicit setup/cleanup or a reset helper.

## Contribution and collaboration notes

- Each person owns their file but discusses API changes before changing public signatures.
- Open pull requests/commit your module; avoid editing another person's file without coordination.
- Keep unrelated authentication, password hashing and database features out of scope.
- Do not commit compiled `.class` files (covered by `.gitignore`).
- Document system assumptions and limitations in the report: identity is simulated; Java code with direct access to raw actions/public maps can bypass RBAC unless that boundary is hardened.