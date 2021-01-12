package nonogram.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * A row or column of an incomplete solution of a nonogram. It contains a list
 * of possible cell configurations candidates.
 */
public class UnsolvedCellArray {
	
	private List<BitSet> candidates;
	private BitSet and = null;
	private BitSet or = null;
	
	/**
	 * Creates an {@link UnsolvedCellArray} for the given clues. All cell
	 * configuration candidates are generated.
	 */
	public UnsolvedCellArray(int[] clues, int totalLength) {
		this(createCandidates(clues, totalLength));
	}
	
	private static List<BitSet> createCandidates(int[] clues, int totalLength) {
		int required = Arrays.stream(clues).sum() + clues.length - 1;
		if (totalLength < required) {
			return Collections.emptyList();
		} else {
			List<BitSet> results = List.of(new BitSet(totalLength));
			for (int clue : clues) {
				List<BitSet> partialResults = results;
				results = new ArrayList<>();
				for (BitSet partialResult : partialResults) {
					int minimumPosition = partialResult.isEmpty() ? 0 : partialResult.length() + 1;
					int maximumPosition = totalLength - required;
					for (int position = minimumPosition; position <= maximumPosition; position++) {
						BitSet result = (BitSet) partialResult.clone();
						result.set(position, position + clue);
						results.add(result);
					}
				}
				required -= clue + 1;
			}
			return results;
		}
	}
	
	/**
	 * Creates a copy of the given {@link UnsolvedCellArray}.
	 */
	public UnsolvedCellArray(UnsolvedCellArray cellArray) {
		this(cellArray.candidates);
		this.and = cellArray.and;
		this.or = cellArray.or;
	}
	
	public UnsolvedCellArray(List<BitSet> candidates) {
		this.candidates = candidates.stream()
				.map(BitSet::clone)
				.map(BitSet.class::cast)
				.collect(Collectors.toCollection(ArrayList::new));
	}
	
	/**
	 * Returns a {@link BitSet} of cells that are set in all candidates.
	 */
	public BitSet and() {
		if (and == null) {
			if (candidates.isEmpty()) {
				and = new BitSet();
			} else {
				and = (BitSet) candidates.get(0).clone();
				for (BitSet b : candidates) {
					and.and(b);
				}
			}
		}
		return and;
	}
	
	/**
	 * Returns a {@link BitSet} of cells that are set in at least one candidate.
	 */
	public BitSet or() {
		if (or == null) {
			or = new BitSet();
			for (BitSet b : candidates) {
				or.or(b);
			}
		}
		return or;
	}
	
	/**
	 * Returns <code>true</code> if there are different candidates.
	 */
	public boolean isAmbiguous() {
		return size() > 1 && !and().equals(or());
	}
	
	/**
	 * The number of candidates.
	 */
	public int size() {
		return candidates.size();
	}
	
	/**
	 * Returns the index of a cell which has an undetermined value.
	 */
	public OptionalInt getAmbiguousIndex() {
		return or().stream().filter(i -> !and().get(i)).findFirst();
	}
	
	/**
	 * Returns <code>true</code> if there are any candidates left.
	 */
	public boolean isSolvable() {
		return !candidates.isEmpty();
	}
	
	/**
	 * Adds a hint. This sets a cell value, eliminating all contradicting
	 * candidates.
	 */
	public boolean addHint(int index, boolean value) {
		if (candidates.removeIf(bitSet -> bitSet.get(index) != value)) {
			and = null;
			or = null;
			return true;
		} else {
			return false;
		}
	}
	
	public void add(BitSet candidate) {
		candidates.add(candidate);
		and = null;
		or = null;
	}
	
	/**
	 * Returns any candidate.
	 * 
	 * @throws IndexOutOfBoundsException if there are no candidates
	 */
	public BitSet getAnyCandidate() {
		return candidates.get(0);
	}
	
	@Override
	public String toString() {
		return candidates.toString();
	}
	
	public void print() {
		BitSet all = and();
		BitSet any = or();
		for (int i = 0; i < any.length(); i++) {
			if (all.get(i)) {
				System.out.print("██");
			} else if (any.get(i)) {
				System.out.print("░░");
			} else {
				System.out.print("  ");
			}
		}
		System.out.println();
	}
}
