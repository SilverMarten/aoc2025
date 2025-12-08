package aoc._2025;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.iterators.CartesianProductIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import aoc.Coordinate3D;
import aoc.FileUtils;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * https://adventofcode.com/2025/day/08
 * 
 * @author Paul Cormier
 *
 */
public class Day08 {

    private static final Logger log = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(Day08.class);

    private static final String INPUT_TXT = "input/Day08.txt";

    private static final String TEST_INPUT_TXT = "testInput/Day08.txt";



    public static void main(String[] args) {

        var resultMessage = "If you multiply together the sizes of the three largest circuits, you get {}";

        log.info("Part 1:");
        log.setLevel(Level.DEBUG);

        // Read the test file
        List<String> testLines = FileUtils.readFile(TEST_INPUT_TXT);

        var expectedTestResult = 40;
        var testResult = part1(testLines, 10);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        // Read the real file
        List<String> lines = FileUtils.readFile(INPUT_TXT);
        log.info(resultMessage, part1(lines, 1000));

        // PART 2
        resultMessage = "The product of the x coordinates of the last two junction boxes to be connected is {}";

        log.info("Part 2:");
        log.setLevel(Level.DEBUG);

        expectedTestResult = 25_272;
        testResult = part2(testLines);

        log.info("Should be {}", expectedTestResult);
        log.info(resultMessage, testResult);

        if (testResult != expectedTestResult)
            log.error("The test result doesn't match the expected value.");

        log.setLevel(Level.INFO);

        log.info(resultMessage, part2(lines));
    }



    /**
     * Given the coordinates of the junction boxes, connect the 10 shortest
     * connections. Then find the three biggest groups, and return the product
     * of their sizes.
     * 
     * @param lines The lines read from the input.
     * @param connections The number of connections to make.
     * 
     * @return The value calculated for part 1.
     */
    private static long part1(final List<String> lines, int connections) {

        // Parse the coordinates into JunctionBoxes
        var junctionBoxes = lines.stream()
                                 .map(l -> l.split(","))
                                 .map(c -> new Coordinate3D(Integer.valueOf(c[0]), Integer.valueOf(c[1]), Integer.valueOf(c[2])))
                                 .map(JunctionBox::new)
                                 .toList();

        // Determine the distance between every pair
        Map<Double, Pair<JunctionBox, JunctionBox>> distances = new HashMap<>();
        CartesianProductIterator<JunctionBox> pairs = new CartesianProductIterator<>(junctionBoxes, junctionBoxes);
        // I realise this is going to do twice as much work as necessary...
        pairs.forEachRemaining(p -> {
            var distance = p.getFirst().getPosition().distanceTo(p.getLast().getPosition());
            if (distance > 0) {
                var newPair = Pair.of(p.getFirst(), p.getLast());
                var existingPair = distances.put(distance, newPair);
                if (existingPair != null && !(newPair.equals(existingPair) || Pair.of(p.getLast(), p.getFirst()).equals(existingPair)))
                    throw new IllegalArgumentException("There was already a pair of coordinates %.5f apart (%s, %s).".formatted(distance,
                                                                                                                                existingPair.getLeft()
                                                                                                                                            .getPosition(),
                                                                                                                                existingPair.getRight()
                                                                                                                                            .getPosition()));
            }
        });

        // Connect the n-closest
        distances.entrySet()
                 .stream()
                 .sorted(Comparator.comparing(Entry::getKey))
                 .limit(connections)
                 .forEach(e -> e.getValue().getLeft().connect(e.getValue().getRight()));

        // Find the 3 biggest resulting circuits
        log.debug("Resulting circuits:");
        return junctionBoxes.stream()
                            .map(JunctionBox::getCircuit)
                            .distinct()
                            .sorted(Comparator.comparing(Set::size, Comparator.reverseOrder()))
                            .peek(c -> log.debug("{} {}", c.size(), c.stream().map(JunctionBox::getPosition).toList()))
                            .limit(3)
                            .mapToLong(Set::size)
                            .reduce(Math::multiplyExact)
                            .getAsLong();

    }



    /**
     * Connect the junction boxes, in order from closest to farthest, until they
     * form a single circuit. Then find the product of the x coordinates of the
     * last two to be connected.
     * 
     * @param lines The lines read from the input.
     * @return The value calculated for part 2.
     */
    private static long part2(final List<String> lines) {

        // Parse the coordinates into JunctionBoxes
        var junctionBoxes = lines.stream()
                                 .map(l -> l.split(","))
                                 .map(c -> new Coordinate3D(Integer.valueOf(c[0]), Integer.valueOf(c[1]), Integer.valueOf(c[2])))
                                 .map(JunctionBox::new)
                                 .toList();

        // Determine the distance between every pair
        Map<Double, Pair<JunctionBox, JunctionBox>> distances = new HashMap<>();
        CartesianProductIterator<JunctionBox> pairs = new CartesianProductIterator<>(junctionBoxes, junctionBoxes);
        // I realise this is going to do twice as much work as necessary...
        pairs.forEachRemaining(p -> {
            var distance = p.getFirst().getPosition().distanceTo(p.getLast().getPosition());
            if (distance > 0) {
                var newPair = Pair.of(p.getFirst(), p.getLast());
                var existingPair = distances.put(distance, newPair);
                if (existingPair != null && !(newPair.equals(existingPair) || Pair.of(p.getLast(), p.getFirst()).equals(existingPair)))
                    throw new IllegalArgumentException("There was already a pair of coordinates %.5f apart (%s, %s).".formatted(distance,
                                                                                                                                existingPair.getLeft()
                                                                                                                                            .getPosition(),
                                                                                                                                existingPair.getRight()
                                                                                                                                            .getPosition()));
            }
        });

        // Connect the n-closest
        var sortedPairs = distances.entrySet()
                                   .stream()
                                   .sorted(Comparator.comparing(Entry::getKey))
                                   .map(Entry::getValue)
                                   .collect(Collectors.toCollection(ArrayDeque::new));

        // Connect them until the circuit contains all the junction boxes
        while (!sortedPairs.isEmpty()) {
            var pairToConnect = sortedPairs.poll();
            var left = pairToConnect.getLeft();
            var right = pairToConnect.getRight();
            left.connect(right);

            if (left.getCircuit().size() == junctionBoxes.size())
                return (long) left.getPosition().getRow() * right.getPosition().getRow();
        }

        return -1;
    }



    /**
     * A representation of a junction box, which has a position in three
     * dimensional space, and a set of junction boxes which form a circuit
     * together.
     */
    private static class JunctionBox {

        /** The junction box's position in three dimensional space. */
        private final Coordinate3D position;

        /**
         * The circuit to which the junction box is connected. Initially, it is
         * only connected to itself.
         */
        private Set<JunctionBox> circuit;



        /**
         * Create a new {@link JunctionBox} at the given point in three
         * dimensional space.
         * 
         * @param position The {@link Coordinate3D} describing this bow's
         *            position in three dimensional space.
         */
        public JunctionBox(Coordinate3D position) {
            this.position = position;
            this.circuit = new HashSet<>();
            this.circuit.add(this);
        }



        public Coordinate3D getPosition() {
            return position;
        }



        public Set<JunctionBox> getCircuit() {
            return circuit;
        }



        /**
         * Connect this junction box to another junction box. In doing so all of
         * the junction boxes in its circuit will be added to the target's
         * circuit.
         * 
         * @param otherJunctionBox The other junction to connect to.
         */
        public void connect(JunctionBox otherJunctionBox) {
            otherJunctionBox.circuit.addAll(circuit);
            circuit.forEach(c -> c.circuit = otherJunctionBox.circuit);
            log.debug("Connecting {} to {} (distance {}). New circuit is {}.",
                      this.getPosition(), otherJunctionBox.getPosition(),
                      this.getPosition().distanceTo(otherJunctionBox.getPosition()),
                      otherJunctionBox.circuit.size());
        }
    }

}