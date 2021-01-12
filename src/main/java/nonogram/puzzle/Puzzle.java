package nonogram.puzzle;

import java.util.Arrays;

/**
 * A Puzzle is a set of nonogram clues.
 *
 */
public class Puzzle {
	
	private int[][][] clues;
	
	/**
	 * Creates a puzzle from the given clues.
	 * 
	 * @param clues An array of clues. <code>clues[0]</code> are the row clues
	 *              (usually given besides a nonogram), <code>clues[1]</code> are
	 *              the column clues. The number of rows an columns is given by the
	 *              size of the row and column clues arrays.
	 */
	public Puzzle(int[][][] clues) {
		this.clues = clues;
	}
	
	public int[][] getRowClues() {
		return clues[0];
	}
	
	public int[][] getColumnClues() {
		return clues[1];
	}
	
	/**
	 * The number of rows of the nonogram.
	 */
	public int getHeight() {
		return clues[0].length;
	}
	
	/**
	 * The number of columns of the nonogram.
	 */
	public int getWidth() {
		return clues[1].length;
	}
	
	/**
	 * A JSON representation of this puzzle.
	 * 
	 * @return a JSON representation, for example
	 *         <pre>
	 * {
	 * 	"ver" : [[2], []],
	 * 	"hor" : [[1], [1]
	 * }
	 *         </pre>
	 */
	public String toJSON() {
		return String.format(
				"{\"ver\" : %s, \"hor\" : %s}",
				Arrays.deepToString(getRowClues()).replace('{', '[').replace('}', ']'),
				Arrays.deepToString(getColumnClues()).replace('{', '[').replace('}', ']')
				);
	}
	
	@Override
	public String toString() {
		return Arrays.deepToString(clues);
	}
}
