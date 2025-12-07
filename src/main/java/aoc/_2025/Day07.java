package aoc._2025;

import static aoc.Direction.DOWN;
import static aoc.Direction.LEFT;
import static aoc.Direction.RIGHT;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/07
 * 
 * @author Paul Cormier
 *
 */
public class Day07 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day07.class);

    private static final String INPUT_TXT = "input/Day07.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day07.txt";



    public static void main(String[] args) {

        var resultMessage = "The tachyon beam will be split {} times.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 21;
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
     * Given the beam splitter arrangement in the input lines, how many
     * resulting beams are there.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        var start = Coordinate.findCoordinates(lines, 'S');
        var splitters = Coordinate.findCoordinates(lines, '^');
        var rows = lines.size();
        var columns = lines.getFirst().length();

        log.atDebug()
           .setMessage("\n{}")
           .addArgument(() -> Coordinate.printMap(rows, columns, start, 'S', splitters, '^'))
           .log();

        Set<Coordinate> nextBeams = new HashSet<>();
        Set<Coordinate> allBeams = new HashSet<>();
        Set<Coordinate> encounteredSplitters = new HashSet<>();

        // Start from one space below the 'S'
        nextBeams.add(start.iterator().next().translate(DOWN, 1));

        while (!nextBeams.isEmpty()) {
            // Queue up the set of coordinates to process next
            Queue<Coordinate> currentBeams = new LinkedList<>(nextBeams);
            nextBeams.clear();

            while (!currentBeams.isEmpty()) {
                var beam = currentBeams.poll();

                // Record it as processed
                allBeams.add(beam);

                // Check below
                var below = beam.translate(DOWN, 1);

                // Whenever a splitter is encountered, add beams next to it
                // Also record that it has been hit
                if (splitters.contains(below)) {
                    nextBeams.add(below.translate(RIGHT, 1));
                    nextBeams.add(below.translate(LEFT, 1));
                    encounteredSplitters.add(below);
                } else if (below.getRow() <= rows) {
                    // The beam continues (until the bottom of the map)
                    nextBeams.add(below);
                }
            }

            log.atTrace()
               .setMessage("\n{}")
               .addArgument(() -> Coordinate.printMap(rows, columns, start, 'S', splitters, '^'))
               .log();
        }

        log.atDebug()
           .setMessage("\n{}")
           .addArgument(() -> Coordinate.printMap(rows, columns, allBeams, '|', splitters, '^'))
           .log();

        return encounteredSplitters.size();
    }



    /**
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }

}