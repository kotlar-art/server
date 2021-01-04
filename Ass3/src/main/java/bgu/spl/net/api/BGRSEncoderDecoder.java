package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

enum Command{
    UsernameBasedCommand,
    CourseNumberCommand,
    OpcodeCommand,
    invalidInputCommand
}

public class BGRSEncoderDecoder implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    private int end = -1;
    short opcode = 0;
    int zeroCounter = 0;
    Command command= Command.CourseNumberCommand;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if(toEnd()){
            return popMessage();
        }
        if(len == 2){
            opcode = bytesToShort(bytes, 0, 1);
            setDecoder(opcode);
        }
        if(command == Command.invalidInputCommand){
            return new IntegerMessage((short) 0,0);
        }
        pushByte(nextByte);
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        return message.actOnEncoder();
    }

    private Message popMessage() {
        if(command == Command.UsernameBasedCommand){
            byte[] b = Arrays.copyOfRange(bytes, 2,len);
            String content = new String(b, StandardCharsets.UTF_8);
            reset();
            String[] arr = content.split("\0", 2);
            return new StringsMessage(opcode, arr);
        }
        else if(command == Command.CourseNumberCommand){
            int content = bytesToShort(bytes, len-2, len-1);
            reset();
            return new IntegerMessage(opcode, content);
        }
        return new IntegerMessage(opcode, null);
    }

    private boolean toEnd() {
        if(command == Command.UsernameBasedCommand){
            return zeroCounter == end;
        }
        return len == end;
    }

    private void reset(){
        len = 0;
        zeroCounter = 0;
        end = -1;
    }


    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
        if(nextByte == '\0'){
            zeroCounter++;
        }
    }

    private void setDecoder(int opcode) {
        if(opcode < 1 || opcode > 11){
            command = Command.invalidInputCommand;
        }
        if(opcode >= 1 && opcode <=3){
            end = 2;
            command = Command.UsernameBasedCommand;
        }
        else if(opcode == 4 || opcode == 11){
            end = 2;
            command = Command.OpcodeCommand;
        }
        else if(opcode == 8){
            end = 1;
            command = Command.UsernameBasedCommand;
        }
        else{
            end = 4;
            command = Command.CourseNumberCommand;
        }
    }

    public short bytesToShort(byte[] byteArr, int index1, int index2)
    {
        short result = (short)((byteArr[index1] & 0xff) << 8);
        result += (short)(byteArr[index2] & 0xff);
        return result;
    }



}
