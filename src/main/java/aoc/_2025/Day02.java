package aoc._2025;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/02
 * 
 * @author Paul Cormier
 *
 */
public class Day02 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day02.class);

    private static final String INPUT_TXT = "input/Day02.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day02.txt";



    public static void main(String[] args) {

        var resultMessage = "The sum of the invalid IDs is {}";

        log.info("Part 1:");
        log.setLevel(Level.TRACE);

        // Read the test file
        List<String> testRanges = Arrays.asList(FileUtils.readFile(TEST_INPUT_TXT)
                                                         .getFirst()
                                                         .split(","));

        var expectedTestResult = 1_227_775_554L;
        var testResult = part1(testRanges);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> ranges = Arrays.asList(FileUtils.readFile(INPUT_TXT)
                                                     .getFirst()
                                                     .split(","));

        log.info(resultMessage, part1(ranges));

        // PART 2
        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 4_174_379_265L;
        testResult = part2(testRanges);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(ranges));
    }



    /**
     * Given the ranges, find values that are made up only of some sequence of
     * digits repeated twice.
     * 
     * @param ranges The ranges read from the input.
     * 
     * @return The sum of all "invalid" IDs.
     */
    private static long part1(final List<String> ranges) {

        return ranges.stream()
                     .map(r -> Range.of(Long.valueOf(r.split("-")[0]),
                                        Long.valueOf(r.split("-")[1])))
                     .map(Day02::findDoubledIDs)
                     .peek(l -> {
                         if (!l.isEmpty())
                             log.debug("Invalid IDs {}", l);
                     })
                     .flatMap(Collection::stream)
                     .mapToLong(Long::longValue)
                     .sum();

        // 19219508517 is too low
    }



    /**
     * Given a particular range, find IDs in the range which are exactly a
     * doubled sequence of digits.
     * 
     * @param range The range in which to search.
     * 
     * @return A list of IDs which are within the given range, and are made up
     *         of a sequence of digits which is repeated once.
     */
    private static List<Long> findDoubledIDs(Range<Long> range) {
        return findRepeatedIDs(range, 2);
    }



    /**
     * Given a particular range, find IDs in the range which are exactly a
     * sequence of digits repeated a certain number of times.
     * 
     * @param range The range in which to search.
     * @param repeats The number of times the sequence is to be repeated.
     * 
     * @return A list of IDs which are within the given range, and are made up
     *         of a sequence of digits which is repeated once.
     */
    private static List<Long> findRepeatedIDs(Range<Long> range, int repeats) {

        log.debug("Range {}", range);

        // Find the range of the first half
        var max = range.getMaximum().toString();
        // Pad the min to the same number of digits as the max
        var min = StringUtils.leftPad(range.getMinimum().toString(), max.length(), '0');

        Range<Integer> halfRange;
        try {
            // Gradually trim off the right digits (least significant values) of each number in the range.
            var digitsToTrim = max.length() / repeats * (repeats - 1);
            halfRange = Range.of(Integer.valueOf(min.substring(0, max.length() - digitsToTrim)),
                                 Integer.valueOf(max.substring(0, min.length() - digitsToTrim)));
        } catch (NumberFormatException nfe) {
            log.error("Could not create a range from {} ({}-{})", range, min, max, nfe);
            return Collections.emptyList();
        }
        log.trace("Checking {}, {} times", halfRange, repeats);

        return IntStream.rangeClosed(halfRange.getMinimum(), halfRange.getMaximum())
                        .mapToLong(i -> Long.valueOf("%1$d".repeat(repeats).formatted(i)))
                        .filter(range::contains)
                        .boxed()
                        .toList();
    }



    /**
     * Given a particular range, find IDs in the range which are exactly a
     * repeated sequence of digits.
     * 
     * @param ranges The lines read from the input.
     * 
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> ranges) {

        return ranges.stream()
                     .map(r -> Range.of(Long.valueOf(r.split("-")[0]),
                                        Long.valueOf(r.split("-")[1])))
                     // Try all the multiples from 2 to the length of the longest string
                     .map(r -> IntStream.rangeClosed(2, r.getMaximum().toString().length())
                                        // Skip anything that isn't a multiple of one of the string's lengths
                                        .filter(i -> r.getMaximum().toString().length() % i == 0 ||
                                                     r.getMinimum().toString().length() % i == 0)
                                        .mapToObj(i -> findRepeatedIDs(r, i))
                                        .flatMap(Collection::stream)
                                        .distinct()
                                        .toList())
                     .peek(l -> {
                         if (!l.isEmpty())
                             log.debug("Invalid IDs {}", l);
                     })
                     .flatMap(Collection::stream)
                     .mapToLong(Long::longValue)
                     .sum();
    }

}