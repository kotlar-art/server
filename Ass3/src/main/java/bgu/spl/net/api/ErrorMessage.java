package bgu.spl.net.api;

public class ErrorMessage extends Message<Integer>{

    private Integer content;

    public ErrorMessage(int errorFor) {
        super(13);
        content = errorFor;
    }

    @Override
    Message actOnProtocol(Protocol p) {
        return null;
    }

    public byte[] actOnEncoder(){
        byte[] op = IntToBytes(opcode);
        byte[] err = IntToBytes(content);
        byte[] emp = new byte[0];
        return merge(op, err, emp);
    }

    @Override
    Integer getContent() {
        return content;
    }
}
