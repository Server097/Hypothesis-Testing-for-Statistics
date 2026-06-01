package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.TDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class OneMeanTTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the one mean t test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter fileWriter = new PrintWriter(new File("oneMeanTTest.out"));
            PrintStream screenWriter = new PrintStream(System.out);

            String nullHypothesis = Utilities.readString(screenReader, screenWriter, "Enter null hypothesis (format - 'μ = 0'): ");
            double hypothesisMean = Double.parseDouble(nullHypothesis.split(" ")[2]);
            fileWriter.println("Null hypothesis (H_0): " + "μ = " + hypothesisMean);
            fileWriter.println();

            String[] alternateHypothesis = Utilities.readString(screenReader, screenWriter, "Enter alternate hypothesis (format - 'μ </>/!= 0'): ").split(" ");
            String alternateHypothesisSign = alternateHypothesis[1];
            fileWriter.println("Alternate hypothesis (H_A): " + "μ " + alternateHypothesisSign + " " + hypothesisMean);
            fileWriter.println();

            double sampleMean = Utilities.readDouble(screenReader, screenWriter, "Enter sample mean: ", 0);
            fileWriter.println("sample mean = " + sampleMean);
            fileWriter.println();

            double sampleSD = Utilities.readDouble(screenReader, screenWriter, "Enter standard deviation: ", 0);
            fileWriter.println("standard deviation = " + sampleSD);
            fileWriter.println();

            int sampleSize = Utilities.readInteger(screenReader, screenWriter, "Enter sample size: ", 0);
            fileWriter.println("sample size = " + sampleSize);
            fileWriter.println();

            double alphaLevel = Utilities.readDouble(screenReader, screenWriter, "Enter alpha level: ", 0.05);
            fileWriter.println("alpha level = " + alphaLevel);
            fileWriter.println();

        // Check
            // Random condition
            boolean random = Utilities.readBoolean(screenReader, screenWriter, "Is random condition satisfied? (type true or false):", true);
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
            if (sampleSize >= 30) {
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
            double standardError = sampleSD / Math.sqrt(sampleSize);
            double tScore = (sampleMean / standardError) * 1000.0 / 1000.0;

            int degreesOfFreedom = sampleSize - 1;
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
                msg = "Assuming the null hypothesis is true (p = " + df.format(hypothesisMean) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ < " + df.format(hypothesisMean) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ < " + df.format(hypothesisMean) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else if (alternateHypothesisSign.equals(">")) {
                msg = "Assuming the null hypothesis is true (p = " + df.format(hypothesisMean) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ > " + df.format(hypothesisMean) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ > " + df.format(hypothesisMean) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else {
                msg = "Assuming the null hypothesis is true (p = " + df.format(hypothesisMean) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.";
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = "Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ != " + df.format(hypothesisMean) + ") is true.";
                } else {
                    msg = "Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ != " + df.format(hypothesisMean) + ") is true.";
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);
            }

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}