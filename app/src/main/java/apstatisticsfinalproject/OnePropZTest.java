package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.NormalDistribution;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class OnePropZTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the one proportion z test calculator!");

        // Choose
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintStream pw = new PrintStream(System.out);

            pw.println("Enter null hypothesis (format - 'p = ...'): ");
            double nullProp = Double.parseDouble(br.readLine().split(" ")[2]);

            pw.println("Enter alternate hypothesis (format - 'p </>/!= ...'): ");
            String[] words = br.readLine().split(" ");
            String alternateHypothesisSign = words[1];
            double alternateProp = Double.parseDouble(words[2]);

            pw.println("Enter number of successes: ");
            int successes = Integer.parseInt(br.readLine());

            pw.println("Enter sample size: ");
            int sampleSize = Integer.parseInt(br.readLine());

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
                pw.println("Enter population size (infinity if not specified): ");
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

            // Large Counts condition
            if (sampleSize * nullProp > 10 && sampleSize * (1 - nullProp) > 10) {
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
        double sampleProp = (double) successes / sampleSize;
        double standardError = Math.sqrt(nullProp * (1 - nullProp) / sampleSize);
        double zScore = (sampleProp - nullProp) / standardError;

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

        DecimalFormat df = new DecimalFormat("#.##");

        // Conclude
            if (alternateHypothesisSign.equals("<")) {
                pw.println("Assuming the null hypothesis is true (p = " + nullProp + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p < " + nullProp + ") is true.");
                } else {
                    pw.println("Because the p value (" + pValue + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p < " + nullProp + ") is true.");
                }
            } else if (alternateHypothesisSign.equals(">")) {
                pw.println("Assuming the null hypothesis is true (p = " + df.format(nullProp) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or greater than the one observed in our sample purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p > " + df.format(nullProp) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p > " + df.format(nullProp) + ") is true.");
                }
            } else {
                pw.println("Assuming the null hypothesis is true (p = " + df.format(nullProp) + "), " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic as extreme or more extreme than the one observed in our sample in either direction, purely by chance.");
                if (pValue <= alphaLevel) {
                    pw.println("Because the p value (" + df.format(pValue) + ") is less than " + alphaLevel + ", we successfully reject the null and have convincing " + 
                    "evidence that the alternate hypothesis (p != " + df.format(alternateProp) + ") is true.");
                } else {
                    pw.println("Because the p value (" + df.format(pValue) + ") is not less than " + alphaLevel + ", we fail to reject the null and do not have " +
                    "convincing evidence that the alternate hypothesis (p != " + df.format(alternateProp) + ") is true.");
                }
            }

        br.close();
        pw.close();
    }
}