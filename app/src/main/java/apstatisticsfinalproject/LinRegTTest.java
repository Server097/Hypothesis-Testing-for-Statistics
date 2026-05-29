package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.TDistribution;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class LinRegTTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the linear regression t test calculator!");

        // Choose
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintStream pw = new PrintStream(System.out);

            pw.println("Enter null hypothesis (format - 'β = 0'): ");
            double hypothesisSlope = Double.parseDouble(br.readLine().split(" ")[2]);

            pw.println("Enter alternate hypothesis (format - 'β </>/!= 0'): ");
            String[] words = br.readLine().split(" ");
            String alternateHypothesisSign = words[1];

            pw.println("Enter sample size (same sample size for both samples): ");
            int sampleSize = Integer.parseInt(br.readLine());

            pw.println("Enter sample slope: ");
            double sampleSlope = Double.parseDouble(br.readLine());

            pw.println("Enter sample standard error of slope: ");
            double sampleSE = Double.parseDouble(br.readLine());

            pw.println("Enter alpha level: ");
            double alphaLevel = Double.parseDouble(br.readLine());

        // Check
            // Linear condition
            pw.println("Is linear condition satisfied? (type true or false): ");
            boolean linear = Boolean.parseBoolean(br.readLine());
            if (linear) {
                pw.println("Linear condition satisfied, relationship between x and y is approximately linear.");
            } else {
                pw.println("Linear condition not satisfied, relationship between x and y is not approximately linear");
                pw.println("Sorry, can't continue with the procedure.");
                pw.close();
                System.exit(-1);
            }

            // Independent/10% condition
            pw.println("Is the random condition satisfied (type true or false): ");

            pw.println("Sampling without replacement? (type true or false): ");
            boolean samplingNoReplacement = Boolean.parseBoolean(br.readLine());
            if (!samplingNoReplacement) {
                pw.println("10% condition satisfied, we can sample without replacement.");
            }
            else {
                pw.println("Enter population size for sample 1 (infinity if not specified): ");
                String line = br.readLine();
                try {
                    int populationSize = Integer.parseInt(line);
                    if (sampleSize <= 0.1 * populationSize) {
                        pw.println("10% condition satisfied");
                    } else {
                        pw.println("Sorry, can't continue with the procedure.");
                        pw.close();
                        System.exit(-1);
                    }
                } catch (NumberFormatException e) {
                    if (line.equalsIgnoreCase("infinity")) {
                        pw.println("10% condition satisfied, trials can be treated as independent.");
                    }
                }
            }

            // Normal condition
            pw.println("Are the residuals approximately normally distributed? (type true or false): ");
            boolean normal = Boolean.parseBoolean(br.readLine());
            if (normal) {
                pw.println("Normal condition satisfied, residuals are approximately normally distributed.");
            } else {
                pw.println("Normal condition not satisfied, residuals are skewed or have outliers.");
            }

            // Equal variance condition
            pw.println("Do the residuals have equal variance? (spread of residuals should be roughly constant across all x-values, no funnel shape; type true or false): ");
            boolean equalVariance = Boolean.parseBoolean(br.readLine());
            if (equalVariance) {
                pw.println("Equal variance condition satisfied, residuals have equal variance.");
            } else {
                pw.println("Equal variance condition not satisfied, residuals do not have equal variance.");
            }

            // Random condition
            pw.println("Is the random condition satisfied?");
            boolean random = Boolean.parseBoolean(br.readLine());
            if (random) {
                pw.println("Random condition satisfied, results can be generalized to the entire population.");
            } else {
                pw.println("Random condition not satisfied, results can only be generalized to the entire sample.");
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

            DecimalFormat df = new DecimalFormat("#.###");

        // Conclude
            if (alternateHypothesisSign.equals("<")) {
                pw.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β < " + df.format(hypothesisSlope) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β < " + df.format(hypothesisSlope) + ") is true.");
                }
            } else if (alternateHypothesisSign.equals(">")) {
                pw.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β > " + df.format(hypothesisSlope) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β > " + df.format(hypothesisSlope) + ") is true.");
                }
            } else {
                pw.println("Assuming the null hypothesis is true (β = " + df.format(hypothesisSlope) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (β != " + df.format(hypothesisSlope) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (β != " + df.format(hypothesisSlope) + ") is true.");
                }
            }

        br.close();
        pw.close();
    }
}