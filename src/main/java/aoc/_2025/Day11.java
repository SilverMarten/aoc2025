package aoc._2025;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
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
        resultMessage = "{} different paths lead from \"svr\" to \"out\", and pass through \"fft\" and \"dac\".";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        var testLines2 = """
            svr: aaa bbb
            aaa: fft
            fft: ccc
            bbb: tty
            tty: ccc
            ccc: ddd eee
            ddd: hub
            hub: fff
            eee: dac
            dac: fff
            fff: ggg hhh
            ggg: out
            hhh: out
            """.lines()
               .toList();

        expectedTestResult = 2;
        testResult = part2(testLines2);

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

        return countPathsBetweenNodes(nodeMap, "you", "out");
    }



    /**
     * Given a map of nodes, count the number of paths from a given node to
     * another node.
     * 
     * @param nodeMap The map of nodes and their outputs.
     * @param from The node to begin from.
     * @param to The node to end at.
     * @return The number of paths from the given node to the other node.
     */
    private static int countPathsBetweenNodes(Map<String, List<String>> nodeMap, String from, String to) {
        return countPathsBetweenNodes(nodeMap, from, to, Set.of(to));
    }



    /**
     * Given a map of nodes, count the number of paths from a given node to
     * another node.
     * 
     * @param nodeMap The map of nodes and their outputs.
     * @param from The node to begin from.
     * @param to The node to end at.
     * @param avoiding The set of nodes that a path should not cross.
     * 
     * @return The number of paths from the given node to the other node.
     */
    private static int countPathsBetweenNodes(Map<String, List<String>> nodeMap, String from, String to, Set<String> avoiding) {

        // You could walk the graph (starting at "you") assuming there are no cycles 
        // (this happens to be true in the example)
        // and track how many ways there are to arrive at a node

        Map<String, Integer> visitsToNode = new HashMap<>();
        visitsToNode.put(from, 1);

        Queue<String> nodesToVisit = new ArrayDeque<>();
        nodesToVisit.add(from);

        // Visit each node in the queue, increment its visit count, and queue its outputs
        while (!nodesToVisit.isEmpty()) {
            String node = nodesToVisit.poll();
            var outputs = nodeMap.get(node);
            if (outputs != null && !avoiding.contains(node)) {
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

        return visitsToNode.getOrDefault(to, 0);
    }



    /**
     * Given the "from" and "to" nodes, find all the paths from "svr" to "out"
     * that pass through both "fft" and "dac".
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

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

        // "svr" to "out" is too complex (runs out of memory)

        // Determine all of the child nodes that a single node may reach
        Map<String, Set<String>> childNodeMap = computeChildNodeMap(nodeMap);

        // Use the child node map to reduce the search space of the counting of paths

        // "dac" does not connect to "fft"
        if (countPathsBetweenNodes(nodeMap, "dac", "fft") != 0) {
            log.error("\"dac\" appears to connect to \"fft\"");
            return -1;
        }

        // The path must go from "svr" to "fft", then to "dac", and finally "out" 
        long paths = 1;

        var irrelevantChildNodes = childNodeMap.entrySet()
                                               .stream()
                                               .filter(e -> !e.getValue().containsAll(Arrays.asList("fft", "dac", "out")))
                                               .map(Entry::getKey)
                                               .collect(Collectors.toSet());
        paths = countPathsBetweenNodes(nodeMap, "svr", "fft", irrelevantChildNodes);

        irrelevantChildNodes = childNodeMap.entrySet()
                                           .stream()
                                           .filter(e -> !e.getValue().containsAll(Arrays.asList("dac", "out")))
                                           .map(Entry::getKey)
                                           .collect(Collectors.toSet());
        paths *= countPathsBetweenNodes(nodeMap, "fft", "dac", irrelevantChildNodes);

        irrelevantChildNodes = childNodeMap.entrySet()
                                           .stream()
                                           .filter(e -> !e.getValue().contains("out"))
                                           .map(Entry::getKey)
                                           .collect(Collectors.toSet());
        paths *= countPathsBetweenNodes(nodeMap, "dac", "out", irrelevantChildNodes);

        return paths;
    }



    /**
     * Create a map listing all child nodes of the known nodes.
     * 
     * @param nodeMap The known nodes.
     * @return A map representing all of the eventual child nodes of each node
     *         in the <code>nodeMap</code>.
     */
    private static Map<String, Set<String>> computeChildNodeMap(Map<String, List<String>> nodeMap) {
        Map<String, Set<String>> childNodeMap = new HashMap<>();
        Queue<String> nodesToCheck = new ArrayDeque<>();

        List<String> nextNodesToCheck = new ArrayList<>();
        List<String> nextChildNodesToCheck = new ArrayList<>();
        nextNodesToCheck.add("svr");
        while (!nextNodesToCheck.isEmpty()) {
            nodesToCheck.addAll(nextChildNodesToCheck);
            nodesToCheck.addAll(nextNodesToCheck);
            nextChildNodesToCheck.clear();
            nextNodesToCheck.clear();
            while (!nodesToCheck.isEmpty()) {
                var node = nodesToCheck.poll();
                if (childNodeMap.containsKey(node))
                    continue;

                var childNodes = nodeMap.get(node);
                // If this is a leaf node
                if (childNodes == null || childNodes.isEmpty())
                    childNodeMap.put(node, Collections.emptySet());
                else // If all of the child nodes are known
                if (childNodes.stream().allMatch(childNodeMap::containsKey)) {
                    childNodeMap.put(node, Stream.concat(childNodes.stream(), childNodes.stream()
                                                                                        .map(childNodeMap::get)
                                                                                        .flatMap(Collection::stream))
                                                 .collect(Collectors.toSet()));
                } else {
                    // Re-queque for later
                    nextNodesToCheck.add(node);
                    // Check the child nodes 
                    childNodes.forEach(n -> {
                        if (!nextChildNodesToCheck.contains(n))
                            nextChildNodesToCheck.add(n);
                    });
                }
            }
        }

        log.atDebug()
           .setMessage("Child nodes:\n{}")
           .addArgument(() -> childNodeMap.entrySet()
                                          .stream()
                                          .sorted(Comparator.comparing(Entry::getKey))
                                          .map(e -> e.getKey() + ": " + e.getValue().stream().collect(Collectors.joining(" ")))
                                          .collect(Collectors.joining("\n")))
           .log();
        return childNodeMap;
    }

}