package aoc._2025;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;

import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/11
 * 
 * @author Paul Cormier
 *
 */
public class Day11 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day11.class);

    private static final String INPUT_TXT = "input/Day11.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day11.txt";



    public static void main(String[] args) {

        var resultMessage = "{} different paths lead from you to out.";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 5;
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
     * Given the "from" and "to" nodes, find all the paths from "you" to "out".
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines) {

        // Parse nodes
        Map<String, List<String>> nodeMap = new HashMap<>();
        lines.stream()
             .forEach(l -> nodeMap.computeIfAbsent(l.substring(0, 3), k -> new ArrayList<>())
                                  .addAll(Stream.of(l.substring(5).split(" ")).toList()));

        log.atDebug()
           .setMessage("Nodes:\n{}")
           .addArgument(() -> nodeMap.entrySet()
                                     .stream()
                                     .sorted(Comparator.comparing(Entry::getKey))
                                     .map(e -> e.getKey() + ": " + e.getValue().stream().collect(Collectors.joining(" ")))
                                     .collect(Collectors.joining("\n")))
           .log();

        // You could walk the graph (starting at "you") assuming there are no cycles 
        // (this happens to be true in the example)
        // and track how many ways there are to arrive at a node

        Map<String, Integer> visitsToNode = new HashMap<>();
        visitsToNode.put("you", 1);
        
        Queue<String> nodesToVisit = new ArrayDeque<>();
        nodesToVisit.add("you");

        // Visit each node in the queue, increment its visit count, and queue its outputs
        while (!nodesToVisit.isEmpty()) {
            String node = nodesToVisit.poll();
            var outputs = nodeMap.get(node);
            if (outputs != null) {
                outputs.forEach(n -> visitsToNode.merge(n, 1, Math::addExact));
                nodesToVisit.addAll(outputs);
            }
        }

        log.atDebug()
           .setMessage("Visits to each node:\n{}")
           .addArgument(() -> visitsToNode.entrySet()
                                          .stream()
                                          .sorted(Comparator.comparing(Entry::getKey))
                                          .map(e -> e.getKey() + ": " + e.getValue())
                                          .collect(Collectors.joining("\n")))
           .log();


        return visitsToNode.get("out");
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