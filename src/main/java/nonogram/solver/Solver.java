package nonogram.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nonogram.puzzle.Puzzle;
import nonogram.puzzle.Solution;
import nonogram.util.Position;

/**
 * Finds solutions to a puzzle.
 */
public class Solver {
	
	private Puzzle puzzle;
	
	private Map<Position, Boolean> hints;
	
	public Solver(Puzzle puzzle) {
		this.puzzle = puzzle;
		this.hints = new HashMap<>();
	}
	
	/**
	 * Returns a solution to the puzzle, if one exists.
	 */
	public Optional<Solution> getAnySolution() {
		return stream().findAny();
	}
	
	/**
	 * Returns a list of all solutions to the puzzle.
	 */
	public List<Solution> getAllSolutions() {
		return stream().collect(Collectors.toList());
	}
	
	/**
	 * Returns a stream of all solutions to the puzzle.
	 */
	public Stream<Solution> stream() {
		UnsolvedBoard board = new UnsolvedBoard(puzzle);
		hints.forEach((position, value) -> board.addHint(position, value));
		return stream(board);
	}
	
	/**
	 * Performs a depth-first search for all solutions on the given board. First the
	 * number of solutions is reduced by finding contradiction between row and cell
	 * candidates. Then a cell is selected, and the board is solved separately for
	 * both possible values.
	 */
	private static Stream<Solution> stream(UnsolvedBoard board) {
		board.reduce();
		
		if (!board.isSolvable()) {
			return Stream.empty();
		} else if (board.isAmbiguous()) {
			Position splitPosition = board.getSplitPosition();
			UnsolvedBoard newBoard = new UnsolvedBoard(board);
			board.addHint(splitPosition, false);
			newBoard.addHint(splitPosition, true);
			return Stream.of(board, newBoard).flatMap(Solver::stream);
		} else {
			return Stream.of(board.toSolution());
		}
	}
	
	/**
	 * Adds a hint by setting a cell value. Only solutions where the cell has this
	 * value are found.
	 */
	public void addHint(int rowIndex, int columnIndex, boolean value) {
		addHint(new Position(rowIndex, columnIndex), value);
	}
	
	/**
	 * Adds a hint by setting a cell value. Only solutions where the cell has this
	 * value are found.
	 */
	public void addHint(Position position, boolean value) {
		hints.put(position, value);
	}
	
	public Puzzle getPuzzle() {
		return puzzle;
	}
}
