package nonogram.puzzle;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import nonogram.util.Position;

/**
 * A solution of a nonogram.
 */
public class Solution {
	
	private List<BitSet> rows;
	private int width;
	private int height;
	
	public Solution(List<BitSet> rows, int width, int height) {
		this.rows = rows;
		this.width = width;
		this.height = height;
	}
	
	public List<BitSet> getRows() {
		return rows;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Creates a puzzle from this solution by finding clues matching the cell
	 * distribution.
	 */
	public Puzzle toPuzzle() {
		int[][][] result = new int[2][][];
		int[] buffer = new int[Math.max(width, height)];
		result[0] = new int[height][];
		for (int i = 0; i < height; i++) {
			int bufferIndex = 0;
			int currentBlock = 0;
			for (int j = 0; j <= width; j++) {
				if (rows.get(i).get(j)) {
					currentBlock++;
				} else if (currentBlock > 0) {
					buffer[bufferIndex++] = currentBlock;
					currentBlock = 0;
				}
			}
			result[0][i] = Arrays.copyOf(buffer, bufferIndex);
		}
		result[1] = new int[width][];
		for (int j = 0; j < width; j++) {
			int bufferIndex = 0;
			int currentBlock = 0;
			for (int i = 0; i <= height; i++) {
				if (i < height && rows.get(i).get(j)) {
					currentBlock++;
				} else if (currentBlock > 0) {
					buffer[bufferIndex++] = currentBlock;
					currentBlock = 0;
				}
			}
			result[1][j] = Arrays.copyOf(buffer, bufferIndex);
		}
		return new Puzzle(result);
	}
	
	public void print() {
		for (BitSet row : rows) {
			for (int i = 0; i < width; i++) {
				System.out.print(row.get(i) ? "██" : ". ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Fills all cells with the given value which have an uninterrupted path to the
	 * given position where the value different.
	 */
	public void floodFill(int rowIndex, int columnIndex, boolean value) {
		Deque<Position> remaining = new LinkedList<>();
		remaining.push(new Position(rowIndex, columnIndex));
		while (!remaining.isEmpty()) {
			Position next = remaining.pop();
			if (inRange(next) && rows.get(next.row).get(next.column) != value) {
				rows.get(next.row).set(next.column, value);
				remaining.add(new Position(next.row - 1, next.column));
				remaining.add(new Position(next.row + 1, next.column));
				remaining.add(new Position(next.row, next.column - 1));
				remaining.add(new Position(next.row, next.column + 1));
			}
		}
	}
	
	private boolean inRange(Position position) {
		return 
				0 <= position.row && position.row < height &&
				0 <= position.column && position.column < width;
	}
	
	/**
	 * Inverts all cell values.
	 */
	public void invert() {
		for (BitSet row : rows) {
			row.flip(0, width);
		}
	}
}
