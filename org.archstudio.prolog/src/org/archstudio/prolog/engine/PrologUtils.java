package org.archstudio.prolog.engine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.archstudio.prolog.op.Evaluable;
import org.archstudio.prolog.op.Executable;
import org.archstudio.prolog.term.ComplexTerm;
import org.archstudio.prolog.term.ListTerm;
import org.archstudio.prolog.term.Term;
import org.archstudio.prolog.term.VariableTerm;

import com.google.common.collect.AbstractIterator;

public class PrologUtils {

	private PrologUtils() {
	}

	public static final Set<VariableTerm> extractVariables(Set<VariableTerm> variables, Term term) {
		if (term instanceof VariableTerm) {
			if (!"_".equals(((VariableTerm) term).getName())) {
				variables.add((VariableTerm) term);
			}
		}
		else if (term instanceof ComplexTerm) {
			for (Term t : ((ComplexTerm) term).getTerms()) {
				extractVariables(variables, t);
			}
		}
		else if (term instanceof ListTerm) {
			if (((ListTerm) term).getHead() != null) {
				extractVariables(variables, ((ListTerm) term).getHead());
			}
			if (((ListTerm) term).getTail() != null) {
				extractVariables(variables, ((ListTerm) term).getTail());
			}
		}
		return variables;
	}

	public static final ComplexTerm resolveComplexTerm(ProofContext proofContext, Term t,
			Map<VariableTerm, Term> variables) {
		t = t.resolve(proofContext, variables);
		if (t instanceof ComplexTerm) {
			return (ComplexTerm) t;
		}
		throw new RuntimeException("Not complex term: " + t);
	}

	public static final Executable resolveExecutable(ProofContext proofContext, Term t,
			Map<VariableTerm, Term> variables) {
		t = t.resolve(proofContext, variables);
		if (t instanceof Executable) {
			return (Executable) t;
		}
		throw new RuntimeException("Not executable: " + t);
	}

	public static final Evaluable resolveEvaluable(ProofContext proofContext, Term t, Map<VariableTerm, Term> variables) {
		t = t.resolve(proofContext, variables);
		if (t instanceof Evaluable) {
			return (Evaluable) t;
		}
		throw new RuntimeException("Not evaluable: " + t);
	}

	public static final Number evaluate(ProofContext proofContext, Term term, Map<VariableTerm, Term> variables) {
		return resolveEvaluable(proofContext, term, variables).evaluate(proofContext, variables);
	}

	public static final BigDecimal toBigDecimal(java.lang.Number n) {
		return n instanceof BigDecimal ? (BigDecimal) n : new BigDecimal((BigInteger) n);
	}

	public static final Iterable<Map<VariableTerm, Term>> negate(Iterable<Map<VariableTerm, Term>> result,
			Map<VariableTerm, Term> variables) {
		if (result.iterator().hasNext()) {
			return emptyVariablesList();
		}
		return Collections.singleton(variables);
	}

	public static final Iterable<Map<VariableTerm, Term>> emptyVariablesList() {
		return Collections.emptyList();
	}

	public static Iterable<Term> termOrListTerms(final Term t) {

		if (t instanceof ListTerm) {
			return new Iterable<Term>() {

				@Override
				public Iterator<Term> iterator() {
					return new AbstractIterator<Term>() {
						Term i = t;

						@Override
						protected Term computeNext() {
							if (i instanceof ListTerm) {
								ListTerm listTerm = (ListTerm) i;
								Term next = listTerm.getHead();
								i = listTerm.getTail();
								if (next != null) {
									return next;
								}
							}
							if (i == null) {
								return endOfData();
							}
							Term next = i;
							i = null;
							return next;
						}
					};
				}
			};
		}
		return Collections.singleton(t);
	}
}