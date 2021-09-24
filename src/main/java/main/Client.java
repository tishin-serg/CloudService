package main;

import java.io.*;
import java.net.Socket;

public class Client {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8189);
        File file = new File("/Users/mac/IdeaProjects/CloudService/src/main/java/storage");

        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        byte[] bytes = new byte[10 * 1024];
        int x;
        while ((x = in.read(bytes)) != -1) {
            out.write(bytes, 0, x);
        }
    }
}
