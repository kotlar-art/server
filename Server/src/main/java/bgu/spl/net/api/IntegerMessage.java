package bgu.spl.net.api;

import bgu.spl.net.srv.Database;

public class IntegerMessage extends Message<Integer>{

    final private Integer content;
    final private Database database;

    public IntegerMessage(short op, Integer i) {
        super(op);
        content = i;
        database = Database.getInstance();
    }

    @Override
    Message actOnProtocol(Protocol p) {
        User user = p.getUser();
        if (p.getUser()==null){
            return createError(opcode);
        }
        if (opcode==4){//log out
            return logOut(user);
        }
        if (opcode==5){
            return courseReg(user);
        }
        if (opcode==6){
            return kdamCheck();
        }
        if (opcode==7){
            return courseStat(user);
        }
        if (opcode==9){
            return isRegistered(user);
        }
        if (opcode==10){
            return unRegister(user);
        }
        if (opcode==11)
            return MyCourses(user);
        return createError(opcode);
    }

    private Message MyCourses(User user) {
        if (user.isAdmin()) return createError(opcode);
        String output = "";
        try {
            output = database.getCoursesOf(user.getUsername());
        }
        catch (IllegalAccessException i){
//            i.printStackTrace();
            return createError(opcode);
        }
        return createACK(opcode, output);
    }

    private Message unRegister(User sender) {
        try {
            database.unRegisterTo(sender.getUsername(), content);
        }
        catch (IllegalAccessException i){
//            i.printStackTrace();
            return createError(opcode);
        }
        return createACK(opcode, "");
    }

    private Message logOut(User user) {

        try {
            database.logOut(user.getUsername());
        }
        catch (IllegalAccessException e){
//            e.printStackTrace();
            return createError(opcode);
        }
        return createACK(opcode, "");
    }


    private Message courseReg(User sender) {
        if (!sender.isAdmin()){
            try {
                database.courseRegister(sender.getUsername(), content);
            }
            catch (IllegalAccessException i){
//                i.printStackTrace();
                return createError(opcode);
            }
            return createACK(opcode, "");
        }
        return createError(opcode);
    }

    private Message kdamCheck() {
        String kdam = "";
        try {
            kdam = database.kdamCoursesOf(content);
        }
        catch (IllegalAccessException i){
//            i.printStackTrace();
            return createError(opcode);
        }
        return createACK(opcode, kdam);
    }

    private Message courseStat(User sender) {

        if (sender.isAdmin()){
            String output = "";
            try {
                output = database.getCourseStat(content);
            }
            catch (IllegalAccessException i){
//                i.printStackTrace();
                return createError(opcode);
            }
            return createACK(opcode, output);
        }
        return createError(opcode);
    }

    private Message isRegistered(User sender) {
        if (!sender.isAdmin()){
            String isRegistered = "";
            try {
                isRegistered = database.isRegistered(sender.getUsername(), content);
            }
            catch (IllegalAccessException i){
//                i.printStackTrace();
                return createError(opcode);
            }
            return createACK(opcode, isRegistered);
        }
        return createError(opcode);
    }


    @Override
    byte[] actOnEncoder() {
        return null;
    }

    @Override
    public Integer getContent() {
        return content;
    }
}
