package aoc._2025;

import java.util.List;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/03
 * 
 * @author Paul Cormier
 *
 */
public class Day03 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day03.class);

    private static final String INPUT_TXT = "input/Day03.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day03.txt";



    public static void main(String[] args) {

        var resultMessage = "The total output joltage is {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 357L;
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info(resultMessage, part1(lines));  // 17281

        // PART 2
        resultMessage = "The total output joltage is {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 3_121_910_778_619L;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Find the largest two-digit number that can be read from left to right in
     * each input line, and return the sum.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        return lines.stream()
                    .mapToLong(l -> Day03.findLargestJoltage(l, 2))
                    .sum();

    }



    /**
     * Scan a line of digits and find the largest n-digit number that can be
     * read from left to right.
     * 
     * @param line The line of digits to scan.
     * @return The value of the largest n-digit number that can be read from
     *         left to right.
     */
    private static long findLargestJoltage(String line, int length) {

        StringBuilder joltageString = new StringBuilder();

        String subString = line;

        for (int n = length; n > 0; n--) {
            char foundDigit = '?';

            for (int i = 9; i > 0; i--) {
                int index = subString.indexOf(i + '0');
                // Stop before the number of digits left to find
                if (index >= 0 && index < subString.length() - (n - 1)) {
                    foundDigit = subString.charAt(index);
                    break;
                }
            }
            // Add the found digit to the string
            joltageString.append(foundDigit);

            // Start from that digit's position and continue for the rest of the line
            // Except for the last digit
            if (n > 1)
                subString = subString.substring(subString.indexOf(foundDigit) + 1);
        }

        log.debug("In {} the largest {}-digit joltage possible is {}.", line, length, joltageString);

        return Long.valueOf(joltageString.toString());
    }



    /**
     * Scan a line of digits and find the largest twelve-digit number that can
     * be read from left to right.
     * 
     * @param lines The lines read from the input.
     * @return The value of the largest twelve-digit number that can be read
     *         from left to right.
     */
    private static long part2(final List<String> lines) {

        return lines.stream()
                    .mapToLong(l -> Day03.findLargestJoltage(l, 12))
                    .sum();

    }

}