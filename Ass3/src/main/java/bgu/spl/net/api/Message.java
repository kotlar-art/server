package bgu.spl.net.api;

import bgu.spl.net.srv.Database;

import java.io.Serializable;

public abstract class Message<T> implements Serializable {

    final protected int opcode;

    public Message(int op){
        opcode = op;

    }

    public int getOpcode(){
        return opcode;
    }

    protected byte[] merge(byte[] array1, byte[] array2, byte[] array3){

        int aLen = array1.length;
        int bLen = array2.length;
        int cLen = array3.length;
        byte[] result = new byte[aLen + bLen + cLen];

        System.arraycopy(array1, 0, result, 0, aLen);
        System.arraycopy(array2, 0, result, aLen, bLen);
        System.arraycopy(array3, 0, result, aLen +bLen, cLen);

        return result;
    }

    protected byte[] IntToBytes(Integer num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    abstract Message actOnProtocol(Protocol p);

    abstract byte[] actOnEncoder();

    abstract T getContent();

    protected Message createError(int errorFor){
        return new ErrorMessage(errorFor);
    }

    protected Message createACK(int ackWhat, String messageForClient){
        return new AckMessage(ackWhat, messageForClient);
    }
}
