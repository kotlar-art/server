package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.BGRSEncoderDecoder;
import bgu.spl.net.api.Protocol;
import bgu.spl.net.srv.Reactor;

public class ReactorMain {

    public static void main(String[] args){
        int port = Integer.decode(args[0]).intValue();
        int numOfThreads = Integer.decode(args[1]).intValue();
        Reactor reactor = new Reactor(numOfThreads, port, ()->new Protocol(), ()->new BGRSEncoderDecoder());
        reactor.serve();
    }
}
