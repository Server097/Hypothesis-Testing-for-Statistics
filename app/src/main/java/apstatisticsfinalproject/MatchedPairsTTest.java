package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.TDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class MatchedPairsTTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the matched pairs t test calculator! (order of subtraction is sample 1 - sample 2)");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintStream screenWriter = new PrintStream(System.out);
            PrintWriter fileWriter = new PrintWriter(new File("matchedPairsTTest.out"));

            String[] nullHypothesis = Utilities.readString(screenReader, screenWriter,"Enter null hypothesis (format - 'μ_diff = 0'): " ).split(" ");
            double hypothesisMean = Double.parseDouble(nullHypothesis[2]);

            fileWriter.println("Null hypothesis (H_0): " + "μ_diff = " + hypothesisMean);
            fileWriter.println();

            screenWriter.println("Enter alternate hypothesis (format - 'μ_diff </>/!= 0'): ");
            String[] alternateHypothesis = Utilities.readString(screenReader, screenWriter, "Enter alternate hypothesis (format - 'μ_diff </>/!= 0'): ").split(" ");
            String alternateHypothesisSign = alternateHypothesis[1];
            fileWriter.println("Alternate hypothesis (H_A): " + "μ_diff " + alternateHypothesisSign + " " + hypothesisMean);
            fileWriter.println();

            int sampleSize = Utilities.readInteger(screenReader, screenWriter, "Enter sample size (same sample size for both samples): ", 0);
            fileWriter.println("sample size = " + sampleSize);
            fileWriter.println();

            String inputType = Utilities.readString(screenReader, screenWriter, "Are you entering the values for the samples or do you already have the sample mean and SD? (enter 'v' for values or 's' for sample mean and SD)"); 
            
            double sampleMeanDiff, sampleSD;
            if (inputType.equals("s")) {
                sampleMeanDiff = Utilities.readDouble(screenReader, screenWriter, "Enter sample mean difference: ", 0);
                sampleSD = Utilities.readDouble(screenReader, screenWriter, "Enter standard deviation of differences: ", 0);
            } else {
                double[] differences = new double[sampleSize];
                double[] sample1Values = new double[sampleSize];
                double[] sample2Values = new double[sampleSize];

                for (int i = 0; i < sampleSize; i++) {
                    sample1Values[i] = Utilities.readDouble(screenReader, screenWriter, "Enter value " + (i + 1) + " for sample 1: ", 0);
                }

                for (int i = 0; i < sampleSize; i++) {
                    sample2Values[i] = Utilities.readDouble(screenReader, screenWriter, "Enter value " + (i + 1) + " for sample 2: ", 0);
                }

                for (int i = 0; i < sampleSize; i++) {
                    differences[i] = sample1Values[i] - sample2Values[i];
                }

                double sum = 0;
                for (int i = 0; i < sampleSize; i++) {
                    sum += differences[i];
                }
                
                sampleMeanDiff = sum / sampleSize;

                double squaredDiffSum = 0;
                for (int i = 0; i < sampleSize; i++) {
                    squaredDiffSum += Math.pow(differences[i] - sampleMeanDiff, 2);
                }
                
                double varianceDiff = squaredDiffSum / (sampleSize - 1);
                sampleSD = Math.sqrt(varianceDiff);
            }

            double alphaLevel = Utilities.readDouble(screenReader, screenWriter, "Enter alpha level: ", 0.05);

        // Check
            // Random condition
            screenWriter.println("Is random condition satisfied? (type true or false): ");
            boolean random = Boolean.parseBoolean(screenReader.readLine());
            if (random) {
                screenWriter.println("Random condition satisfied, results can be generalized to the entire population.");
            } else {
                screenWriter.println("Random condition not satisfied, results can only be generalized to the entire sample.");
            }

            // 10% condition
            screenWriter.println("Sampling without replacement? (type true or false): ");
            boolean samplingNoReplacement = Boolean.parseBoolean(screenReader.readLine());
            if (!samplingNoReplacement) {
                screenWriter.println("10% condition satisfied, we can sample without replacement.");
            }
            else {
                screenWriter.println("Enter population size for sample 1 (infinity if not specified): ");
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
            double standardError = sampleSD / Math.sqrt(sampleSize);
            double tScore = sampleMeanDiff / standardError;

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
                msg = ("Assuming the null hypothesis is true (μ_diff = " + df.format(hypothesisMean) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = ("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ_diff < " + df.format(hypothesisMean) + ") is true.");
                } else {
                    msg = ("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ_diff < " + df.format(hypothesisMean) + ") is true.");
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else if (alternateHypothesisSign.equals(">")) {
                msg = ("Assuming the null hypothesis is true (μ_diff = " + df.format(hypothesisMean) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.");
                Utilities.writeMessage(msg, screenWriter, fileWriter);
                
                if (pValue <= alphaLevel) {
                    msg = ("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ_diff > " + df.format(hypothesisMean) + ") is true.");
                } else {
                    msg = ("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ_diff > " + df.format(hypothesisMean) + ") is true.");
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);

            } else {
                msg = ("Assuming the null hypothesis is true (μ_diff = " + df.format(hypothesisMean) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.");
                Utilities.writeMessage(msg, screenWriter, fileWriter);

                if (pValue <= alphaLevel) {
                    msg = ("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (μ_diff != " + df.format(hypothesisMean) + ") is true.");
                } else {
                    msg = ("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (μ_diff != " + df.format(hypothesisMean) + ") is true.");
                }
                Utilities.writeLastMessage(msg, screenWriter, fileWriter);
            }

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}