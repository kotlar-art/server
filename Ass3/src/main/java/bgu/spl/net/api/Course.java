package bgu.spl.net.api;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Course {

    final private int courseNumber;
    final private String courseName;
    final private Vector<Integer> KdamCourses;
    private Vector<User> registeredStudents;
    private ConcurrentHashMap<String, User> quickAccess;
    final private int maxStudents;
    private Object studentsLock;


    public Course(int courseNum, String name, Vector<Integer> kdam, int maxStudents){
        courseNumber = courseNum;
        courseName = name;
        KdamCourses = kdam;
        registeredStudents = new Vector<User>();
        quickAccess = new ConcurrentHashMap<String, User>();
        this.maxStudents = maxStudents;
        studentsLock = new Object();
    }

    public int getCourseNumber(){
        return courseNumber;
    }

    public String getCourseName(){
        return courseName;
    }

    public Vector<Integer> getKdamAsVector(){
        return KdamCourses;
    }
    public String getKdamCourses(){
        String kdam = "[";
        for (int i = 0; i<KdamCourses.size();i++){
            Integer nextKdam = KdamCourses.get(i);
            if (i==KdamCourses.size()-1){
                kdam = kdam + nextKdam;
            }
            else kdam = kdam + nextKdam + ", ";
        }
        return kdam + "]";
    }

    public String getStat(){
        String courseStat = "";
        String registeredNames = "[";
        synchronized (studentsLock) {
            int seatsAvailable = maxStudents - registeredStudents.size();
            courseStat = "Course: (" + courseNumber + ") " + courseName + "\n" +
                    "Seats Available: " + seatsAvailable + "/" + maxStudents + "\n";
            for (int i = 0; i < registeredStudents.size(); i++) {
                if (i == registeredStudents.size() - 1) registeredNames = registeredNames + registeredStudents.get(i);
                else registeredNames = registeredNames + registeredStudents.get(i) + ", ";
            }
        }
        return courseStat + "Students Registered: " + registeredNames + "]";
    }

    public boolean registerStudent(User subscriber){
        int i = 0;
        if (registeredStudents.size()<maxStudents) {
            synchronized (studentsLock) {
                if (registeredStudents.size()<maxStudents) {
                    while (i < registeredStudents.size() && registeredStudents.get(i).compareTo(subscriber) > 0) i++;
                    registeredStudents.add(i, subscriber);
                    quickAccess.putIfAbsent(subscriber.getUsername(), subscriber);
                    subscriber.registerToCourse(this);
                    return true;
                }
            }
        }
        return false;
    }

    public void unRegister(User sender) {
        synchronized (studentsLock){
            registeredStudents.remove(sender);
            quickAccess.remove(sender.getUsername());
            sender.unRegister(this);
        }
    }
}
