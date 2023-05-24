package com.codegym.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("New connection was established with a remote " +
                    "address " + socket.getRemoteSocketAddress().toString());

            Connection connection = null;
            String name = "";
            try {
                connection = new Connection(socket);
                name = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, name));
                notifyUsers(connection, name);
                serverMainLoop(connection, name);
                connectionMap.remove(name);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, name));
                ConsoleHelper.writeMessage("Connection with the remote address is closed");
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error occurred while communicating with the remote address"
                        + socket.getRemoteSocketAddress().toString());
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            Message response = null;
            boolean isValid = false;
            first:
            while (!isValid) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                response = connection.receive();
                if (response.getType() != MessageType.USER_NAME)
                    continue;
                if (response.getData() == null || response.getData().equals(""))
                    continue;
                for (String name : connectionMap.keySet()) {
                    if (name.equals(response.getData())) {
                        ConsoleHelper.writeMessage("Some user use this name, try again");
                        continue first;
                    }
                }
                isValid = true;
                connectionMap.put(response.getData(), connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
            }
            return response.getData();
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if (!entry.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, entry.getKey()));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message received = connection.receive();
                if (received.getType() == MessageType.TEXT) {
                    String text = userName + ": " + received.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, text));
                } else {
                    ConsoleHelper.writeMessage("Something gone wrong: it's not text message");
                }
            }
        }
    }

    public static void sendBroadcastMessage(Message message) {
        try {
            for (Connection connection : connectionMap.values()) {
                connection.send(message);
            }
        } catch (IOException e) {
            System.out.println("Message couldn't be sent: " + e);
        }
    }

    public static void main(String[] args) throws IOException {
        int serverPort = ConsoleHelper.readInt();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("The server is running");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Handler(clientSocket);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Problem has occur: " + e);
        } finally {
            serverSocket.close();
        }
    }


}
