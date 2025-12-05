package aoc._2025;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import aoc.RangeUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/05
 * 
 * @author Paul Cormier
 *
 */
public class Day05 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day05.class);

    private static final String INPUT_TXT = "input/Day05.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day05.txt";



    public static void main(String[] args) {

        var resultMessage = "{} of the available ingredient IDs are fresh.";

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
        resultMessage = "{} ingredient IDs are considered to be fresh according to the fresh ingredient ID ranges.";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 14;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Given freshness ranges and ingredient IDs, how many ingredients are
     * fresh?
     * 
     * @param lines The lines read from the input.
     * @return The number of fresh ingredients.
     */
    private static long part1(final List<String> lines) {

        Set<Range<Long>> freshnessRanges = new HashSet<>();
        Set<Long> ingredientIds = new HashSet<>();

        // Parse the ingredient IDs and ranges
        lines.stream()
             .filter(StringUtils::isNotBlank)
             .forEach(l -> {
                 if (l.contains("-"))
                     freshnessRanges.add(Range.of(Long.valueOf(l.split("-")[0]), Long.valueOf(l.split("-")[1])));
                 else
                     ingredientIds.add(Long.valueOf(l));
             });

        // Check each ingredient ID
        return ingredientIds.stream()
                            .filter(id -> IterableUtils.matchesAny(freshnessRanges, r -> r.contains(id)))
                            .count();
    }



    /**
     * Given freshness ranges only, how many ingredient ID would be considered
     * fresh?
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {
        // Parse the freshness ranges
        Queue<Range<Long>> freshnessRanges = lines.stream()
                                                  .filter(l -> l.contains("-"))
                                                  .map(l -> Range.of(Long.valueOf(l.split("-")[0]), Long.valueOf(l.split("-")[1])))
                                                  .collect(Collectors.toCollection(ArrayDeque::new));

        log.debug("Freshness ranges:\n{}", freshnessRanges);

        // Consolidate the ranges
        Set<Range<Long>> consolidatedRanges = new HashSet<>();

        while (!freshnessRanges.isEmpty()) {
            Range<Long> range = freshnessRanges.poll();
            // Find overlapping ranges
            var overlappingRanges = freshnessRanges.stream()
                                                   .filter(range::isOverlappedBy)
                                                   .toList();

            // If it doesn't overlap anything, then it it consolidated.
            if (overlappingRanges.isEmpty()) {
                consolidatedRanges.add(range);
            } else {
                // Otherwise, create the new ranges, and put them back in the queue.
                freshnessRanges.removeAll(overlappingRanges);
                overlappingRanges.stream()
                                 .map(r -> RangeUtils.union(range, r))
                                 .forEach(freshnessRanges::add);
            }
        }

        log.debug("Consolidated ranges:\n{}", consolidatedRanges);

        return (long) consolidatedRanges.stream()
                                        .mapToDouble(RangeUtils::size)
                                        .sum();
    }

}