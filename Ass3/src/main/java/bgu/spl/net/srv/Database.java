package bgu.spl.net.srv;

import bgu.spl.net.api.Course;
import bgu.spl.net.api.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private ConcurrentHashMap<String, User> users;
    private ConcurrentHashMap<Integer, Course> courses;
    private Object usersLock;



    //to prevent user from creating new Database
    private static class SingletonHolder {
        private static Database instance = new Database();

        private static void reset() {
            instance = new Database();
        }
    }


    public static Database getInstance() {
        return SingletonHolder.instance;
    }

    public static void reset() {
        SingletonHolder.reset();
    }

    private Database()  {
        courses = new ConcurrentHashMap<Integer, Course>();
        users = new ConcurrentHashMap<String, User>();
        usersLock = new Object();
        initialize("CoursesAss3");
    }
    boolean initialize(String coursesFilePath) {

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(coursesFilePath));
            String line = reader.readLine();
            int order = 1;
            while (line != null) {
                System.out.println("the whole line of the course is " + line);
                int i = 0;
                String courseStringNumber = "";
                while (line.charAt(i)!='|'){
                    char next = line.charAt(i);
                    courseStringNumber = courseStringNumber + next;
                    i++;
                }
                int courseNumber = Integer.parseInt(courseStringNumber);
                System.out.println("course number is " + courseNumber);
                i++;
                String courseName = "";
                while (line.charAt(i)!='|'){
                    char next = line.charAt(i);
                    courseName = courseName + next;
                    i++;
                }
                System.out.println("course name is " + courseName);
                i++;
                Vector<Integer> kdam = new Vector<>();
                String nextKdamCourseNumber = "";
                while (line.charAt(i)!='|'){
                    char next = line.charAt(i);
                    if ((next==',' || next==']')&&!nextKdamCourseNumber.equals("")) {
                        int kdamCourseNumber = Integer.parseInt(nextKdamCourseNumber);
                        kdam.add(kdamCourseNumber);
                        nextKdamCourseNumber = "";
                    }
                    else {
                        if (next != '[') nextKdamCourseNumber = nextKdamCourseNumber + next;
                    }
                    i++;
                }
                System.out.println("kdam courses are " + kdam.toString());
                i++;
                String maxStudentsString = "";
                while (i<line.length()){
                    char next = line.charAt(i);
                    maxStudentsString = maxStudentsString + next;
                    i++;
                }
                int maxStudents = Integer.parseInt(maxStudentsString);
                System.out.println("max students is " + maxStudents);

                Course newCourse = new Course(order, courseNumber, courseName, kdam, maxStudents);
                courses.putIfAbsent(courseNumber, newCourse);
                order++;
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public User registerAdmin(String username, String password) {
        if (!users.containsKey(username)){
            synchronized (usersLock){
                if (!users.containsKey(username)) {
                    User newAdmin = new User(username, password, true);
                    users.put(username, newAdmin);
                    return newAdmin;
                }
            }
        }
        return null;
    }

    public User registerStudent(String username, String password) {
        if (!users.containsKey(username)){
            synchronized (usersLock){
                if (!users.containsKey(username)) {
                    User newStudent = new User(username, password, false);
                    users.put(username, newStudent);
                    return newStudent;
                }
            }
        }
        return null;
    }

    public void logIn(String username, String password) throws IllegalAccessException, IllegalArgumentException{
        User logAttempt = users.get(username);
        if (logAttempt!=null){
            if (!logAttempt.isLoggedIn()){
                if (logAttempt.getPassword().equals(password)) {
                    logAttempt.logIn();
                    return;
                }
                throw new IllegalAccessException("password incorrect");
            }
            throw new IllegalAccessException("user is already logged in");
        }
        throw new IllegalArgumentException("no such username in the system");
    }

    public void logOut(String username) throws IllegalAccessException, IllegalArgumentException{
        User logOutRequester = users.get(username);
        if (logOutRequester!=null){
            if (logOutRequester.isLoggedIn()){
                logOutRequester.logOut();
                return;
            }
            throw new IllegalAccessException("user is already logged out");
        }
        throw new IllegalArgumentException("no such username in the system");
    }

    public void courseRegister(String username, Integer courseNumber) throws IllegalAccessException {
        User regRequester = users.get(username);
        Course registerTo = courses.get(courseNumber);
        if (regRequester != null && !regRequester.isAdmin()) {
            if (registerTo != null) {
                if (regRequester.isLoggedIn()) {
                    if (isQualified(regRequester, registerTo)) {
                        if (!regRequester.isRegisteredTo(courseNumber)){
                            if (registerTo.registerStudent(regRequester)) return;
                            throw new IllegalAccessException("course is full");
                        }
                        throw new IllegalAccessException("already registered");
                    }
                    throw new IllegalAccessException(regRequester.getUsername() + " is not qualified to register to the course " + registerTo.getCourseName());
                }
                throw new IllegalAccessException(username + " is not logged in");
            }
            throw new IllegalAccessException("the course " + registerTo.getCourseName() + " doesn't exist");
        }
        throw new IllegalAccessException("the username " + regRequester.getUsername() + " does not exist");
    }
    private boolean isQualified(User qualifier, Course qualifiedFor) {
        Vector<Integer> kdam = qualifiedFor.getKdamAsVector();
        return qualifier.isRegisteredtTo(kdam);
    }

    public String kdamCoursesOf(Integer courseNumber) throws IllegalAccessException{
        Course course = courses.get(courseNumber);
        if (course!=null){
            return course.getStat();
        }
        throw new IllegalAccessException("no such course");
    }

    public String getCourseStat(Integer courseNumber) throws IllegalAccessException{
        Course query = courses.get(courseNumber);
        if (query!=null){
            return query.getStat();
        }
        throw new IllegalAccessException("no such course");
    }

    public String getStudentStat(String username) throws IllegalAccessException {
        User query = users.get(username);
        if (query!=null){
            return query.getStat();
        }
        throw new IllegalAccessException("no such student");
    }

    public String isRegistered(String username, Integer content) throws IllegalAccessException {
        User sender = users.get(username);
        Course query = courses.get(content);
        if (sender!=null){
            if (query!=null){
                if (sender.isLoggedIn()){
                    if (sender.isRegisteredTo(content)) return "REGISTERED";
                    return "NOT REGISTERED";
                }
                throw new IllegalAccessException(username + " is not logged in");
            }
            throw new IllegalAccessException("no such course");
        }
        throw new IllegalAccessException("no such student");
    }

    public void unRegisterTo(String username, Integer courseNumber) throws IllegalAccessException {
        User sender = users.get(username);
        Course courseToQuit = courses.get(courseNumber);
        if (sender!=null){
            if (courseToQuit!=null){
                if (sender.isLoggedIn()){
                    if (sender.isRegisteredTo(courseNumber)){
                        courseToQuit.unRegister(sender);
                    }
                    throw new IllegalAccessException("already unregistered");
                }
                throw new IllegalAccessException(username + " is not logged in");
            }
            throw new IllegalAccessException("no such course");
        }
        throw new IllegalAccessException("no such student");
    }
    public String getCoursesOf(String username) throws IllegalAccessException {
        User requester = users.get(username);
        if (requester!=null){
            if (requester.isLoggedIn()){
                return requester.getCoursesAsString();
            }
            throw new IllegalAccessException(username + " is not logged in");
        }
        throw new IllegalAccessException("no such student");
    }


    public static void main(String[] args){

        Database database = Database.getInstance();
        String s = "";
        try {
            s = database.getCourseStat(400);
        }
        catch (IllegalAccessException i ){};
        System.out.println(s);
	}

}
