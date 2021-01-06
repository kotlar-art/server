package bgu.spl.net.api;

import bgu.spl.net.srv.Database;

public class StringsMessage extends Message<String[]>{

    final private String[] content;
    private Database database;

    public StringsMessage(short op, String[] content) {
        super(op);
        this.content = content;
        database = Database.getInstance();
    }

    @Override
    public Message actOnProtocol(Protocol p) {
        User client = p.getUser();
        if (client==null){
            if (opcode==1){//register Admin
                return adminReg();
            }
            if (opcode==2){//register Student
                return studentReg();
            }
            if (opcode==3){//log in
                return logIn(p);
            }
            return createError(opcode);
        }
        if (opcode==8){
            return studentStat(client);
        }
        return createError(opcode);
    }



    @Override
    byte[] actOnEncoder() {
        return null;
    }

    private Message adminReg(){
        User newAdmin;
        try{
            newAdmin = database.registerAdmin(content[0], content[1]);
        }
        catch (IllegalAccessException i){
            i.printStackTrace();
            return createError(opcode);
        }
        if (newAdmin!=null) {
            return createACK(opcode, "");
        }
        return createError(opcode);
    }

    private Message studentReg(){
        User newStudent = null;
        try {
            newStudent = database.registerStudent(content[0], content[1]);
        }
        catch (IllegalAccessException i){
            createError(opcode);
        }
        if (newStudent!=null) {
            return createACK(opcode, "");
        }
        return createError(opcode);
    }

    private Message logIn(Protocol p){
        User newUser;
        try{
            newUser = database.logIn(content[0], content[1]);
            p.setUser(newUser);
        }
        catch (IllegalAccessException i){
            i.printStackTrace();
            return createError(opcode);
        }
        p.setUser(newUser);
        return createACK(opcode, "");

    }

    private Message studentStat(User user){
        if (user.isAdmin){
            String output = "";
            try {
                output = database.getStudentStat(content[0]);
            }
            catch (IllegalAccessException i){
                i.printStackTrace();
                return createError(opcode);
            }
            return createACK(opcode, output);
        }
        return createError(opcode);
    }

    public String[] getContent(){
        return content;
    }
}
