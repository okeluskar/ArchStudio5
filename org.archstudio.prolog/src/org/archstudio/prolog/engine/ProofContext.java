package org.archstudio.prolog.engine;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.archstudio.prolog.op.Executable;
import org.archstudio.prolog.op.iso.Abs;
import org.archstudio.prolog.op.iso.Add;
import org.archstudio.prolog.op.iso.AlphaGreaterThan;
import org.archstudio.prolog.op.iso.AlphaGreaterThanEqual;
import org.archstudio.prolog.op.iso.AlphaLessThan;
import org.archstudio.prolog.op.iso.AlphaLessThanEqual;
import org.archstudio.prolog.op.iso.BagOf;
import org.archstudio.prolog.op.iso.Conjunction;
import org.archstudio.prolog.op.iso.Cut;
import org.archstudio.prolog.op.iso.Disjunction;
import org.archstudio.prolog.op.iso.Equals;
import org.archstudio.prolog.op.iso.False;
import org.archstudio.prolog.op.iso.FindAll;
import org.archstudio.prolog.op.iso.IfThen;
import org.archstudio.prolog.op.iso.Is;
import org.archstudio.prolog.op.iso.IsAtom;
import org.archstudio.prolog.op.iso.IsAtomic;
import org.archstudio.prolog.op.iso.IsCallable;
import org.archstudio.prolog.op.iso.IsCompound;
import org.archstudio.prolog.op.iso.IsFloat;
import org.archstudio.prolog.op.iso.IsInteger;
import org.archstudio.prolog.op.iso.IsNonvar;
import org.archstudio.prolog.op.iso.IsNumber;
import org.archstudio.prolog.op.iso.IsVar;
import org.archstudio.prolog.op.iso.Length;
import org.archstudio.prolog.op.iso.Multiply;
import org.archstudio.prolog.op.iso.Neck;
import org.archstudio.prolog.op.iso.Not;
import org.archstudio.prolog.op.iso.NotEquals;
import org.archstudio.prolog.op.iso.NotUnifiable;
import org.archstudio.prolog.op.iso.SetOf;
import org.archstudio.prolog.op.iso.SoftCut;
import org.archstudio.prolog.op.iso.Sort;
import org.archstudio.prolog.op.iso.Subtract;
import org.archstudio.prolog.op.iso.True;
import org.archstudio.prolog.op.iso.Unifiable;
import org.archstudio.prolog.op.iso.ValueEquals;
import org.archstudio.prolog.op.iso.ValueGreaterThan;
import org.archstudio.prolog.op.iso.ValueGreaterThanEqual;
import org.archstudio.prolog.op.iso.ValueLessThan;
import org.archstudio.prolog.op.iso.ValueLessThanEqual;
import org.archstudio.prolog.op.iso.ValueNotEquals;
import org.archstudio.prolog.op.iso.Write;
import org.archstudio.prolog.op.iso.WriteNewLine;
import org.archstudio.prolog.term.ComplexTerm;
import org.archstudio.prolog.term.ConstantTerm;
import org.archstudio.prolog.term.ListTerm;
import org.archstudio.prolog.term.Term;
import org.archstudio.prolog.term.VariableTerm;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ProofContext implements Cloneable {

	private static final Map<String, Class<? extends Executable>> operations = Maps.newHashMap();
	{
		// register the ISO operations

		operations.put("+", Add.class);
		operations.put("@>", AlphaGreaterThan.class);
		operations.put("@>=", AlphaGreaterThanEqual.class);
		operations.put("@<", AlphaLessThan.class);
		operations.put("@=<", AlphaLessThanEqual.class);
		operations.put(",", Conjunction.class);
		operations.put("!", Cut.class);
		operations.put(";", Disjunction.class);
		operations.put("==", Equals.class);
		operations.put("->", IfThen.class);
		operations.put("*", Multiply.class);
		operations.put(":-", Neck.class);
		operations.put("\\+", Not.class);
		operations.put("\\=", NotUnifiable.class);
		operations.put("\\==", NotEquals.class);
		operations.put("*->", SoftCut.class);
		operations.put("-", Subtract.class);
		operations.put("=", Unifiable.class);
		operations.put("=:=", ValueEquals.class);
		operations.put(">", ValueGreaterThan.class);
		operations.put(">=", ValueGreaterThanEqual.class);
		operations.put("<", ValueLessThan.class);
		operations.put("=<", ValueLessThanEqual.class);
		operations.put("=\\=", ValueNotEquals.class);

		operations.put("abs", Abs.class);
		operations.put("atom", IsAtom.class);
		operations.put("atomic", IsAtomic.class);
		operations.put("bagof", BagOf.class);
		operations.put("callable", IsCallable.class);
		operations.put("compound", IsCompound.class);
		operations.put("fail", False.class);
		operations.put("false", False.class);
		operations.put("findall", FindAll.class);
		operations.put("float", IsFloat.class);
		operations.put("integer", IsInteger.class);
		operations.put("is", Is.class);
		operations.put("length", Length.class);
		operations.put("nonvar", IsNonvar.class);
		operations.put("number", IsNumber.class);
		operations.put("setof", SetOf.class);
		operations.put("sort", Sort.class);
		operations.put("true", True.class);
		operations.put("var", IsVar.class);
		operations.put("write", Write.class);
		operations.put("writeln", WriteNewLine.class);

		// add additional operations
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		if (reg != null) {
			// The Extension Registry can be null if we're running outside of Eclipse
			for (IConfigurationElement configurationElement : reg
					.getConfigurationElementsFor("org.archstudio.prolog.operation")) {
				String name = configurationElement.getAttribute("name");
				String className = configurationElement.getAttribute("class");
				if (name != null && className != null) {
					String bundleName = configurationElement.getDeclaringExtension().getContributor().getName();
					try {
						@SuppressWarnings("unchecked")
						Class<Executable> clazz = (Class<Executable>) Platform.getBundle(bundleName).loadClass(
								className);
						operations.put(name, clazz);
					}
					catch (ClassNotFoundException cnfe) {
						cnfe.printStackTrace();
					}
				}
			}
		}
	}
	private final ListMultimap<Signature, ComplexTerm> knowledgeBase = ArrayListMultimap.create();
	private final Map<Signature, ListMultimap<Object, ComplexTerm>[]> valueBasedKnowledgeBase = Maps.newHashMap();
	private final LoadingCache<Signature, Map<ComplexTerm, Integer>> knowledgeBaseIndeces = CacheBuilder.newBuilder()
			.build(new CacheLoader<Signature, Map<ComplexTerm, Integer>>() {
				@Override
				public Map<ComplexTerm, Integer> load(Signature key) throws Exception {
					Map<ComplexTerm, Integer> indeces = Maps.newHashMap();
					List<ComplexTerm> terms = knowledgeBase.get(key);
					for (int i = 0; i < terms.size(); i++) {
						indeces.put(terms.get(i), i);
					}
					return indeces;
				}
			});

	private BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));

	public ProofContext() {
	}

	public ProofContext(Iterable<ComplexTerm> knowledgeBase) {
		add(knowledgeBase);
	}

	public ProofContext(ProofContext toCopy) {
		add(toCopy.knowledgeBase.values());
	}

	public void add(Iterable<ComplexTerm> terms) {
		for (ComplexTerm term : terms) {
			add(term, true);
		}
	}

	@SuppressWarnings("unchecked")
	public void add(ComplexTerm term, boolean atEnd) {
		ComplexTerm head = term;
		if (!(term instanceof Neck) && !PrologUtils.extractVariables(Sets.<VariableTerm> newHashSet(), term).isEmpty()) {
			term = new Neck(":-", Lists.newArrayList(term, new True("true")));
		}
		if (term instanceof Neck) {
			head = (ComplexTerm) ((Neck) term).getTerm(0);
		}
		add(knowledgeBase.get(head.getSignature()), term, atEnd);
		ListMultimap<Object, ComplexTerm>[] indexes = valueBasedKnowledgeBase.get(head.getSignature());
		if (indexes == null) {
			valueBasedKnowledgeBase.put(head.getSignature(), indexes = new ListMultimap[head.getArity()]);
			for (int j = 0; j < head.getArity(); j++) {
				indexes[j] = ArrayListMultimap.create();
			}
		}
		for (int termIndex = 0; termIndex < head.getArity(); termIndex++) {
			ListMultimap<Object, ComplexTerm> index = indexes[termIndex];
			add(index.get(head.getTerm(termIndex)), term, atEnd);
		}
		knowledgeBaseIndeces.invalidate(term.getSignature());
	}

	private void add(List<ComplexTerm> list, ComplexTerm term, boolean atEnd) {
		if (atEnd) {
			list.add(term);
		}
		else {
			list.add(0, term);
		}
	}

	public List<ComplexTerm> getKnowledgeBaseTerms(ComplexTerm goal, Map<VariableTerm, Term> variables) {
		if (!knowledgeBase.containsKey(goal.getSignature())) {
			throw new IllegalArgumentException("Unrecognized signature: " + goal.getSignature());
		}
		List<ComplexTerm> result = knowledgeBase.get(goal.getSignature());
		for (int termIndex = 0; termIndex < goal.getArity(); termIndex++) {
			if (goal.getTerm(termIndex) instanceof VariableTerm) {
				if (variables.containsKey(goal.getTerm(termIndex))) {
					Object value = variables.get(goal.getTerm(termIndex));
					ListMultimap<Object, ComplexTerm>[] v = valueBasedKnowledgeBase.get(goal.getSignature());
					if (v != null) {
						List<ComplexTerm> t = v[termIndex].get(value);
						if (t != null) {
							// TODO: perform intersection instead of size comparison
							if (t.size() < result.size()) {
								result = t;
							}
						}
					}
				}
			}
			else {
				Object value = goal.getTerm(termIndex);
				ListMultimap<Object, ComplexTerm>[] v = valueBasedKnowledgeBase.get(goal.getSignature());
				if (v != null) {
					List<ComplexTerm> t = (List<ComplexTerm>) v[termIndex].asMap().get(value);
					if (t != null) {
						// TODO: perform intersection instead of size comparison
						if (t.size() < result.size()) {
							result = t;
						}
					}
				}
			}
		}
		return Lists.newArrayList(result);
	}

	public Integer getIndex(ComplexTerm complexTerm) {
		return knowledgeBaseIndeces.getUnchecked(complexTerm.getSignature()).get(complexTerm);
	}

	public Term create(String name, List<Term> terms) {
		if (".".equals(name)) {
			return new ListTerm(terms.get(0), terms.get(1));
		}
		@SuppressWarnings("unchecked")
		Class<Executable> operationClass = (Class<Executable>) operations.get(name);
		if (operationClass != null) {
			try {
				if (terms != null) {
					Constructor<Executable> c = operationClass.getConstructor(String.class, List.class);
					return c.newInstance(name, terms);
				}
				else {
					Constructor<Executable> c = operationClass.getConstructor(String.class);
					return c.newInstance(name);
				}
			}
			catch (Throwable exc) {
				throw new RuntimeException(exc);
			}
		}
		if (terms != null) {
			return new ComplexTerm(name, terms);
		}
		return new ConstantTerm(name);
	}

	public BufferedWriter getOutput() {
		return output;
	}

	public void setOutput(BufferedWriter output) {
		this.output = output;
	}
}
