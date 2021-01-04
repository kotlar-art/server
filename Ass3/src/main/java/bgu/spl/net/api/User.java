package bgu.spl.net.api;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class User implements Comparable<User>{
     final private String username;
     final private String lowerCaseUsername;
     final private String password;
     private Vector<Course> registeredCourses;
     private ConcurrentHashMap<Integer, Course> quickAccess;
     final boolean isAdmin;
     private boolean loggedIn;
     private Object courseslock;

     public User(String username, String password, boolean isAdmin){
         this.username = username;
         this.lowerCaseUsername = username.toLowerCase();
         this.password = password;
         this.registeredCourses = new Vector<Course>();
         this.quickAccess = new ConcurrentHashMap<Integer, Course>();
         this.isAdmin = isAdmin;
         loggedIn = false;
         courseslock = new Object();

     }

    public String getUsername() {
        return username;
    }

    public String getLowerCaseUsername(){
         return lowerCaseUsername;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void logIn(){
         loggedIn = true;
    }

    public void logOut(){
         loggedIn = false;
    }

    public String getStat(){
        String output = "Student: " + username + "\n";
        String courses = "[";
        synchronized (courseslock){
            for (int i = 0; i<registeredCourses.size();i++){
                if (i==registeredCourses.size()-1) courses = courses + registeredCourses.get(i).getCourseNumber();
                else courses = courses + registeredCourses.get(i).getCourseNumber() + ",";
            }
        }
        return output + "Courses: " + courses + "]";
    }

    public void registerToCourse(Course course){
         int i = 0;
         synchronized (courseslock) {
             while (i < registeredCourses.size() && course.getCourseNumber() > registeredCourses.elementAt(i).getCourseNumber())
                 i++;
             registeredCourses.add(i, course);
             quickAccess.putIfAbsent(course.getCourseNumber(), course);
         }

    }

    @Override
    public int compareTo(User o) {
        return this.lowerCaseUsername.compareTo(o.getLowerCaseUsername());
    }

    public boolean isRegisteredTo(Integer courseNumber){
         synchronized (courseslock) {
             return quickAccess.containsKey(courseNumber);
         }
    }

    public void printCourses(){
         String s = username + " is registered to: ";
         for (int i = 0; i<registeredCourses.size(); i++){
             s = s + registeredCourses.get(i).getCourseName() + ", ";
         }
        System.out.println(s);
    }

    public boolean isRegisteredtTo(Vector<Integer> kdam) {
         synchronized (courseslock){
             for (int i = 0; i<kdam.size();i++){
                 if (!quickAccess.containsKey(kdam.get(i))) return false;
             }
             return true;
         }
    }

    public void unRegister(Course course) {
         synchronized (courseslock){
             registeredCourses.remove(course);
             quickAccess.remove(course.getCourseNumber());
         }
    }

    public String getCoursesAsString() {
        String courses = "[";
        synchronized (courseslock){
            for (int i = 0; i<registeredCourses.size();i++){
                if (i==registeredCourses.size()-1) courses = courses + registeredCourses.get(i).getCourseNumber();
                else courses = courses + registeredCourses.get(i).getCourseNumber() + ",";
            }
            return courses + "]";
        }
    }
}
