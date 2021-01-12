package nonogram;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nonogram.puzzle.Solution;
import nonogram.solver.Solver;
import nonogram.solver.UnsolvedCellArray;

/**
 * Shows a few examples.
 */
public class Nonogram {
	
	private static int count;
	
	public static void main(String[] args) {
		Solver solver1 = new Solver(Puzzles.cw2);
		solver1.getAnySolution().get().print();
		
		Solver solver2 = new Solver(Puzzles.cw1);
		solver2.addHint(3, 6, false);
		solver2.addHint(3, 7, true);
		solver2.addHint(4, 4, false);
		solver2.addHint(4, 5, true);
		solver2.addHint(5, 4, false);
		solver2.addHint(5, 5, true);
		
		List<Solution> solutions = new ArrayList<>();
		
		long time = System.nanoTime();
		count = 0;
		solver2.stream().forEach(solution -> {
			System.out.println("Solution " + ++count + ":");
			solution.print();
			solutions.add(solution);
		});
		System.out.printf("Time: %.1f s%n", (System.nanoTime() - time) / 1e9);
		
		printSolutionIntersection(solutions);
		
		Solution solution = solutions.get(0);
		solution.floodFill(0, 0, false);
		solution.floodFill(solution.getHeight() - 1, 0, false);
		solution.print();
	}
	
	private static void printSolutionIntersection(List<Solution> solutions) {
		if (!solutions.isEmpty()) {
			System.out.println("printSolutionIntersection");
			for (int i = 0; i < solutions.get(0).getHeight(); i++) {
				final int rowIndex = i;
				UnsolvedCellArray row = new UnsolvedCellArray(solutions.stream()
						.map(Solution::getRows)
						.map(rows -> rows.get(rowIndex))
						.collect(Collectors.toList())
						);
				row.print();
			}
		}
	}
}