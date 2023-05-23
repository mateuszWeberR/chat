package com.codegym.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String string = "";
        boolean again = true;
        while (again) {
            try {
                string = reader.readLine();
                again = false;
            } catch (IOException e) {
                System.out.println("An error occurred while trying to enter text. Try again.");
            }
        }
        return string;
    }

    public static int readInt() {
        int number = 0;
        boolean again = true;
        while (again) {
            try {
                number = Integer.parseInt(readString());
                again = false;
            } catch (NumberFormatException e) {
                System.out.println("An error while trying to enter a number. Try again.");
            }
        }
        return number;
    }

}
