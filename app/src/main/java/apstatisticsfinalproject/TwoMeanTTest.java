package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.TDistribution;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class TwoMeanTTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the two mean t test calculator!");

        // Choose
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintStream pw = new PrintStream(System.out);

            pw.println("Enter null hypothesis (format - 'μ1 - μ2 = 0'): ");
            double nullDiff = Double.parseDouble(br.readLine().split(" ")[4]);

            pw.println("Enter alternate hypothesis (format - 'μ1 - μ2 </>/!= 0'): ");
            String[] words = br.readLine().split(" ");
            String alternateHypothesisSign = words[3];
            double alternateDiff = Double.parseDouble(words[4]);

            pw.println("Enter sample mean (for sample 1): ");
            double sampleMean1 = Double.parseDouble(br.readLine());

            pw.println("Enter standard deviation (for sample 1): ");
            double sampleSD1 = Double.parseDouble(br.readLine());

            pw.println("Enter sample size (for sample 1): ");
            int sampleSize1 = Integer.parseInt(br.readLine());

            pw.println("Enter sample mean (for sample 2): ");
            double sampleMean2 = Double.parseDouble(br.readLine());

            pw.println("Enter standard deviation (for sample 2): ");
            double sampleSD2 = Double.parseDouble(br.readLine());

            pw.println("Enter sample size (for sample 2): ");
            int sampleSize2 = Integer.parseInt(br.readLine());

            pw.println("Enter alpha level: ");
            double alphaLevel = Double.parseDouble(br.readLine());

        // Check
            // Random condition
            pw.println("Is random condition satisfied? (type true or false): ");
            boolean random = Boolean.parseBoolean(br.readLine());
            if (random) {
                pw.println("Random condition satisfied, results can be generalized to the entire population.");
            } else {
                pw.println("Random condition not satisfied, results can only be generalized to the entire sample.");
            }

            // 10% condition
            pw.println("Sampling without replacement? (type true or false): ");
            boolean samplingNoReplacement = Boolean.parseBoolean(br.readLine());
            if (!samplingNoReplacement) {
                pw.println("10% condition satisfied, we can sample without replacement.");
            }
            else {
                pw.println("Enter population size for sample 1 (infinity if not specified): ");
                String line1 = br.readLine();
                pw.println("Enter population size for sample 2 (infinity if not specified): ");
                String line2 = br.readLine();
                try {
                    int populationSize1 = Integer.parseInt(line1);
                    int populationSize2 = Integer.parseInt(line2);
                    if (sampleSize1 <= 0.1 * populationSize1 && sampleSize2 <= 0.1 * populationSize2) {
                        pw.println("10% condition satisfied");
                    } else {
                        pw.println("Sorry, can't continue with the procedure.");
                        pw.close();
                        System.exit(-1);
                    }
                } catch (NumberFormatException e) {
                    if (line1.equalsIgnoreCase("infinity") || line2.equalsIgnoreCase("infinity")) {
                        pw.println("10% condition satisfied, trials can be treated as independent.");
                    }
                }
            }

            // Large Counts condition
            if (sampleSize1 >= 30 && sampleSize2 >= 30) {
                pw.println("Large Counts condition satisfied, sampling distribution can be treated as approximately normal.");
            } else {
                pw.println("Does the problem say the sampling distribution is approximately normal? (type true or false): ");
                boolean condition = Boolean.parseBoolean(br.readLine());
                if (condition) {
                    pw.println("Large Counts condition satisfied, sampling distribution can be treated as approximately normal.");
                } else {
                    pw.println("Sorry, can't continue with the procedure.");
                    pw.close();
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

            DecimalFormat df = new DecimalFormat("#.###");

        // Conclude
            if (alternateHypothesisSign.equals("<")) {
                pw.println("Assuming the null hypothesis is true (p = " + df.format(nullDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p < " + df.format(alternateDiff) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p < " + df.format(alternateDiff) + ") is true.");
                }
            } else if (alternateHypothesisSign.equals(">")) {
                pw.println("Assuming the null hypothesis is true (p = " + df.format(nullDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p > " + df.format(alternateDiff) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p > " + df.format(alternateDiff) + ") is true.");
                }
            } else {
                pw.println("Assuming the null hypothesis is true (p = " + df.format(nullDiff) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p != " + df.format(alternateDiff) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p != " + df.format(alternateDiff) + ") is true.");
                }
            }

        br.close();
        pw.close();
    }
}