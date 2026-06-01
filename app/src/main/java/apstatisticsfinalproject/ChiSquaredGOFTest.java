package apstatisticsfinalproject;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ChiSquaredGOFTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to the chi squared goodness of fit test calculator!");

        // Choose
            BufferedReader screenReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter fileWriter = new PrintWriter(new File("gof.out"));
            PrintStream screenWriter = new PrintStream(System.out);

            int sampleSize = Utilities.readInteger(screenReader, screenWriter, "Enter sample size: ", 100);
            fileWriter.println("sample size = " + sampleSize);
            fileWriter.println();

            int categories = Utilities.readInteger(screenReader, screenWriter, "Enter number of categories: ", 5);
            String categoricalVariable = Utilities.readString(screenReader, screenWriter, "Enter name of categorical variable: ");

            String allProps = Utilities.readString(screenReader, screenWriter, "Enter expected proportions (separated by spaces): ");
            double[] expectedProportions = Arrays.stream(allProps.split(" ")).mapToDouble(Double::parseDouble).toArray();

            String observed = Utilities.readString(screenReader, screenWriter, "Enter observed counts (separated by spaces): ");
            double[] observedCounts = Arrays.stream(observed.split(" ")).mapToDouble(Double::parseDouble).toArray();

            double[] expectedCounts = new double[categories];
            for (int i = 0; i < categories; i++) {
                expectedCounts[i] = expectedProportions[i] * sampleSize;
            }

            fileWriter.println("Null hypothesis (H_0): The true distribution of (" + categoricalVariable + ") is the same as the expected distribution.");
            fileWriter.println();
            fileWriter.println("Alternative hypothesis (H_A): The true distribution of the categorical variable (" + categoricalVariable + ") is not the same as the expected distribution.");
            fileWriter.println();

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
                String line = Utilities.readString(screenReader, screenWriter, "Enter population size for the sample (leave blank if not specified): ");
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
            int totalExpectedCount = categories;
            int expectedCountMoreThan5 = 0;

            for (int i = 0; i < categories; i++) {
                if (expectedCounts[i] >= 5) {
                    expectedCountMoreThan5++;
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
            for (int i = 0; i < categories; i++) {
                chiSquaredStat += (Math.pow(observedCounts[i] - expectedCounts[i], 2) / expectedCounts[i]);
            }

            chiSquaredStat *= 1000.0;
            chiSquaredStat /= 1000.0;

            double degreesOfFreedom = categories - 1;
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
            msg = "Assuming the null hypothesis is true (the true distribution of " + categoricalVariable + " is the same as the expected distribution)" + ", " + "there is approximately a " + df.format(pValue) + " probability of obtaining a " +
                "sample statistic equal to or less than the one observed in our sample purely by chance.";
            Utilities.writeMessage(msg, screenWriter, fileWriter);

            if (pValue < alphaLevel) {
                msg = "Since the p-value is less than the alpha level, we reject the null hypothesis and conclude that the alternate hypothesis (" + "the true distribution of " + categoricalVariable + " is different from the expected distribution" + ") is true.";
            } else {
                msg = "Since the p-value is greater than or equal to the alpha level, we fail to reject the null hypothesis and do not have enough evidence to conclude that the alternate hypothesis (" + "the true distribution of " + categoricalVariable + " is different from the expected distribution" + ") is true.";
            }
            Utilities.writeLastMessage(msg, screenWriter, fileWriter);

        screenReader.close();
        screenWriter.close();
        fileWriter.close();
    }
}