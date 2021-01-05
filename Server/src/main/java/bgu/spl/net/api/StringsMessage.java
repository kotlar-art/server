package bgu.spl.net.api;

import bgu.spl.net.srv.Database;

public class StringsMessage extends Message<String[]>{

    final private String[] content;
    private Database database;

    public StringsMessage(short op, String[] content) {
        super(op);
        System.out.println("string messsage created for " + opcode);
        this.content = content;
        database = Database.getInstance();
    }

    @Override
    public Message actOnProtocol(Protocol p) {
        User client = p.getUser();
        if (client==null){
            if (opcode==1){//register Admin
                System.out.println("entered register admin and opcode is " + opcode);
                return opcode1(p);
            }
            if (opcode==2){//register Student
                return opcode2(p);
            }
            return createError(opcode);
        }
        if (opcode==3){//log in
            return opcode3();
        }
        if (opcode==4){//log out
            return opcode4(client);
        }
        if (opcode==8){
            return opcode8(client);
        }
        return null;
    }



    @Override
    byte[] actOnEncoder() {
        return null;
    }

    private Message opcode1(Protocol p){
        User newAdmin = database.registerAdmin(content[0], content[1]);
        if (newAdmin!=null) {
            p.setUser(newAdmin);
            System.out.println("username is " + content[0] + " password is " + content[1]);
            return createACK(opcode, "");
        }
        return createError(opcode);
    }

    private Message opcode2(Protocol p){

        User newStudent = database.registerStudent(content[0], content[1]);
        if (newStudent!=null) {
            p.setUser(newStudent);
            return createACK(opcode, "");
        }
        return createError(opcode);
    }

    private Message opcode3(){

        try{
            database.logIn(content[0], content[1]);
        }
        catch (IllegalAccessException i){
            i.printStackTrace();
            return createError(opcode);
        }
        catch (IllegalArgumentException i){
            i.printStackTrace();
            return createError(opcode);
        }
        return createACK(opcode, "");

    }
    private Message opcode4(User user) {

        try {
            database.logOut(user.getUsername());
        }
        catch (IllegalArgumentException i){
            i.printStackTrace();
            return createError(opcode);
        }
        catch (IllegalAccessException e){
            e.printStackTrace();
            return createError(opcode);
        }
        return createACK(opcode, "");

    }

    private Message opcode8(User user){
        if (user.isAdmin){
            String output = "";
            try {
                output = database.getStudentStat(user.getUsername());
            }
            catch (IllegalAccessException i){
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
