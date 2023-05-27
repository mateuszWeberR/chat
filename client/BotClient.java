package com.codegym.task.task30.task3008.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BotClient extends Client {
    private List<String> jokes = jokesGenerator();

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Hello, there. I'm a bot. I understand the following commands:" +
                    " date, day, month, year, time, hour, minutes, seconds.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            super.processIncomingMessage(message);

            if (message.contains(": ")) {
                String[] separated = message.split(": ");
                Date date = Calendar.getInstance().getTime();
                if (separated[1].equals("date")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("d.MM.YYYY").format(date));
                } else if (separated[1].equals("time")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("H:mm:ss").format(date));
                } else if (separated[1].equals("day")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("d").format(date));
                } else if (separated[1].equals("month")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("MMMM").format(date));
                } else if (separated[1].equals("year")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("YYYY").format(date));
                } else if (separated[1].equals("hour")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("H").format(date));
                } else if (separated[1].equals("minutes")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("m").format(date));
                } else if (separated[1].equals("seconds")) {
                    sendTextMessage("Information for " + separated[0] + ": " +
                            new SimpleDateFormat("s").format(date));
                } else if (separated[1].equals("joke")) {
                    sendTextMessage(getRandomJoke(jokes));
                }
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        StringBuilder sb = new StringBuilder("date_bot_");
        sb.append((int) (Math.random() * 100));
        return sb.toString();
    }

    public static void main(String[] args) {
        new BotClient().run();
    }

    public List<String> jokesGenerator() {
        List<String> list = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader
                    ("/home/mati/IdeaProjects/GymTasks/CodeGymTasks/3.JavaMultithreading/src/com/codegym/task/task30/task3008/files/jokes.txt"));
            while (reader.ready()) {
                list.add(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public String getRandomJoke(List<String> jokes) {
        int bound = jokes.size();
        int number = new Random().nextInt(bound);
        return jokes.get(number);
    }
}
