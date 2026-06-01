package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.NormalDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class TwoPropZTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the two proportion z test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter fileWriter = new PrintWriter(new File("twoPropZTest.out"));
            PrintStream screenWriter = new PrintStream(System.out);

            String[] nullHypothesis = Utilities.readString(screenReader, screenWriter,"Enter null hypothesis (format - 'p1 - p2 = 0'): " ).split(" ");
            int nullDiff = Integer.parseInt(nullHypothesis[4]);
            fileWriter.println("Null hypothesis (H_0): " + "p1 - p2 = 0");
            fileWriter.println();

            String[] alternateHypothesis = Utilities.readString(screenReader, screenWriter, "Enter alternate hypothesis (format - 'p1 - p2 </>/!= 0'): ").split(" ");
            String alternateHypothesisSign = alternateHypothesis[3];
            fileWriter.println("Alternative hypothesis (H_A): " + "p1 - p2 " + alternateHypothesisSign + " 0");
            fileWriter.println();

            int successes1 = Utilities.readInteger(screenReader, screenWriter, "Enter number of successes (for sample 1): ", 0);
            fileWriter.println("successes for sample 1 = " + successes1);
            fileWriter.println();

            int sampleSize1 = Utilities.readInteger(screenReader, screenWriter, "Enter sample size (for sample 1): ", 0);
            fileWriter.println("sample size for sample 1 = " + sampleSize1);
            fileWriter.println();

            double sampleProp1 = (double) successes1 / sampleSize1;

            int successes2 = Utilities.readInteger(screenReader, screenWriter, "Enter number of successes (for sample 2): ", 0);
            fileWriter.println("successes for sample 2 = " + successes2);
            fileWriter.println();

            int sampleSize2 = Utilities.readInteger(screenReader, screenWriter, "Enter sample size (for sample 2): ", 0);
            fileWriter.println("sample size for sample 2 = " + sampleSize2);
            fileWriter.println();
            double sampleProp2 = (double) successes2 / sampleSize2;

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
                screenWriter.println("Enter population size for sample 1 (infinity if not specified): ");
                String line1 = screenReader.readLine();
                screenWriter.println("Enter population size for sample 2 (infinity if not specified): ");
                String line2 = screenReader.readLine();
                try {
                    int populationSize1 = Integer.parseInt(line1);
                    int populationSize2 = Integer.parseInt(line2);
                    if (sampleSize1 <= 0.1 * populationSize1 && sampleSize2 <= 0.1 * populationSize2) {
                        screenWriter.println("10% condition satisfied");
                    } else {
                        screenWriter.println("Sorry, can't continue with the procedure.");
                        screenWriter.close();
                        System.exit(-1);
                    }
                } catch (NumberFormatException e) {
                    if (line1.isEmpty() || line2.isEmpty()) {
                        screenWriter.println("10% condition satisfied, trials can be treated as independent.");
                    }
                }
            }

            // Large Counts condition
            if (sampleSize1 * sampleProp1 >= 10 && sampleSize1 * (1 - sampleProp1) >= 10 && sampleSize2 * sampleProp2 >= 10 && sampleSize2 * (1 - sampleProp2) >= 10) {
                screenWriter.println("Large Counts condition satisfied, sampling distribution can be treated as approximately normal.");
            } else {
                screenWriter.println("Does the problem say the sampling distribution is approximately normal? (type true or false): ");
                boolean condition = Boolean.parseBoolean(screenReader.readLine());
                if (condition) {
                    screenWriter.println("Large Counts condition satisfied, sampling distribution can be treated as approximately normal.");
                } else {
                    screenWriter.println("Sorry, can't continue with the procedure.");
                    screenWriter.close();
                    System.exit(-1);
                }
            }

        // Calculate
            double pooledProp = (double) (successes1 + successes2) / (sampleSize1 + sampleSize2);
            double standardError = Math.sqrt(pooledProp * (1 - pooledProp) / sampleSize1 + pooledProp * (1 - pooledProp) / sampleSize2);
            double zScore = (sampleProp1 - sampleProp2) / standardError;

            NormalDistribution normal = new NormalDistribution();
            double pLeft = normal.cumulativeProbability(zScore);
            double pRight = 1 - pLeft;
            double pValue = 0;

            if (alternateHypothesisSign.equals("<")) {
                pValue = Math.round(pLeft * 1000.0) / 1000.0;
            } else if (alternateHypothesisSign.equals(">")) {
                pValue = Math.round(pRight * 1000.0) / 1000.0;
            } else {
                pValue = Math.round(2.0 * Math.min(pLeft, pRight) * 1000.0) / 1000.0;
            }
            fileWriter.println("z score = " + zScore);
            fileWriter.println();
            fileWriter.println("p value = " + pValue);
            fileWriter.println();

            DecimalFormat df = new DecimalFormat("#.###");

        // Conclude
            String msg = "";
            if (alternateHypothesisSign.equals("<")) {
                msg = "Assuming the null hypothesis is true (p1 - p2 = " + df.format(nullDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p1 - p2 < " + df.format(nullDiff) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p1 - p2 < " + df.format(nullDiff) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else if (alternateHypothesisSign.equals(">")) {
                msg = "Assuming the null hypothesis is true (p1 - p2 = " + df.format(nullDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p1 - p2 > " + df.format(nullDiff) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p1 - p2 > " + df.format(nullDiff) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else {
                msg = "Assuming the null hypothesis is true (p1 - p2 = " + df.format(nullDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p1 - p2 != " + df.format(nullDiff) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p1 - p2 != " + df.format(nullDiff) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);
            }

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}