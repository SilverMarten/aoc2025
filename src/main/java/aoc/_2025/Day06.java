package aoc._2025;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/06
 * 
 * @author Paul Cormier
 *
 */
public class Day06 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day06.class);

    private static final String INPUT_TXT = "input/Day06.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day06.txt";



    public static void main(String[] args) {

        var resultMessage = "The grand total found by adding together all of the answers to the individual problems is {}.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 4_277_556L;
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
        resultMessage = "The correct answers to the cephalopods' questions is {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 3_263_827L;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Given the cephalopods' math problems, find the sum of all of the answers.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        // Store the columns of numbers
        List<List<Long>> numberColumns = new ArrayList<>();
        // Initialize it to the right number of columns
        IntStream.range(0, lines.getFirst().split(" +").length)
                 .forEach(i -> numberColumns.add(new ArrayList<>()));

        // Store the operations
        final List<BinaryOperator<Long>> operations = new ArrayList<>();

        // Parse the input
        lines.forEach(l -> {
            var lineParts = Stream.of(l.split(" +"))
                                  .filter(StringUtils::isNotBlank)
                                  .toList();

            if (l.contains("*")) {
                // Store the operations to be performed
                lineParts.stream()
                         .map(o -> switch (o) {
                             case "*" -> (BinaryOperator<Long>) Math::multiplyExact;
                             case "+" -> (BinaryOperator<Long>) Math::addExact;
                             default -> throw new IllegalArgumentException("Unexpected value: " + o);
                         })
                         .forEach(operations::add);
            } else {
                // Parse the numbers into the right columns
                var lineNumbers = lineParts.stream()
                                           .map(Long::valueOf)
                                           .toList();
                IntStream.range(0, lineParts.size())
                         .forEach(i -> numberColumns.get(i).add(lineNumbers.get(i)));
            }
        });

        // Combine the values in each column, and sum them
        return IntStream.range(0, numberColumns.size())
                        .mapToLong(i -> numberColumns.get(i)
                                                     .stream()
                                                     .collect(Collectors.reducing(operations.get(i))).orElse(0L))
                        .sum();
    }



    /**
     * Reading the numbers from top to bottom, right to left, find the sum of
     * all of the answers.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        // Get the last line (which has the operations)
        var lineParts = Stream.of(lines.getLast().split(" +"))
                              .filter(StringUtils::isNotBlank)
                              .toList();

        // Store the operations to be performed
        List<BinaryOperator<Long>> operations = lineParts.stream()
                                                         .map(o -> switch (o) {
                                                             case "*" -> (BinaryOperator<Long>) Math::multiplyExact;
                                                             case "+" -> (BinaryOperator<Long>) Math::addExact;
                                                             default -> throw new IllegalArgumentException("Unexpected value: " + o);
                                                         })
                                                         .toList()
                                                         // Reversed because they are to be processed from right to left
                                                         .reversed();

        // Transpose the rows and columns
        var transposedLines = transpose(lines.subList(0, lines.size() - 1));

        log.atDebug()
           .setMessage(transposedLines::toString)
           .log();

        // Group the lists of numbers together
        AtomicInteger groupIndex = new AtomicInteger();
        List<List<Long>> numberGroups = new ArrayList<>();
        numberGroups.add(new ArrayList<>());

        transposedLines.forEach(l -> {
            // Setup for the next group
            if (StringUtils.isBlank(l)) {
                groupIndex.incrementAndGet();
                numberGroups.add(new ArrayList<>());
            } else {
                numberGroups.get(groupIndex.intValue()).add(Long.valueOf(l));
            }
        });

        log.atDebug()
           .setMessage(numberGroups::toString)
           .log();

        // Apply the operation and sum the results

        return IntStream.range(0, numberGroups.size())
                        .mapToLong(i -> numberGroups.get(i)
                                                    .stream()
                                                    .collect(Collectors.reducing(operations.get(i)))
                                                    .orElse(0L))
                        .peek(s -> log.debug(s + ""))
                        .sum();

    }



    /**
     * Given a list of lines of text (each with the same length) transpose them
     * into lines based on the columns, from right to left and top to bottom.
     * 
     * @param lines The lines to be transposed.
     * @return A new list of lines representing the columns of the original
     *         input, from right to left and top to bottom.
     */
    private static List<String> transpose(List<String> lines) {
        // Store the new lines to be appended to
        List<StringBuilder> transposedLines = new ArrayList<>();
        IntStream.range(0, lines.getFirst().length())
                 .forEach(i -> transposedLines.add(new StringBuilder()));

        lines.forEach(l -> IntStream.range(0, l.length())
                                    .forEach(i -> transposedLines.get(i).append(l.charAt(i))));

        return transposedLines.reversed()
                              .stream()
                              .map(StringBuilder::toString)
                              .map(String::trim)
                              .toList();
    }

}