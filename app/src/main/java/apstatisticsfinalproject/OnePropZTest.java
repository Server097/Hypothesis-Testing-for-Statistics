package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.NormalDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class OnePropZTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the one proportion z test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintStream screenWriter = new PrintStream(System.out);
            PrintWriter fileWriter = new PrintWriter(new File("onePropZTest.out"));

            String[] nullHypothesis = Utilities.readString(screenReader, screenWriter,"Enter null hypothesis (format - 'p = ...'): " ).split(" ");
            double hypothesisProp = Double.parseDouble(nullHypothesis[2]);
            fileWriter.println("Null hypothesis (H_0): " + "p = " + hypothesisProp);
            fileWriter.println();

            String[] alternateHypothesis = Utilities.readString(screenReader, screenWriter,"Enter alternate hypothesis (format - 'p </>/!= ...'): " ).split(" ");
            String alternateHypothesisSign = alternateHypothesis[1];
            fileWriter.println("Alternative hypothesis (H_A): " + "p " + alternateHypothesisSign + " " + hypothesisProp);
            fileWriter.println();

            int successes = Utilities.readInteger(screenReader, screenWriter, "Enter number of successes: ", 0);
            fileWriter.println("successes = " + successes);
            fileWriter.println();

            int sampleSize = Utilities.readInteger(screenReader, screenWriter, "Enter sample size: ", 0);
            fileWriter.println("sample size = " + sampleSize);
            fileWriter.println();

            double alphaLevel = Utilities.readDouble(screenReader, screenWriter, "Enter alpha level: ", 0);
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
                screenWriter.println("Enter population size (infinity if not specified): ");
                String line = screenReader.readLine();
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
                    if (line.equalsIgnoreCase("infinity")) {
                        screenWriter.println("10% condition satisfied, trials can be treated as independent.");
                    }
                }
            }

            // Large Counts condition
            if (sampleSize * hypothesisProp >= 10 && sampleSize * (1 - hypothesisProp) >= 10) {
                screenWriter.println("Large Counts condition satisfied, sampling distribution can be treated as approximately normal.");
            } else {
                boolean condition = Utilities.readBoolean(screenReader, screenWriter, "Does the problem say the sampling distribution is approximately normal? (type true or false): ", true);
                if (condition) {
                    screenWriter.println("Large Counts condition satisfied, sampling distribution can be treated as approximately normal.");
                } else {
                    screenWriter.println("Sorry, can't continue with the procedure.");
                    screenWriter.close();
                    System.exit(-1);
                }
            }

        // Calculate
            double sampleProp = (double) successes / sampleSize;
            double standardError = Math.sqrt(hypothesisProp * (1 - hypothesisProp) / sampleSize);
            double zScore = (sampleProp - hypothesisProp) / standardError;

            NormalDistribution normal = new NormalDistribution();
            double pLeft = normal.cumulativeProbability(zScore);
            double pRight = 1 - pLeft;
            double pValue = 0;

            if (alternateHypothesisSign.equals("<")) {
                pValue = Math.round(pLeft * 100.0) / 100.0;
            } else if (alternateHypothesisSign.equals(">")) {
                pValue = Math.round(pRight * 100.0) / 100.0;
            } else {
                pValue = Math.round(2.0 * Math.min(pLeft, pRight) * 100.0) / 100.0;
            }

            fileWriter.println("z score = " + zScore);
            fileWriter.println();
            fileWriter.println("p value = " + pValue);
            fileWriter.println();

            DecimalFormat df = new DecimalFormat("#.##");

        // Conclude
            String msg;
            if (alternateHypothesisSign.equals("<")) {
                msg = "Assuming the null hypothesis is true (p = " + hypothesisProp + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p < " + df.format(hypothesisProp) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p < " + df.format(hypothesisProp) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else if (alternateHypothesisSign.equals(">")) {
                msg = "Assuming the null hypothesis is true (p = " + df.format(hypothesisProp) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p > " + df.format(hypothesisProp) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p > " + df.format(hypothesisProp) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else {
                msg = "Assuming the null hypothesis is true (p = " + df.format(hypothesisProp) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p != " + df.format(hypothesisProp) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p != " + df.format(hypothesisProp) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);
            }

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}