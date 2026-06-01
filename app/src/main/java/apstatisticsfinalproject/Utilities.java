package apstatisticsfinalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Utilities {
    public static int readInteger(BufferedReader screenReader, PrintStream screenWriter, String prompt,
            int defaultValue) throws IOException {
        String updatedPromptString = prompt + " (default: " + defaultValue + "): ";
        String input = readString(screenReader, screenWriter, updatedPromptString);
        if (input.isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(input);
    }

    public static double readDouble(BufferedReader screenReader, PrintStream screenWriter, String prompt,
            double defaultValue) throws IOException {
        String updatedPromptString = prompt + " (default: " + defaultValue + "): ";
        String input = readString(screenReader, screenWriter, updatedPromptString);
        if (input.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(input);
    }

    public static boolean readBoolean(BufferedReader screenReader, PrintStream screenWriter, String prompt,
            boolean defaultValue) throws IOException {
        String updatedPromptString = prompt + " (default: " + defaultValue + "): ";
        String input = readString(screenReader, screenWriter, updatedPromptString);
        if (input.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(input);
    }

    public static String readString(BufferedReader screenReader, PrintStream screenWriter, String prompt)
            throws IOException {
        screenWriter.print(prompt);
        String variable1 = screenReader.readLine();
        return variable1;
    }

    public static int[][] read2DIntegerArray(BufferedReader screenReader, PrintStream screenWriter, int rows, int cols)
            throws IOException {
        int[][] array = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            screenWriter.print("Enter values for row " + (r + 1) + " (comma-separated values): ");
            String[] values = screenReader.readLine().split(",");
            for (int c = 0; c < cols; c++) {
                array[r][c] = Integer.parseInt(values[c]);
            }
        }
        return array;
    }

    public static String arrayToString(int[][] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("     ");
        for (int c = 0; c < array[0].length; c++)
            sb.append(String.format("%6d", c));
        sb.append("\n");
        sb.append("     ");
        for (int c = 0; c < array[0].length; c++)
            sb.append(String.format("------"));
        sb.append("\n");
        

        for (int r = 0; r < array.length; r++) {
            sb.append(String.format("%3d |", r));
            for (int c = 0; c < array[r].length; c++) {
                sb.append(String.format("%6d", array[r][c]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void writeMessage(String msg, PrintStream screenWriter, PrintWriter fileWriter) {
        screenWriter.println(msg);
        fileWriter.println(msg);
        fileWriter.println();
    }

    public static void writeLastMessage(String msg, PrintStream screenWriter, PrintWriter fileWriter) {
        screenWriter.println(msg);
        fileWriter.print(msg);
    }
}
