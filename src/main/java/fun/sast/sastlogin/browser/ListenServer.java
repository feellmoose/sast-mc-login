package fun.sast.sastlogin.browser;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.BiConsumer;

public class ListenServer {

    private final BiConsumer<BufferedReader, BufferedWriter> callback;
    private boolean open;

    public ListenServer(BiConsumer<BufferedReader, BufferedWriter> callback) {
        this.callback = callback;
    }

    public static ListenServer create(BiConsumer<BufferedReader, BufferedWriter> callback) {
        return new ListenServer(callback);
    }

    public ListenServer openServer(int port){
        this.open = true;
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("sast-link client/server start listening port: " + port);
                int i = 1;
                while (this.open) {
                    System.out.println(i++);
                    try (Socket clientSocket = serverSocket.accept();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
                        callback.accept(reader, writer);
                        writer.flush();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "sast-link_listener").start();
        return this;
    }

    public void close() {
        this.open = false;
    }

}
