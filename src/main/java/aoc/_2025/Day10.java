package aoc._2025;

import java.util.List;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/10
 * 
 * @author Paul Cormier
 *
 */
public class Day10 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day10.class);

    private static final String INPUT_TXT = "input/Day10.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day10.txt";



    public static void main(String[] args) {

        var resultMessage = "{} is the fewest button presses required to correctly configure the indicator lights on all of the machines.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 7;
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
        resultMessage = "{}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 1_234_567_890;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Given the machine configurations on each line, how many button presses
     * are needed to reach the desired state for all machines?
     * 
     * @param lines The lines read from the input.
     * @return The total number of button presses to get to the desired state
     *         for all machines.
     */
    private static long part1(final List<String> lines) {

        // Parse the machine configurations
        List<Machine> machines = lines.stream()
                                      .map(Machine::fromLine)
                                      .toList();

        // Run the machine, and press buttons until the desired state is reached
        return machines.stream()
                       .mapToInt(Day10::runToStart)
                       .sum();

    }



    /**
     * Given a {@link Machine}, figure out how few button presses are needed to
     * get it to its starting state.
     * 
     * @param machine The configured {@link Machine} to start.
     * 
     * @return The minimum number of button presses needed to get it to start.
     */
    private static int runToStart(Machine machine) {

        return 0;
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }



    private static class Machine {

        /**
         * Given a line containing a machine configuration, a single indicator
         * light diagram in [square brackets], one or more button wiring
         * schematics in (parentheses), and joltage requirements in {curly
         * braces}, create a new {@link Machine} configured with these
         * parameters.
         * 
         * @param line The configuration string for the {@link Machine}.
         * @return A new {@link Machine}, configured according to the input
         *         string.
         */
        public static Machine fromLine(String line) {
            return new Machine();
        }



        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return super.toString();
        }
    }

}