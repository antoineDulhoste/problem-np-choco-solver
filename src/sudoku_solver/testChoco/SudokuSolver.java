package testChoco;

import org.chocosolver.solver.ICause;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import java.util.ArrayList;

public class SudokuSolver {

	private final int SIZE = 16;
	private final int modulo = (int)Math.sqrt(SIZE);
	private Box[][] grid = new Box[SIZE][SIZE];
	ArrayList<IntVar> variables= new ArrayList<>();
	private boolean isSolve = false;
	
	private Model model = new Model("Sudoku solver");
	
	public SudokuSolver(String[] arr) {
		parse(arr);
	}
	
	public void solveDef() {


		for ( int idx = 0; idx < SIZE; idx++) {
			// Add constraints on rows, all differents ...
			model.allDifferent(getRowVars(idx)).post();
			// Add constraints on cols, all differents ...
			model.allDifferent(getColVars(idx)).post();

			// Add on inner grid
			model.allDifferent(getInnerGridVars(idx)).post();
		}

		isSolve = model.getSolver().solve();

		if ( isSolve) {
			show();
		}
		else {
			System.out.println("No solution found");
		}
	}

	public void solveMax() {


		for ( int idx = 0; idx < SIZE; idx++) {
			// Add constraints on rows, all differents ...
			model.allDifferent(getRowVars(idx)).post();
			// Add constraints on cols, all differents ...
			model.allDifferent(getColVars(idx)).post();

			// Add on inner grid
			model.allDifferent(getInnerGridVars(idx)).post();
		}
		IntVar[] tabValeur= new IntVar[variables.size()];
		for(int i = 0 ; i<variables.size();i++){
			tabValeur[i]=variables.get(i);
		}

		model.getSolver().setSearch(Search.intVarSearch(
				// selects the variable of smallest domain size
				new MaxDomain(model),
				// selects the smallest domain value (lower bound)
				new IntDomainMin(),
				DecisionOperatorFactory.makeIntNeq(),
				tabValeur

		));
		isSolve = model.getSolver().solve();

		if ( isSolve) {
			show();
		}
		else {
			System.out.println("No solution found");
		}
	}

	public void solveMin() {


		for ( int idx = 0; idx < SIZE; idx++) {
			// Add constraints on rows, all differents ...
			model.allDifferent(getRowVars(idx)).post();
			// Add constraints on cols, all differents ...
			model.allDifferent(getColVars(idx)).post();

			// Add on inner grid
			model.allDifferent(getInnerGridVars(idx)).post();
		}
		IntVar[] tabValeur= new IntVar[variables.size()];
		for(int i = 0 ; i<variables.size();i++){
			tabValeur[i]=variables.get(i);
		}

		model.getSolver().setSearch(Search.intVarSearch(
				// selects the variable of smallest domain size
				new MinDomain(model),
				// selects the smallest domain value (lower bound)
				new IntDomainMin(),
				DecisionOperatorFactory.makeIntNeq(),
				tabValeur

		));
		isSolve = model.getSolver().solve();

		if ( isSolve) {
			show();
		}
		else {
			System.out.println("No solution found");
		}
	}
	
	private IntVar[] getRowVars(int rowIdx) {
		IntVar[] res = new IntVar[SIZE];
		
		for ( int idx = 0; idx < SIZE; idx++) {
			res[idx] = grid[rowIdx][idx].getVar(); 
		}
		
		return res;
	}
	
	private IntVar[] getColVars(int colIdx) {
		IntVar[] res = new IntVar[SIZE];
		
		for ( int idx = 0; idx < SIZE; idx++) {
			res[idx] = grid[idx][colIdx].getVar(); 
		}
		
		return res;
	}
	

	private IntVar[] getInnerGridVars(int idx) {
		IntVar[] res = new IntVar[SIZE];
		
		int toAddCol = (idx % modulo) * modulo;
		int toAddRow = (idx / modulo) * modulo;
		
		int idxRes = 0;
		for ( int idxRow = 0; idxRow < modulo; idxRow++ ) {
			for ( int idxCol = 0; idxCol < modulo; idxCol++ ) {
				res[idxRes++] = grid[idxRow + toAddRow][idxCol + toAddCol].getVar(); 
				
			}
			
		}

		return res;
	}

	
	private void parse(String[] arr) {
		int rowIdx = 0; 
		for ( String row : arr) {
			int colIdx = 0;
			for (char val : row.toCharArray() ) {
				
				if ( '*' != val ) {
					NumberBox nb =new NumberBox(val, rowIdx, colIdx, model);
					//variables.add(nb.getVar());
					grid[rowIdx][colIdx] = nb;
				}
				else {
					VarBox vb =new VarBox(rowIdx, colIdx, model);
					variables.add(vb.getVar());
					grid[rowIdx][colIdx] = vb;
				}
				
				colIdx ++;
			}
			
			rowIdx ++;
		}
	}
	
	public void show() {

		int rowIdx = 0;
		for (Box[] row : grid) {
			
			if ( rowIdx % modulo == 0 ) {
				System.out.println("-------------");
			}
			int colIdx = 0;
			for( Box box : row) {
				if ( colIdx % modulo == 0 ) {
					System.out.print("|");
				}
				System.out.print(box.toString(isSolve));
				
				colIdx ++;
			}
			System.out.print("|" + System.lineSeparator());
			
			rowIdx ++;
		}
		
		System.out.println("-------------");
		
	}
	
	public static void main(String[] args ) {
		String[] sudoku = new String[] {
				"****F*AB*8******",
				"*****5*******3*6",
				"*******C5**F****",
				"****93********2*",
				"****8******54*B*",
				"**********FB7*9*",
				"******4****016**",
				"*****E**3*D*A**F",
				"****0****D*****7",
				"******3*B*C***1*",
				"******69*A**BF**",
				"****1B***9****D*",
				"****************",
				"****************",
				"****************",
				"****************"

		};
		
		SudokuSolver grid = new SudokuSolver(sudoku);
		grid.show();
		long time=System.nanoTime();
		grid.solveMax();
		long endTime=System.nanoTime();
		double temps_execute=(double)(endTime-time)/1000000000;
		System.out.println(temps_execute);
	}
}
