package testChoco;

import org.chocosolver.memory.IStateInt;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableEvaluator;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.variables.IntVar;

public class MinDomain implements VariableSelector<IntVar>, VariableEvaluator<IntVar> {
	private final IStateInt lastIdx;

	public MinDomain(Model model) {
		this.lastIdx = model.getEnvironment().makeInt(0);
	}

	public IntVar getVariable(IntVar[] variables) {
		IntVar smallVar = null;
		int smallDSize = 2147483647;
		int idx;
		for(idx = this.lastIdx.get(); idx < variables.length && variables[idx].isInstantiated(); ++idx) {
		}

		this.lastIdx.set(idx);

		for(; idx < variables.length; ++idx) {
			int dsize = variables[idx].getDomainSize();
			if (dsize < smallDSize && dsize > 1) {
				smallVar = variables[idx];
				smallDSize = dsize;
			}
		}

		return smallVar;
	}

	public double evaluate(IntVar variable) {
		return (double)variable.getDomainSize();
	}
}
