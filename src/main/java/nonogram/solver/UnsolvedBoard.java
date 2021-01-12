package nonogram.solver;

import java.util.BitSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import nonogram.puzzle.Puzzle;
import nonogram.puzzle.Solution;
import nonogram.util.Position;

/**
 * An incomplete solution of a nonogram. It contains a list of possible cell
 * configurations for each row and column.
 */
public class UnsolvedBoard {
	private List<UnsolvedCellArray> rows;
	private List<UnsolvedCellArray> cols;
	private Boolean solvable = null;
	
	public UnsolvedBoard(Puzzle puzzle) {
		this(
				mapClues(puzzle.getRowClues(), puzzle.getWidth()),
				mapClues(puzzle.getColumnClues(), puzzle.getHeight())
				);
	}
	
	private static List<UnsolvedCellArray> mapClues(int[][] cluesArray, int length) {
		return Stream.of(cluesArray)
				.map(clues -> new UnsolvedCellArray(clues, length))
				.collect(Collectors.toList());
	}
	
	public UnsolvedBoard(List<UnsolvedCellArray> rows, List<UnsolvedCellArray> cols) {
		this.rows = rows;
		this.cols = cols;
	}
	
	public UnsolvedBoard(UnsolvedBoard board) {
		this(cloneCells(board.rows), cloneCells(board.cols));
		this.solvable = board.solvable;
	}
	
	private static List<UnsolvedCellArray> cloneCells(List<UnsolvedCellArray> cellArrays) {
		return cellArrays.stream()
				.map(UnsolvedCellArray::new)
				.collect(Collectors.toList());
	}
	
	public Position getSplitPosition() {
		OptionalInt bestRowOptional = getBestSplitIndex(rows);
		if (bestRowOptional.isPresent()) {
			int bestRow = bestRowOptional.getAsInt();
			int bestRowDistance = Math.min(bestRow, rows.size() - 1 - bestRow);
			int bestCol = getBestSplitIndex(cols).getAsInt();
			int bestColDistance = Math.min(bestCol, cols.size() - 1 - bestCol);
			if (bestRowDistance < bestColDistance) {
				return new Position(bestRow, rows.get(bestRow).getAmbiguousIndex().getAsInt());
			} else {
				return new Position(cols.get(bestCol).getAmbiguousIndex().getAsInt(), bestCol);
			}
		} else {
			return null;
		}
	}
	
	private static OptionalInt getBestSplitIndex(List<UnsolvedCellArray> rows) {
		return IntStream.range(0, rows.size())
				.map(i -> i % 2 == 0 ? i / 2 : rows.size() - 1 - i / 2)
				.filter(i -> rows.get(i).isAmbiguous())
				.findFirst();
	}
	
	/**
	 * Reduces the candidates of rows (or columns) by applying cell values known
	 * from columns (or rows). If a cell has the same value in all of a row's (or
	 * column's) candidates, this value can be set in the corresponding column (or
	 * row), eliminating all contradicting candidates there.
	 */
	public void reduce() {
		boolean reduceRows;
		boolean reduceCols;
		do {
			reduceRows = reduce(cols, rows);
			reduceCols = isSolvable() && reduce(rows, cols);
		} while (reduceRows || reduceCols);
	}
	
	private boolean reduce(List<UnsolvedCellArray> source, List<UnsolvedCellArray> target) {
		boolean anyChanges = false;
		
		for (int i = 0; i < source.size(); i++) {
			BitSet all = source.get(i).and();
			BitSet any = source.get(i).or();
			for (int j = 0; j < target.size(); j++) {
				if (all.get(j)) {
					anyChanges |= target.get(j).addHint(i, true);
				} else if (!any.get(j)) {
					anyChanges |= target.get(j).addHint(i, false);
				}
				if (!target.get(j).isSolvable()) {
					solvable = false;
				}
			}
		}
		return anyChanges;
	}
	
	public boolean isSolvable() {
		if (solvable == null) {
			solvable = rows.stream().allMatch(UnsolvedCellArray::isSolvable);
		}
		return solvable;
	}
	
	public boolean isAmbiguous() {
		return rows.stream().anyMatch(UnsolvedCellArray::isAmbiguous);
	}
	
	public void addHint(Position splitPosition, boolean value) {
		addHint(splitPosition.row, splitPosition.column, value);
	}
	
	public void addHint(int rowIndex, int columnIndex, boolean value) {
		rows.get(rowIndex).addHint(columnIndex, value);
		cols.get(columnIndex).addHint(rowIndex, value);
		solvable = null;
	}
	
	public Solution toSolution() {
		List<BitSet> rows = getRows().stream().map(UnsolvedCellArray::getAnyCandidate).collect(Collectors.toList());
		return new Solution(rows, getWidth(), getHeight());
	}
	
	public void print() {
		for (UnsolvedCellArray row : rows) {
			for (int i = 0; i < cols.size(); i++) {
				System.out.print(row.getAnyCandidate().get(i) ? "██" : ". ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public List<UnsolvedCellArray> getRows() {
		return rows;
	}
	
	public int getWidth() {
		return cols.size();
	}
	
	public int getHeight() {
		return rows.size();
	}
}
