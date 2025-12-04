package aoc._2025;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/04
 * 
 * @author Paul Cormier
 *
 */
public class Day04 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day04.class);

    private static final String INPUT_TXT = "input/Day04.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day04.txt";



    public static void main(String[] args) {

        var resultMessage = "{} rolls of paper can be accessed by a forklift.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 13;
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
        resultMessage = "{} rolls of paper in total can be removed by the Elves and their forklifts.";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 43;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * How many rolls of paper have fewer than 4 adjacent rolls of paper.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        var rolls = Coordinate.findCoordinates(lines, '@');

        return rolls.stream()
                    .filter(r -> SetUtils.intersection(r.findAdjacent(), rolls).size() < 4)
                    .count();

    }



    /**
     * How many rolls of paper can be removed in total.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static int part2(final List<String> lines) {

        var rolls = Coordinate.findCoordinates(lines, '@');
        int startingRolls = rolls.size();

        final List<Coordinate> rollsToRemove = new ArrayList<>();
        do {
            rollsToRemove.clear();
            rolls.stream()
                 .filter(r -> SetUtils.intersection(r.findAdjacent(), rolls).size() < 4)
                 .forEach(rollsToRemove::add);

            rolls.removeAll(rollsToRemove);

            log.atDebug().setMessage("Remove {} rolls of paper:\n{}\n")
               .addArgument(rollsToRemove.size())
               .addArgument(() -> Coordinate.printMap(lines.size(), lines.size(), rolls, '@', Set.copyOf(rollsToRemove), 'x'))
               .log();

        } while (!rollsToRemove.isEmpty());

        return startingRolls - rolls.size();

    }

}