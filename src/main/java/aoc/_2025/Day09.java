package aoc._2025;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.iterators.CartesianProductIterator;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.Range;
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

        // Parse the coordinates
        var coordinates = lines.stream()
                               .map(l -> Coordinate.of(Integer.valueOf(l.split(",")[0]), Integer.valueOf(l.split(",")[1])))
                               .toList();
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
           .addArgument(() -> Coordinate.printMap(rows, columns, Set.copyOf(coordinates)))
           .log();

        // Create some kind of map of the edges
        // The key is the row, the range is the columns it covers
        MultiValuedMap<Integer, Range<Integer>> horizontalEdges = new ArrayListValuedHashMap<>();
        var totalCoordinates = coordinates.size();
        IntStream.range(0, totalCoordinates - 1)
                 .forEach(i -> {
                     var first = coordinates.get(i);
                     var next = coordinates.get(i + 1);
                     if (first.getRow() == next.getRow())
                         horizontalEdges.put(first.getRow(), Range.of(first.getColumn(), next.getColumn()));
                 });
        // The key is the column, the range is the rows it covers
        MultiValuedMap<Integer, Range<Integer>> verticalEdges = new ArrayListValuedHashMap<>();
        IntStream.range(0, totalCoordinates - 1)
                 .forEach(i -> {
                     var first = coordinates.get(i);
                     var next = coordinates.get(i + 1);
                     if (first.getColumn() == next.getColumn())
                         verticalEdges.put(first.getColumn(), Range.of(first.getRow(), next.getRow()));
                 });

        // Add the last edge
        var firstCoordinate = coordinates.getFirst();
        var lastCoordinate = coordinates.getLast();
        if (firstCoordinate.getRow() == lastCoordinate.getRow())
            horizontalEdges.put(firstCoordinate.getRow(), Range.of(firstCoordinate.getColumn(), lastCoordinate.getColumn()));

        long maxArea = 0;

        var totalPairs = (totalCoordinates + 1) * totalCoordinates / 2. - totalCoordinates * 1.5;
        for (int i = 0; i < totalCoordinates; i++) {
            var first = coordinates.get(i);
            for (int j = i + 2; j < totalCoordinates; j++) {
                var currentPair = i * totalCoordinates + (totalCoordinates - j);
                if (currentPair % 1000 == 0)
                    log.info("{} out of {}", currentPair, (int) totalPairs);

                var next = coordinates.get(j);

                // Check if the area is bigger than the current max
                long area = (long) (Math.abs(first.getRow() - next.getRow()) + 1) *
                            (Math.abs(first.getColumn() - next.getColumn()) + 1);
                if (area <= maxArea)
                    continue;

                var minRow = Math.min(first.getRow(), next.getRow());
                var maxRow = Math.max(first.getRow(), next.getRow());
                var minColumn = Math.min(first.getColumn(), next.getColumn());
                var maxColumn = Math.max(first.getColumn(), next.getColumn());

                // Only bother with areas with no points (corners) inside of it.
                var rowRange = Range.of(minRow + 1, maxRow - 1);
                var columnRange = Range.of(minColumn + 1, maxColumn - 1);

                var containsNone = coordinates.stream()
                                              .noneMatch(c -> rowRange.contains(c.getRow()) && columnRange.contains(c.getColumn()));
                if (containsNone) {
                    // Check the other corners
                    var cornersInside = isPointInside(first.getRow(), next.getColumn(), horizontalEdges, verticalEdges) &&
                                        isPointInside(next.getRow(), first.getColumn(), horizontalEdges, verticalEdges);

                    if (!cornersInside)
                        continue;

                    // Check every point on the border to see if it's inside

                    // Start at the top-left and go clockwise
                    int row = minRow;
                    int column = minColumn;
                    boolean allInside = isPointInside(row, column, horizontalEdges, verticalEdges);
                    while (allInside && column < maxColumn) {
                        column++;
                        allInside &= isPointInside(row, column, horizontalEdges, verticalEdges);
                    }
                    while (allInside && row < maxRow) {
                        row++;
                        allInside &= isPointInside(row, column, horizontalEdges, verticalEdges);
                    }
                    while (allInside && column > minColumn) {
                        column--;
                        allInside &= isPointInside(row, column, horizontalEdges, verticalEdges);
                    }
                    while (allInside && row > minRow) {
                        row--;
                        allInside &= isPointInside(row, column, horizontalEdges, verticalEdges);
                    }

                    if (allInside)
                        // Save the area
                        maxArea = area;
                }
            }
        }

        return maxArea;
    }



    /**
     * Determine if a point is "inside" by checking that a vertical line upwards
     * passes through a positive, odd number of horizontal edges; or if it is on
     * one of the edges.
     * 
     * @param point The point to check.
     * @param horizontalEdges The map of all of the horizontal edges at each
     *            row.
     * @param verticalEdges The map of all of the vertical edges at each column.
     * @return <code>true</code> if a line from the point upwards passes through
     *         a positive and odd number of edges, or if the point is on an
     *         edge.
     */
    private static boolean isPointInside(int row, int column,
                                         MultiValuedMap<Integer, Range<Integer>> horizontalEdges,
                                         MultiValuedMap<Integer, Range<Integer>> verticalEdges) {
        if (horizontalEdges.get(row).stream().anyMatch(r -> r.contains(column)) ||
            verticalEdges.get(column).stream().anyMatch(r -> r.contains(row)))
            return true;

        var edgesPassed = horizontalEdges.entries()
                                         .stream()
                                         .filter(e -> e.getKey() <= row)
                                         .map(Entry::getValue)
                                         .filter(r -> r.contains(column))
                                         .count();
        edgesPassed += verticalEdges.get(column)
                                    .stream()
                                    .filter(r -> r.getMinimum() < row)
                                    .count();

        return edgesPassed % 2 == 1;
    }

}