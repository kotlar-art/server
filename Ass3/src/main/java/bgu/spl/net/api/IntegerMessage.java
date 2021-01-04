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
        if (p.getUser()==null) return createError(opcode);
        if (opcode==5){
            return opcode5(user);
        }
        if (opcode==6){
            return opcode6();
        }
        if (opcode==7){
            return opcode7(user);
        }
        if (opcode==9){
            return opcode9(user);
        }
        if (opcode==10){
            return opcode10(user);
        }
        if (opcode==11)
            return MyCourses(user);
        return null;
    }

    private Message MyCourses(User user) {
        if (user.isAdmin()) createError(opcode);
        String output = "";
        try {
            output = database.getCoursesOf(user.getUsername());
        }
        catch (IllegalAccessException i){
            return createError(opcode);
        }
        return createACK(opcode, output);
    }

    private Message opcode10(User sender) {
        try {
            database.unRegisterTo(sender.getUsername(), content);
        }
        catch (IllegalAccessException i){
            return createError(opcode);
        }
        return createACK(opcode, "");
    }


    private Message opcode5(User sender) {
        if (!sender.isAdmin()){
            try {
                database.courseRegister(sender.getUsername(), content);
            }
            catch (IllegalAccessException i){
                i.printStackTrace();
                return createError(opcode);
            }
            return createACK(opcode, "");
        }
        return createError(opcode);
    }

    private Message opcode6() {
        String kdam = "";
        try {
            kdam = database.kdamCoursesOf(content);
        }
        catch (IllegalAccessException i){
            return createError(opcode);
        }
        return createACK(opcode, kdam);
    }

    private Message opcode7(User sender) {

        if (sender.isAdmin()){
            String output = "";
            try {
                output = database.getCourseStat(content);
            }
            catch (IllegalAccessException i){
                createError(opcode);
            }
            return createACK(opcode, output);
        }
        return createError(opcode);
    }

    private Message opcode9(User sender) {
        if (!sender.isAdmin()){
            String isRegistered = "";
            try {
                isRegistered = database.isRegistered(sender.getUsername(), content);
            }
            catch (IllegalAccessException i){
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
