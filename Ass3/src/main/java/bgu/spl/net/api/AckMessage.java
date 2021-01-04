package bgu.spl.net.api;

public class AckMessage extends Message<Integer>{

    private Integer content;
    private String StringForClient;

    public AckMessage(int ackWhat) {
        super(12);
        content = ackWhat;
        StringForClient = null;
    }

    public AckMessage(int ackWhat, String messageForClient) {
        super(12);
        content = ackWhat;
        StringForClient = messageForClient;
    }

    public byte[] actOnEncoder(){
        byte[] op = IntToBytes(opcode);
        byte[] con = IntToBytes(content);
        byte[] message = StringForClient.getBytes();
        return merge(op, con, message);
    }

    @Override
    Message actOnProtocol(Protocol p) {
        return null;
    }

    @Override
    Integer getContent() {
        return content;
    }



}

