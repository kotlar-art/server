package bgu.spl.net.api;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Database;

public class Protocol implements MessagingProtocol<Message>{


    private Database database;
    private ConnectionHandler handler;
    private User user;

    public Protocol(ConnectionHandler handler){
        database = Database.getInstance();
        this.handler = handler;
        user = null;
    }

    @Override
    public Message process(Message msg) {
        return msg.actOnProtocol(this);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        if (user==null) this.user = user;
    }
}