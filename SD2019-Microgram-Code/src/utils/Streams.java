package utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Streams {

    public static byte[] getBytesFromInpuStream(InputStream in) throws IOException {
        DataInputStream dataInput = new DataInputStream(in);
        List<Byte> bufferInList = new LinkedList<>();
        try {
            while(true) {
                bufferInList.add(dataInput.readByte());
            }
        }catch (IOException e){
            return byteListToByteArray(bufferInList);
        }
    }

    private static byte[] byteListToByteArray(List<Byte> list){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }
}
