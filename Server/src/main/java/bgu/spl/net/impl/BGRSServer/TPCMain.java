package bgu.spl.net.impl.BGRSServer;
import bgu.spl.net.api.BGRSEncoderDecoder;
import bgu.spl.net.api.Protocol;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;

public class TPCMain {

    public static void main(String args[]) {
        System.out.println("creating TPC");
        int port = Integer.decode(args[0]).intValue();
        BaseServer b = new BaseServer(port, ()->new Protocol(), ()->new BGRSEncoderDecoder()) {
            @Override
            protected void execute(BlockingConnectionHandler handler) {
                Thread newThread = new Thread(handler);
                newThread.start();
            }
        };
        b.serve();
    }
}
