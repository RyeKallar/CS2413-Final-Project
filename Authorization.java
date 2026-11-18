import java.util.*;

public class Authorization {
    private Authorization() { }

    //Check wether a role has permission to perform an action 
    public static boolean hasPermission(String actorId, String actionName) {
        //unkown User or action
        if(actorId == null || actionName == null){
            return false;
        }

        String role = Data.users.get(actorId);

        if(role == null){
            return false;
        }

        role = role.toLowerCase(Locale.ROOT);
        actionName = actionName.toLowerCase(Locale.ROOT);

        switch(actionName){

            case "view_course":
            case "view_grades":
                return role.equals("student") || role.equals("professor") || role.equals("dean");

            case "modify_grade":
                return role.equals("professor") || role.equals("dean");

            case "create_course":
            case "assign_professor":
                return role.equals("dean");

            default:
                return false;
        }
    }

    //Check wether a student is enrolled in a course
    private static boolean isEnrolled(String studentId, String courseId){

        return Data.enrollments.getOrDefault(studentId, Collections.emptyList()).contains(courseId);
    }

    //Check wether a proff is assigned to a course
    private static boolean teachesCourse(String professorId, String courseId){

        return professorId.equals(Data.professors.get(courseId));
    }

    //View Courses
    public static Map<String, String> viewCourses(String actorId){

        if (!hasPermission(actorId, "view_courses")){
            return null;
        }

        String role = Data.users.get(actorId);

        if(role.equals("student")){
            return Actions.getProfessorCourses(actorId);        }

        if(role.equals("professor")){
            return Actions.getAllCourses();
        }

        if(role.equals("dean")){
            return Actions.getAllCourses();
        }

        return null;
    }

    //View all grades for a student
    public static Map<String, Double> viewGrades(String actorId, String studentId){

        if(!hasPermission(actorId, "view_grades")){
            return null;
        }

        if(!"student".equals(Data.users.get(studentId))){
            return null;
        }

        String role = Data.users.get(actorId);

        if(role.equals("student") && !actorId.equals(studentId)){
            return null;
        }

        Map<String, Double> result = new HashMap<>();

        Map<String, Double> grades = Actions.getGrades(studentId);

        for(String courseId: grades.keySet()){

            if(!isEnrolled(studentId, courseId)){
                continue;
            }            

            if(role.equals("professor") && !teachesCourse(actorId,courseId)){
                continue;
            }

            result.put(courseId, grades.get(courseId));
        }
        return result;
    }

    //View a specific grade
    public static Double viewGrade(String actorId, String studentId, String courseId){
        if(!"student". equals(Data.users.get(studentId))){
            return null;
        }

        String role = Data.users.get(actorId);

        if(!isEnrolled(studentId, courseId)){
            return null;
        }

        if(role.equals("student") && !actorId.equals(studentId)){
            return null;
        }

        if(role.equals("professor") && !teachesCourse(actorId, courseId)){
            return null;
        }

        return Actions.getGrades(studentId, courseId);
    }

    //Modify a grade
    public static boolean modifyGrade(String actorId, String studentId, String courseId, double newGrade){

        if(!hasPermission(actorId, "modify_grade")){
            return false;
        }

        if(!"student".equals(Data.users.get(studentId))){
            return false;
        }

        String role = Data.users.get(actorId);

        if(role.equals("professor") && !teachesCourse(actorId, courseId)){
            return false;
        }

        if(!isEnrolled(studentId, courseId)){
            return false;
        }

        return Actions.modifyGrade(studentId, courseId, newGrade);
    }

    //Create a course
    public static boolean createCourse( String actorId, String courseId, String courseName){

        if(!hasPermission(actorId, "create_course")){
            return false;
        }

        return Actions.createCourse(courseId, courseName);
    }

    //Assign a professor to a course
    public static boolean assignProfessor( String actorId, String courseId, String professorId){

        if(!hasPermission(actorId, "assign_professor")){
            return false;
        }

        return Actions.assignProfessor(courseId, professorId);
    }


}