package org.archstudio.prolog.op.iso;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.archstudio.prolog.engine.PrologUtils;
import org.archstudio.prolog.engine.ProofContext;
import org.archstudio.prolog.engine.UnificationEngine;
import org.archstudio.prolog.op.Executable;
import org.archstudio.prolog.term.ComplexTerm;
import org.archstudio.prolog.term.Term;
import org.archstudio.prolog.term.VariableTerm;

public class AlphaLessThan extends ComplexTerm implements Executable {

	public AlphaLessThan(String name, List<? extends Term> terms) {
		super(name, 2, terms);
	}

	@Override
	public Iterable<Map<VariableTerm, Term>> execute(ProofContext proofContext, UnificationEngine unificationEngine,
			Term source, Map<VariableTerm, Term> variables) {
		String n1 = PrologUtils.resolveString(proofContext, getTerm(0), variables);
		String n2 = PrologUtils.resolveString(proofContext, getTerm(1), variables);
		return n1.compareTo(n2) < 0 ? Collections.singleton(variables) : PrologUtils.emptyVariablesList();
	}
}
