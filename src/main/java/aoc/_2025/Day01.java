package aoc._2025;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/01
 * 
 * @author Paul Cormier
 *
 */
public class Day01 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day01.class);

    private static final String INPUT_TXT = "input/Day01.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day01.txt";



    public static void main(String[] args) {

        var resultMessage = "The actual password to open the door is {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 3;
        var testResult = part1(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);

        log.info(resultMessage, part1(lines));

        // PART 2
        resultMessage = "The password to open the door is {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 6;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Starting from 50, follow the turns given in the lines, and count the
     * number of times it stops on 0.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        AtomicInteger dial = new AtomicInteger(50);

        return lines.stream()
                    .map(l -> l.replace('R', '+').replace('L', '-'))
                    .mapToInt(Integer::valueOf)
                    .map(i -> dial.accumulateAndGet(i, (x, y) -> (x + y + 1000) % 100))
                    .peek(i -> log.debug("The dial is pointing at {}", i))
                    .filter(i -> i == 0)
                    .count();

    }



    /**
     * Starting from 50, follow the turns given in the lines, and count the
     * number of times it passes 0.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        AtomicInteger dial = new AtomicInteger(50);
        AtomicInteger count = new AtomicInteger();

        lines.stream()
             .map(l -> l.replace('R', '+').replace('L', '-'))
             .mapToInt(Integer::valueOf)
             .forEach(i -> {

                 // Turn the dial
                 int pointTo = dial.addAndGet(i);

                 log.debug("The dial is rotated {} to point to {} ({})", i, (pointTo + 1000) % 100, pointTo);

                 // If the new value is the same as the change, then it started at 0; don't count it.
                 // If the new value went past 100 or 0, count how many times.
                 if (i != pointTo && (pointTo >= 100 || pointTo <= 0)) {
                     var delta = Math.abs(pointTo) / 100 + i < 0 ? 1 : 0;
                     count.addAndGet(delta);
                     log.debug("  during this rotation, it points at 0, {} times.", delta);
                 }

                 // Reset the modulus
                 dial.getAndSet((pointTo + 1000) % 100);
             });

        // 2421 is too low...

        return count.longValue();
    }

}