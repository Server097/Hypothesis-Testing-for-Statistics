package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ChiSquaredIndependenceTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the chi squared independence test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter fileWriter = new PrintWriter(new File("independence.out"));
            PrintStream screenWriter = new PrintStream(System.out);

            String variable1 = Utilities.readString(screenReader, screenWriter, "Enter variable 1: ");
            String variable2 = Utilities.readString(screenReader, screenWriter, "Enter variable 2: ");

            fileWriter.println("Null hypothesis (H_0): " + variable1 + " and " + variable2 + " are independent.");
            fileWriter.println();
            fileWriter.println("Alternative hypothesis (H_A): " + variable1 + " and " + variable2 + " are associated.");
            fileWriter.println();

            int rows = Utilities.readInteger(screenReader, screenWriter, "Enter number of rows (excluding total): ", 2);
            int cols = Utilities.readInteger(screenReader, screenWriter, "Enter number of columns (excluding total): ", 2);

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

            int sampleSize = observedCounts[rows - 1][cols - 1];


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
                String line = Utilities.readString(screenReader, screenWriter, "Enter population size for sample 1 (leave blank if not specified): ");
                try {
                    int populationSize = Integer.parseInt(line);
                    if (sampleSize <= 0.1 * populationSize) {
                        screenWriter.println("10% condition satisfied");
                    } else {
                        screenWriter.println("Sorry, can't continue with the procedure.");
                        screenWriter.close();
                        System.exit(-1);
                    }
                } catch (NumberFormatException e) {
                    if (line.isEmpty()) {
                        screenWriter.println("10% condition satisfied, trials can be treated as independent.");
                    }
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
                    chiSquaredStat += Math.pow(observedCounts[r][c] - expectedCounts[r][c], 2) / expectedCounts[r][c];
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
            msg = ("Assuming the null hypothesis is true (" + variable1 + " and " + variable2 + " are independent)" + ", " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
            Utilities.writeMessage(msg, screenWriter, fileWriter);
            
            if (pValue < alphaLevel) {
                msg = ("Since the p-value is less than the alpha level, we reject the null hypothesis and conclude that the alternate hypothesis (" + variable1 + " and " + variable2 + " are associated) is true.");
            } else {
                msg = ("Since the p-value is greater than or equal to the alpha level, we fail to reject the null hypothesis and do not have enough evidence to conclude that the alternate hypothesis ( " + variable1 + " and " + variable2 + " are associated) is true.");
            }
            Utilities.writeLastMessage(msg, screenWriter, fileWriter);

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}