package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ChiSquaredHomogeneityTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the chi squared homogeneity test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter fileWriter = new PrintWriter(new File("homogeneity.out"));
            PrintStream screenWriter = new PrintStream(System.out);

            String variable1 = Utilities.readString(screenReader, screenWriter, "Enter variable 1: ");
            String variable2 = Utilities.readString(screenReader, screenWriter, "Enter variable 2: ");

            fileWriter.println("Null hypothesis (H_0): " + "The distribution of " + variable1 + " is the same among the populations of " + variable2 + ".");
            fileWriter.println();
            fileWriter.println("Alternative hypothesis (H_A): " + "The distribution of " + variable1 + " is different for at least one population of " + variable2 + ".");
            fileWriter.println();

            int rows = Utilities.readInteger(screenReader, screenWriter, "Enter number of rows (excluding total): ", 3);
            int cols = Utilities.readInteger(screenReader, screenWriter, "Enter number of columns (excluding total): ", 3);

            int[][] observedCounts = new int[rows + 1][cols + 1];
            double[][] expectedCounts = new double[rows + 1][cols + 1];

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    screenWriter.print("Enter observed count for row " + (r + 1) + ", column " + (c + 1) + ": ");
                    observedCounts[r][c] = Integer.parseInt(screenReader.readLine());
                }
            }

            for (int r = 0; r < rows; r++) {
                int rowSum = 0;
                for (int c = 0; c < cols; c++) {
                    rowSum += observedCounts[r][c];
                }
                observedCounts[r][cols] = rowSum;
            }

            for (int c = 0; c < cols + 1; c++) {
                int colSum = 0;
                for (int r = 0; r < rows; r++) {
                    colSum += observedCounts[r][c];
                }
                observedCounts[rows][c] = colSum;
            }

            screenWriter.println(Arrays.toString(observedCounts));
            for (int r = 0; r < rows + 1; r++) {
                for (int c = 0; c < cols + 1; c++) {
                    expectedCounts[r][c] = (double) (observedCounts[r][cols] * observedCounts[rows][c]) / observedCounts[rows][cols];
                }
            }

            double alphaLevel = Utilities.readDouble(screenReader, screenWriter, "Enter alpha level: ", 0.05);
            fileWriter.println("alpha level = " + alphaLevel);
            fileWriter.println();

        // Check
            // Random condition
            boolean random = Utilities.readBoolean(screenReader, screenWriter, "Is random condition satisfied? (type true or false): ", true);
            if (random) {
                screenWriter.println("Random condition satisfied, results can be generalized to the entire population.");
            } else {
                screenWriter.println("Random condition not satisfied, results can only be generalized to the entire sample.");
            }

            // 10% condition
            boolean samplingNoReplacement = Utilities.readBoolean(screenReader, screenWriter, "Sampling without replacement? (type true or false): ", true);
            if (!samplingNoReplacement) {
                screenWriter.println("10% condition satisfied, we can sample without replacement.");
            }
            else {
                int rowsPassingCondition = 0;

                for (int r = 0; r < rows; r++) {
                    screenWriter.print("Population size for row " + (r + 1) + ": ");
                    String line = screenReader.readLine();
                    if (line.isEmpty()) {
                        rowsPassingCondition++;
                        continue;
                    }
                    else if (expectedCounts[r][cols] <= 0.1 * Integer.parseInt(line)) {
                        rowsPassingCondition++;
                    }
                }

                if (rowsPassingCondition == rows) {
                    screenWriter.println("10% condition satisfied, trials can be treated as independent.");
                } else {
                    screenWriter.println("Sorry, can't continue with the procedure.");
                    screenWriter.close();
                    System.exit(-1);
                }
            }    


            // Large Counts condition
            int totalExpectedCount = rows * cols;
            int expectedCountMoreThan5 = 0;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (expectedCounts[r][c] >= 5) {
                        expectedCountMoreThan5++;
                    }
                }
            }

            if (expectedCountMoreThan5 == totalExpectedCount) {
                screenWriter.println("Large Counts condition satisfied.");
            } else {
                screenWriter.println("Large Counts condition not satisfied.");
                screenWriter.println("Sorry, can't continue with the procedure.");
                screenWriter.close();
                System.exit(-1);
            }

        // Calculate
            double chiSquaredStat = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    chiSquaredStat += (Math.pow(observedCounts[r][c] - expectedCounts[r][c], 2) / expectedCounts[r][c]);
                }
            }

            chiSquaredStat *= 1000.0;
            chiSquaredStat /= 1000.0;

            double degreesOfFreedom = (rows - 1) * (cols - 1);
            double pLeft = new ChiSquaredDistribution(degreesOfFreedom).cumulativeProbability(chiSquaredStat);
            double pValue = 1 - (pLeft * 1000.0)/ 1000.0;

            DecimalFormat df = new DecimalFormat("#.###");
            fileWriter.println("chi squared statistic = " + df.format(chiSquaredStat));
            fileWriter.println();
            fileWriter.println("degrees of freedom = " + df.format(degreesOfFreedom));
            fileWriter.println();
            fileWriter.println("p value = " + df.format(pValue));
            fileWriter.println();

        // Conclude
            String msg;
            msg = "Assuming the null hypothesis is true (" + variable1 + " and " + variable2 + " are indapp/src/main/java/apstatisticsfinalproject/ChiSquaredHomogeneityTest.javaependent)" + ", " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.";
            Utilities.writeMessage(msg, screenWriter, fileWriter);

            if (pValue < alphaLevel) {
                msg = "Since the p-value is less than the alpha level, we reject the null hypothesis and conclude that the alternate hypothesis (" + "The distribution of " + variable1 + " is different for at least one population of " + variable2 + ") is true.";
            } else {
                msg = "Since the p-value is greater than or equal to the alpha level, we fail to reject the null hypothesis and do not have enough evidence to conclude that the alternate hypothesis (" + "The distribution of " + variable1 + " is different for at least one population of " + variable2 + ") is true.";
            }
            Utilities.writeLastMessage(msg, screenWriter, fileWriter);

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}