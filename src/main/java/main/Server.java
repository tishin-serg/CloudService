package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        File file = new File("/Users/mac/IdeaProjects/CloudService/destination/1.txt");
        ServerSocket serverSocket = new ServerSocket(8189);
        Socket socket = serverSocket.accept();
        InputStream in = socket.getInputStream();
        OutputStream out = new FileOutputStream(file);
        byte[] bytes = new byte[10 * 1024];
        int x;
        while((x = in.read(bytes)) != -1) {
            out.write(bytes, 0, x);
        }
        out.flush();
        in.close();
        socket.close();
    }
}
