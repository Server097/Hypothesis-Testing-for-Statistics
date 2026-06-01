package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.TDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class TwoMeanTTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the two mean t test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintStream screenWriter = new PrintStream(System.out);
            PrintWriter fileWriter = new PrintWriter(new File("twoMeanTTest.out"));

            screenWriter.println("Enter null hypothesis (format - 'μ1 - μ2 = 0'): ");
            String[] nullHypothesis = Utilities.readString(screenReader, screenWriter,"Enter null hypothesis (format - 'μ1 - μ2 = 0'): " ).split(" ");
            double hypothesisDiff = Double.parseDouble(nullHypothesis[4]);
            fileWriter.println("Null hypothesis (H_0): " + "μ1 - μ2 = " + hypothesisDiff);
            fileWriter.println();

            screenWriter.println("Enter alternate hypothesis (format - 'μ1 - μ2 </>/!= 0'): ");
            String[] alternateHypothesis = Utilities.readString(screenReader, screenWriter, "Enter alternate hypothesis (format - 'μ1 - μ2 </>/!= 0'): ").split(" ");
            String alternateHypothesisSign = alternateHypothesis[3];
            fileWriter.println("Alternative hypothesis (H_A): " + "μ1 - μ2 " + alternateHypothesisSign + " " + hypothesisDiff);
            fileWriter.println();

            double sampleMean1 = Utilities.readDouble(screenReader, screenWriter, "Enter sample mean (for sample 1): ", 0);
            fileWriter.println("sample mean for sample 1 = " + sampleMean1);

            double sampleSD1 = Utilities.readDouble(screenReader, screenWriter, "Enter standard deviation (for sample 1): ", 0);
            fileWriter.println("standard deviation for sample 1 = " + sampleSD1);
            fileWriter.println();

            int sampleSize1 = Utilities.readInteger(screenReader, screenWriter, "Enter sample size (for sample 1): ", 0);
            fileWriter.println("sample size for sample 1 = " + sampleSize1);
            fileWriter.println();

            double sampleMean2 = Utilities.readDouble(screenReader, screenWriter, "Enter sample mean (for sample 2): ", 0);
            fileWriter.println("sample mean for sample 2 = " + sampleMean2);
            fileWriter.println();

            double sampleSD2 = Utilities.readDouble(screenReader, screenWriter, "Enter standard deviation (for sample 2): ", 0);
            fileWriter.println("standard deviation for sample 2 = " + sampleSD2);
            fileWriter.println();

            int sampleSize2 = Utilities.readInteger(screenReader, screenWriter, "Enter sample size (for sample 2): ", 0);
            fileWriter.println("sample size for sample 2 = " + sampleSize2);
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
                    if (line1.equalsIgnoreCase("infinity") || line2.equalsIgnoreCase("infinity")) {
                        screenWriter.println("10% condition satisfied, trials can be treated as independent.");
                    }
                }
            }

            // Large Counts condition
            if (sampleSize1 >= 30 && sampleSize2 >= 30) {
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
            double differenceInMeans = sampleMean1 - sampleMean2;
            double standardError = Math.sqrt((Math.pow(sampleSD1, 2) / sampleSize1) + (Math.pow(sampleSD2, 2) / sampleSize2));
            double tScore = differenceInMeans / standardError;

            int degreesOfFreedom = Math.min(sampleSize1 - 1, sampleSize2 - 1);
            TDistribution tDist = new TDistribution(degreesOfFreedom);
            double pLeft = tDist.cumulativeProbability(tScore);
            double pRight = 1 - pLeft;
            double pValue = 0;

            if (alternateHypothesisSign.equals("<")) {
                pValue = Math.round(pLeft * 1000.0) / 1000.0;
            } else if (alternateHypothesisSign.equals(">")) {
                pValue = Math.round(pRight * 1000.0) / 1000.0;
            } else {
                pValue = Math.round(2.0 * Math.min(pLeft, pRight) * 1000.0) / 1000.0;
            }

            fileWriter.println("t score = " + tScore);
            fileWriter.println();
            fileWriter.println("degrees of freedom = " + degreesOfFreedom);
            fileWriter.println();
            fileWriter.println("p value = " + pValue);
            fileWriter.println();

            DecimalFormat df = new DecimalFormat("#.###");

        // Conclude
        String msg;
            if (alternateHypothesisSign.equals("<")) {
                msg = "Assuming the null hypothesis is true (μ1 - μ2 = " + df.format(hypothesisDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ1 - μ2 < " + df.format(hypothesisDiff) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ1 - μ2 < " + df.format(hypothesisDiff) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else if (alternateHypothesisSign.equals(">")) {
                msg = "Assuming the null hypothesis is true (μ1 - μ2 = " + df.format(hypothesisDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ1 - μ2 > " + df.format(hypothesisDiff) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ1 - μ2 > " + df.format(hypothesisDiff) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else {
                msg = "Assuming the null hypothesis is true (μ1 - μ2 = " + df.format(hypothesisDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ1 - μ2 != " + df.format(hypothesisDiff) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ1 - μ2 != " + df.format(hypothesisDiff) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);
            }

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}