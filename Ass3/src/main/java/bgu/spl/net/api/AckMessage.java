package bgu.spl.net.api;

public class AckMessage extends Message<Integer>{

    private short content;
    private String StringForClient;

    public AckMessage(short ackWhat, String messageForClient) {
        super((short) 12);
        content = ackWhat;
        StringForClient = messageForClient;
        if (StringForClient.length()>0) StringForClient = StringForClient + '\0';
    }

    public byte[] actOnEncoder(){
        byte[] op = shortToBytes((short) 12);
        byte[] con = shortToBytes(content);
        byte[] message = StringForClient.getBytes();
        return merge(op, con, message);
    }

    @Override
    Message actOnProtocol(Protocol p) {
        return null;
    }

    @Override
    Integer getContent() {
        return (int)content;
    }



}

