package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.TDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class LinRegTTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the linear regression t test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter fileWriter = new PrintWriter(new File("linreg.out"));
            PrintStream screenWriter = new PrintStream(System.out);

            String[] nullHypothesis = Utilities.readString(screenReader, screenWriter, "Enter null hypothesis (format - 'β = 0'): ").split(" ");
            int hypothesisSlope = Integer.parseInt(nullHypothesis[4]);
            fileWriter.println("Null hypothesis (H_0): " + "β = 0");
            fileWriter.println();

            String[] alternateHypothesis = Utilities.readString(screenReader, screenWriter, "Enter alternate hypothesis (format - 'β </>/!= 0'): ").split(" ");
            String alternateHypothesisSign = alternateHypothesis[1];
            fileWriter.println("Alternative hypothesis (H_A): " + "β " + alternateHypothesisSign + " 0");
            fileWriter.println();

            int sampleSize = Utilities.readInteger(screenReader, screenWriter, "Enter sample size (same sample size for both samples): ", 0);
            fileWriter.println("sample size = " + sampleSize);
            fileWriter.println();

            double sampleSlope = Utilities.readDouble(screenReader, screenWriter, "Enter sample slope: ", 0.0);
            fileWriter.println("sample slope = " + sampleSlope);
            fileWriter.println();

            double sampleSE = Utilities.readDouble(screenReader, screenWriter, "Enter sample standard error of slope: ", 0.0);
            fileWriter.println("sample SE = " + sampleSE);
            fileWriter.println();

            double alphaLevel = Utilities.readDouble(screenReader, screenWriter, "Enter alpha level: ", 0.05);
            fileWriter.println("alpha level = " + alphaLevel);
            fileWriter.println();

        // Check
            // Linear condition
            boolean linear = Utilities.readBoolean(screenReader, screenWriter, "Is linear condition satisfied? (type true or false): ", true);
            if (linear) {
                screenWriter.println("Linear condition satisfied, relationship between x and y is approximately linear.");
            } else {
                screenWriter.println("Linear condition not satisfied, relationship between x and y is not approximately linear");
                screenWriter.println("Sorry, can't continue with the procedure.");
                screenWriter.close();
                System.exit(-1);
            }

            // Independent/10% condition
            screenWriter.println("Is the random condition satisfied (type true or false): ");

            boolean samplingNoReplacement = Utilities.readBoolean(screenReader, screenWriter, "Sampling without replacement? (type true or false): ", true);
            if (!samplingNoReplacement) {
                screenWriter.println("10% condition satisfied, we can sample without replacement.");
            }
            else {
                screenWriter.println("Enter population size (leave blank if not specified): ");
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
                    if (line.isEmpty()) {
                        screenWriter.println("10% condition satisfied, trials can be treated as independent.");
                    }
                }
            }

            // Normal condition
            screenWriter.println("Are the residuals approximately normally distributed? (type true or false): ");
            boolean normal = Boolean.parseBoolean(screenReader.readLine());
            if (normal) {
                screenWriter.println("Normal condition satisfied, residuals are approximately normally distributed.");
            } else {
                screenWriter.println("Normal condition not satisfied, residuals are skewed or have outliers.");
            }

            // Equal variance condition
            screenWriter.println("Do the residuals have equal variance? (spread of residuals should be roughly constant across all x-values, no funnel shape; type true or false): ");
            boolean equalVariance = Boolean.parseBoolean(screenReader.readLine());
            if (equalVariance) {
                screenWriter.println("Equal variance condition satisfied, residuals have equal variance.");
            } else {
                screenWriter.println("Equal variance condition not satisfied, residuals do not have equal variance.");
            }

            // Random condition
            screenWriter.println("Is the random condition satisfied?");
            boolean random = Boolean.parseBoolean(screenReader.readLine());
            if (random) {
                screenWriter.println("Random condition satisfied, results can be generalized to the entire population.");
            } else {
                screenWriter.println("Random condition not satisfied, results can only be generalized to the entire sample.");
            }

        // Calculate
            double tScore = sampleSlope / sampleSE;

            int degreesOfFreedom = sampleSize - 2;
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
            if (alternateHypothesisSign.equals("<")) {
                screenWriter.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
                fileWriter.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
                fileWriter.println();
                if (pValue <= alphaLevel) {
                    screenWriter.println("Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β < " + df.format(hypothesisSlope) + ") is true.");
                    fileWriter.print("Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β < " + df.format(hypothesisSlope) + ") is true.");
                } else {
                    screenWriter.println("Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β < " + df.format(hypothesisSlope) + ") is true.");
                    fileWriter.print("Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β < " + df.format(hypothesisSlope) + ") is true.");
                }
            } else if (alternateHypothesisSign.equals(">")) {
                screenWriter.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.");
                fileWriter.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.");
                fileWriter.println();
                if (pValue <= alphaLevel) {
                    screenWriter.println("Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β > " + df.format(hypothesisSlope) + ") is true.");
                    fileWriter.print("Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β > " + df.format(hypothesisSlope) + ") is true.");
                } else {
                    screenWriter.println("Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β > " + df.format(hypothesisSlope) + ") is true.");
                    fileWriter.print("Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β > " + df.format(hypothesisSlope) + ") is true.");
                }
            } else {
                screenWriter.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.");
                if (pValue <= alphaLevel) {
                    screenWriter.println("Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β != " + df.format(hypothesisSlope) + ") is true.");
                    fileWriter.print("Because the p value (" + df.format(pValue) + ") is less than or equal to " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β != " + df.format(hypothesisSlope) + ") is true.");
                } else {
                    screenWriter.println("Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β != " + df.format(hypothesisSlope) + ") is true.");
                    fileWriter.print("Because the p value (" + df.format(pValue) + ") is greater than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β != " + df.format(hypothesisSlope) + ") is true.");
                }
            }

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}