package bgu.spl.net.api;

public class ErrorMessage extends Message<Integer>{

    private short content;

    public ErrorMessage(short errorFor) {
        super((short)13);
        content = errorFor;
    }

    @Override
    Message actOnProtocol(Protocol p) {
        return null;
    }

    public byte[] actOnEncoder(){
        byte[] op = shortToBytes((short)13);
        byte[] err = shortToBytes(content);
        byte[] emp = new byte[0];
        return merge(op, err, emp);
    }

    @Override
    Integer getContent() {
        return (int)content;
    }
}
