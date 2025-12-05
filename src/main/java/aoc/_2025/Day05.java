package aoc._2025;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
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
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        return -1;
    }

}