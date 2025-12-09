package aoc._2025;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.iterators.CartesianProductIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import aoc.Coordinate;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/09
 * 
 * @author Paul Cormier
 *
 */
public class Day09 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day09.class);

    private static final String INPUT_TXT = "input/Day09.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day09.txt";



    public static void main(String[] args) {

        var resultMessage = "{} is the largest area of any rectangle you can make.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 50;
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
        resultMessage = "{} is the largest area of any rectangle you can make, using only red or green tiles.";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 24;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Given the list of coordinates, what is the largest area of rectangle you
     * can make?
     * 
     * @param lines The lines read from the input.
     * @return The area of the largest rectangle.
     */
    private static long part1(final List<String> lines) {

        var coordinates = Coordinate.parseCoordinates(lines);
        var rows = coordinates.stream()
                              .mapToInt(Coordinate::getRow)
                              .max()
                              .getAsInt();
        var columns = coordinates.stream()
                                 .mapToInt(Coordinate::getColumn)
                                 .max()
                                 .getAsInt();

        log.atDebug()
           .setMessage("\n{}")
           .addArgument(() -> Coordinate.printMap(rows, columns, coordinates))
           .log();

        Map<Long, Pair<Coordinate, Coordinate>> areas = new HashMap<>();
        CartesianProductIterator<Coordinate> pairs = new CartesianProductIterator<>(coordinates, coordinates);
        pairs.forEachRemaining(p -> areas.put((long) (Math.abs(p.getFirst().getRow() - p.getLast().getRow()) + 1) *
                                              (Math.abs(p.getFirst().getColumn() - p.getLast().getColumn()) + 1),
                                              Pair.of(p.getFirst(), p.getLast())));

        log.atDebug()
           .setMessage("Areas:\n{}")
           .addArgument(() -> areas.entrySet()
                                   .stream()
                                   .sorted(Comparator.comparing(Entry::getKey, Comparator.reverseOrder()))
                                   .map(e -> "%d (%s and %s)".formatted(e.getKey(),
                                                                        e.getValue().getLeft(),
                                                                        e.getValue().getRight()))
                                   .collect(Collectors.joining("\n")))
           .log();

        return areas.keySet().stream().mapToLong(Long::longValue).max().getAsLong();
        // 2147314224 is too low.
    }



    /**
     * Given the list of coordinates, define an area with, then within that area
     * what is the largest area of rectangle you can make?
     * 
     * @param lines The lines read from the input.
     * @return The area of the largest rectangle, made of only red or green
     *         tiles.
     */
    private static long part2(final List<String> lines) {

        var coordinates = Coordinate.parseCoordinates(lines);
        var rows = coordinates.stream()
                              .mapToInt(Coordinate::getRow)
                              .max()
                              .getAsInt();
        var columns = coordinates.stream()
                                 .mapToInt(Coordinate::getColumn)
                                 .max()
                                 .getAsInt();

        log.atDebug()
           .setMessage("\n{}")
           .addArgument(() -> Coordinate.printMap(rows, columns, coordinates))
           .log();

        Map<Long, Pair<Coordinate, Coordinate>> areas = new HashMap<>();
        CartesianProductIterator<Coordinate> pairs = new CartesianProductIterator<>(coordinates, coordinates);

        pairs.forEachRemaining(p -> {
            var first = p.getFirst();
            var last = p.getLast();
            var firstLast = Coordinate.of(first.getRow(), last.getColumn());
            var lastFirst = Coordinate.of(first.getColumn(), last.getRow());

            // What if: at least one of the opposite corners has to be one of the bounding coordinates?
            // Nope. But maybe it will cut down on the search space.
            if (coordinates.contains(firstLast) || coordinates.contains(lastFirst))
                areas.put((long) (Math.abs(first.getRow() - last.getRow()) + 1) *
                          (Math.abs(first.getColumn() - last.getColumn()) + 1),
                          Pair.of(first, last));
        });

        log.atDebug()
           .setMessage("Areas:\n{}")
           .addArgument(() -> areas.entrySet()
                                   .stream()
                                   .sorted(Comparator.comparing(Entry::getKey, Comparator.reverseOrder()))
                                   .map(e -> "%d (%s and %s)".formatted(e.getKey(),
                                                                        e.getValue().getLeft(),
                                                                        e.getValue().getRight()))
                                   .collect(Collectors.joining("\n")))
           .log();

        return areas.keySet().stream().mapToLong(Long::longValue).max().getAsLong();
    }

}