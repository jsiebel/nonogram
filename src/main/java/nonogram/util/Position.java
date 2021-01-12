package nonogram.util;

/**
 * Represents a position on a grid. Instances are immutable.
 */
public class Position {
	public final int row;
	public final int column;

	public Position(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public String toString() {
		return "Position(" + row + ", " + column + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}else if (obj instanceof Position) {
			Position position = (Position) obj;
			return position.row == row && position.column == column;
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return row ^ column<<16;
	}
}
