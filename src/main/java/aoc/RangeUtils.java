package aoc;

import org.apache.commons.lang3.Range;

/** Utilities for working with {@link Range}s. */
public class RangeUtils {

    /** Utility classes have private constructors. */
    private RangeUtils() {}



    /**
     * Calculate the union of two overlapping {@link Range}.
     *
     * @param range1 A {@link Range} which overlaps {@code range2}. Must not be
     *            {@code null}.
     * @param range2 A {@link Range} which overlaps {@code range1}. Must not be
     *            {@code null}.
     * 
     * @return A {@link Range} representing the union of {@code range1} and
     *         {@code range2}.
     * @throws IllegalArgumentException if {@code range1} does not overlap
     *             {@code range2}.
     */
    public static <T> Range<T> union(Range<T> range1, Range<T> range2) {

        if (!range1.isOverlappedBy(range2)) {
            throw new IllegalArgumentException(String.format("Cannot calculate union with non-overlapping ranges %s and %s",
                                                             range1, range2));
        }
        if (range1.equals(range2)) {
            return range1;
        }
        final T min = range1.getComparator().compare(range1.getMinimum(), range2.getMinimum()) < 0 ? range1.getMinimum()
                                                                                                   : range2.getMinimum();
        final T max = range1.getComparator().compare(range1.getMaximum(), range2.getMaximum()) < 0 ? range2.getMaximum()
                                                                                                   : range1.getMaximum();
        return Range.of(min, max, range1.getComparator());
    }



    /**
     * Given a {@link Range} of a type of {@link Number}, determine the "size"
     * of the range based on the difference between the maximum and minimum
     * values, plus 1 since it is inclusive.
     * 
     * @param <T> The type of the {@link Range}, which is a {@link Number}.
     * @param range The {@link Range} to determine its size. Must not be
     *            {@code null}.
     * @return The size of the range.
     */
    public static <T extends Number> double size(Range<T> range) {
        return range.getMaximum().doubleValue() - range.getMinimum().doubleValue() + 1;
    }

}