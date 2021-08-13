package org.evosuite.seeding.smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.coverage.branch.BranchFitness;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.interprocedural.ComputationPath;
import org.evosuite.graphs.interprocedural.DepVariable;
import org.evosuite.graphs.interprocedural.InterproceduralGraphAnalysis;
import org.evosuite.instrumentation.InstrumentingClassLoader;
import org.evosuite.seeding.RuntimeSensitiveVariable;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.ArrayStatement;
import org.evosuite.testcase.statements.AssignmentStatement;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.PrimitiveStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.DepVariableWrapper;
import org.evosuite.testcase.variable.ArrayIndex;
import org.evosuite.testcase.variable.ArrayReference;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.utils.Randomness;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class SensitivityMutator {
	public static List<List<Object>> data = new ArrayList<List<Object>>();
	public static Object HeadValue;
	public static Object TailValue;
//	public static int iter = 0;
	public static String projectId;
	public static String className;
	public static String methodName;

	public static TestCase initializeTest(TestFactory testFactory, boolean allowNullValue) {
		TestCase test = new DefaultTestCase();
		int success = -1;
		long t1 = System.currentTimeMillis();
		while (test.size() == 0 || success == -1) {
			long t2 = System.currentTimeMillis();
			if ((t2 - t1) / 1000 > 3)
				return null;
			test = new DefaultTestCase();
			success = testFactory.insertRandomStatement(test, 0);
			if (test.size() != 0 && success != -1 && !allowNullValue) {
				mutateNullStatements(test);
			}
		}

		return test;
	}

	public static void mutateNullStatements(TestCase test) {
		for (int i = 0; i < test.size(); i++) {
			Statement s = test.getStatement(i);
			if (s instanceof NullStatement) {
				TestFactory.getInstance().changeNullStatement(test, s);
				System.currentTimeMillis();
			}
		}
	}

	public static void testSensitity(Set<FitnessFunction<?>> fitness)
			throws ClassNotFoundException, ConstructionFailedException {
		Map<Branch, Set<DepVariable>> branchesInTargetMethod = InterproceduralGraphAnalysis.branchInterestedVarsMap
				.get(Properties.TARGET_METHOD);

		ComputationPath path = null;
		for (Branch branch : branchesInTargetMethod.keySet()) {
			testBranchSensitivity(branchesInTargetMethod, branch, path);
		}

		System.currentTimeMillis();
	}

	public static ValuePreservance testBranchSensitivity(ComputationPath path) {
		Branch branch = path.getBranch();

		TestCase test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		if (test == null) {
			return new ValuePreservance(null, null);
		}
		String methodCall = test.getStatement(test.size() - 1).toString();
		while (!methodCall.equals(Properties.TARGET_METHOD)) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
			methodCall = test.getStatement(test.size() - 1).toString();
		}
		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);

		ValuePreservance preservingList = new ValuePreservance(null, null);

		TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
		DepVariable rootVariable = path.getComputationNodes().get(0);
		BytecodeInstruction tailInstruction = path.getRelevantTailInstruction();

		List<BytecodeInstruction> observations = new ArrayList<>();
		observations.add(tailInstruction);

		List<DepVariable> vars = new ArrayList<>();
		vars.add(rootVariable);
		preservingList = checkPreservance(branch, newTestChromosome, vars, observations, synthensizer);
		return preservingList;
	}

	public static ValuePreservance testBranchSensitivity(List<DepVariable> rootVariables,
			List<BytecodeInstruction> observingValues, Branch branch, TestChromosome testSeed) {
		TestCase test = null;
		if(testSeed == null) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);			
		}
		else {
			test = testSeed.getTestCase();
		}
		if (test == null) {
			return new ValuePreservance(null, null);
		}
		String methodCall = test.getStatement(test.size() - 1).toString();
		if (!methodCall.equals(Properties.TARGET_METHOD)) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		}
		
		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);
		
		TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
		ValuePreservance preservingList = checkPreservance(branch, newTestChromosome, rootVariables,
				observingValues, synthensizer);
		
		System.currentTimeMillis();
		return preservingList;
	}

	private static ValuePreservance checkPreservance(Branch branch, TestChromosome newTestChromosome,
			List<DepVariable> rootVariables, List<BytecodeInstruction> observations,
			ConstructionPathSynthesizer synthensizer) {
		ValuePreservance preservance = new ValuePreservance(observations, rootVariables);
		
		Map<DepVariableWrapper, List<VariableReference>> map = synthensizer.getGraph2CodeMap();
		
		for (int i = 0; i < Properties.DYNAMIC_SENSITIVITY_THRESHOLD; i++) {
			newTestChromosome = (TestChromosome) newTestChromosome.clone();
			MethodInputs inputs = constructInputValues(rootVariables, newTestChromosome, map);
			inputs.mutate();
			Map<String, List<Object>> observationMap = evaluateObservations(branch, observations, newTestChromosome);
			
			ObservationRecord record = new ObservationRecord(inputs, observationMap);
			preservance.addRecord(record);
			
//			System.currentTimeMillis();
		}
		
		System.currentTimeMillis();
		return preservance;
	}

	private static Map<String, Boolean> constructObservationsType(List<BytecodeInstruction> observations) {
		Map<String, Boolean> m = new HashMap<>();
		for (BytecodeInstruction ob : observations) {
			m.put(ob.toString(), ob.isConstant());
		}
		return m;
	}

	private static TestChromosome createNewTestCase(Branch branch) {
		TestCase test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);

		TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
		return newTestChromosome;
	}


	private static  boolean observationIsNull(Map<String, List<Object>> observationMap) {
		for(String s : observationMap.keySet()) {
			if(observationMap.get(s).size() > 0)
				return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	private static MethodInputs constructInputValues(List<DepVariable> rootVariables,
			TestChromosome newTestChromosome, Map<DepVariableWrapper, List<VariableReference>> map) {
		Map<String, PrimitiveStatement> inputVariables = new HashMap<>();
		Map<String, Object> inputConstants = new HashMap<>();
		for (DepVariable rootVar : rootVariables) {

			if(rootVar.isConstant()) {
				inputConstants.put(rootVar.getInstruction().toString(), getConstantObject(rootVar.getInstruction()));
				continue;
			}
			
			List<DepVariable> relevantPrimitiveChildren = rootVar.getAllChildrenNodesIncludingItself();
			List<PrimitiveStatement> relevantStatements = new ArrayList<>();
			for(DepVariable var: relevantPrimitiveChildren) {
				
				List<VariableReference> varList = map.get(new DepVariableWrapper(var));
				if(varList == null) continue;
				for(VariableReference ref: varList) {
					Statement statement = newTestChromosome.getTestCase().getStatement(ref.getStPosition()); 
					if(statement instanceof PrimitiveStatement) {
						relevantStatements.add((PrimitiveStatement)statement);
					}
					else if(statement instanceof ArrayStatement) {
						List<PrimitiveStatement> primitiveStatements = checkAllRelevantIndexes((ArrayStatement)statement);
						for(PrimitiveStatement pStat: primitiveStatements) {
							if(!relevantStatements.contains(pStat)) {
								relevantStatements.add(pStat);
							}
						}
					}
				}
			}
			
			for(int i=0; i<relevantStatements.size(); i++) {
				PrimitiveStatement inputStatement = relevantStatements.get(i);
				if(inputStatement instanceof NullStatement) 
					continue;
				inputVariables.put(String.valueOf(inputStatement.getPosition()), inputStatement);
			}
		}

		return new MethodInputs(inputVariables, inputConstants);
	}

	@SuppressWarnings("rawtypes")
	private static List<PrimitiveStatement> checkAllRelevantIndexes(ArrayStatement statement) {
		
		List<PrimitiveStatement> list = new ArrayList<>();
		
		TestCase test = statement.getTestCase();
		ArrayReference ref = statement.getArrayReference();
		for(int i=statement.getPosition(); i<test.size(); i++) {
			Statement pState = test.getStatement(i);
			if(pState instanceof AssignmentStatement) {
				AssignmentStatement aStat = (AssignmentStatement)pState;
				if(aStat.getReturnValue() instanceof ArrayIndex) {
					
					ArrayIndex index = (ArrayIndex)aStat.getReturnValue();
					if(index.getArray().equals(ref)) {
						Statement s = test.getStatement(aStat.getValue().getStPosition());
						if(s instanceof PrimitiveStatement) {
							list.add((PrimitiveStatement)s);
						}
						
					}
					
				}
			}
		}
		
		return list;
	}

	private static Statement isArrayStatement(DepVariable var, TestChromosome newTestChromosome,
			Map<DepVariableWrapper, List<VariableReference>> map) {
		Statement rootValueStatement = null;
		TestCase tc = newTestChromosome.getTestCase();

		List<VariableReference> methodCallParams = getMethodCallParams(tc);
		
		for (VariableReference paramRef : methodCallParams) {
			if(var.isParameter()) {
				if(paramRef != methodCallParams.get(var.getParamOrder() - 1))
					continue;
			}
			Statement relevantStatement = getStatementModifyVariable(paramRef);
			return relevantStatement;
		}
		return rootValueStatement;
	}

	public static Object getConstantObject(BytecodeInstruction ins) {
		switch (ins.getASMNode().getOpcode()) {
		case Opcodes.LDC:
			LdcInsnNode node = (LdcInsnNode) ins.getASMNode();
			return node.cst;
		case Opcodes.ICONST_0:
			return 0;
		case Opcodes.ICONST_1:
			return 1;
		case Opcodes.ICONST_2:
			return 2;
		case Opcodes.ICONST_3:
			return 3;
		case Opcodes.ICONST_4:
			return 4;
		case Opcodes.ICONST_5:
			return 5;
		case Opcodes.ICONST_M1:
			return -1;
		case Opcodes.LCONST_0:
			return 0l;
		case Opcodes.LCONST_1:
			return 1l;
		case Opcodes.DCONST_0:
			return 0.0;
		case Opcodes.DCONST_1:
			return 1.0;
		case Opcodes.FCONST_0:
			return 0f;
		case Opcodes.FCONST_1:
			return 1f;
		case Opcodes.FCONST_2:
			return 2f;
		case Opcodes.BIPUSH:
		case Opcodes.SIPUSH:
			IntInsnNode iNode = (IntInsnNode) ins.getASMNode();
			return iNode.operand;
		}
		return null;
	}

	public static ValuePreservance testBranchSensitivity(/* Set<FitnessFunction<?>> fitness, */
			Map<Branch, Set<DepVariable>> branchesInTargetMethod, Branch branch, ComputationPath path0) {
		TestCase test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
		if (test == null) {
			return new ValuePreservance(null, null);
		}

		System.currentTimeMillis();
		String methodCall = test.getStatement(test.size() - 1).toString();
		while (!methodCall.equals(Properties.TARGET_METHOD)) {
			test = SensitivityMutator.initializeTest(TestFactory.getInstance(), false);
			methodCall = test.getStatement(test.size() - 1).toString();
		}
		ConstructionPathSynthesizer synthensizer = new ConstructionPathSynthesizer(TestFactory.getInstance());
		try {
			synthensizer.constructDifficultObjectStatement(test, branch, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestChromosome oldTestChromosome = new TestChromosome();
		oldTestChromosome.setTestCase(test);
//		oldTestChromosome.setTestCase(test);
//		for (FitnessFunction<?> ff : fitness) {
//			oldTestChromosome.addFitness(ff);
//		}

		Map<Branch, List<ComputationPath>> branchWithBDChanged = parseComputationPaths(branch, branchesInTargetMethod);

		List<ComputationPath> paths = new ArrayList<ComputationPath>();
		if (path0 != null) {
			paths.add(path0);
		} else {
			paths = branchWithBDChanged.get(branch);
		}

		ValuePreservance preservingList = new ValuePreservance(null, null);
		if (paths == null) {
			return preservingList;
		}

		// Favor 'field' root variables
		List<ComputationPath> sortedPaths = new ArrayList<ComputationPath>();
		for (ComputationPath path : paths) {
			DepVariable rootVariable = path.getComputationNodes().get(0);
			if (rootVariable.isInstaceField() || rootVariable.isStaticField()) {
				sortedPaths.add(0, path);
			} else {
				sortedPaths.add(path);
			}
		}
		paths = sortedPaths;

		for (ComputationPath path : paths) {
			TestChromosome newTestChromosome = (TestChromosome) oldTestChromosome.clone();
			DepVariable rootVariable = path.getComputationNodes().get(0);
			BytecodeInstruction tailInstruction = path.getRelevantTailInstruction();

			List<BytecodeInstruction> observations = new ArrayList<>();
			observations.add(tailInstruction);
			List<DepVariable> vars = new ArrayList<>();
			vars.add(rootVariable);
			preservingList = checkPreservance(branch, newTestChromosome, vars, observations, synthensizer);
//			if (preservingList.isSensitivityPreserving() || preservingList.isValuePreserving()) {
//				return preservingList;
//			}
		}

		return preservingList;
	}

//	private static SensitivityPreservance testHeadTailValue(Branch branch, TestChromosome newTestChromosome, DepVariable rootVariable,
//			BytecodeInstruction tailInstruction, ConstructionPathSynthesizer synthensizer) {
//		SensitivityPreservance preservance = new SensitivityPreservance();
//		Map<DepVariableWrapper, List<VariableReference>> map = synthensizer.getGraph2CodeMap();
//		Statement relevantStatement = locateRelevantStatement(rootVariable, newTestChromosome, map);
//		Object headValue = retrieveHeadValue(relevantStatement);
//		long t1 = System.currentTimeMillis();
//		Object tailValue = evaluateTailValue(branch, tailInstruction, newTestChromosome);
//		long t2 = System.currentTimeMillis();
//		AbstractMOSA.getFirstTailValueTime += t2 - t1;
//		boolean valuePreserving = checkValuePreserving(headValue, tailValue);
//
//		HeadValue = headValue;
//		TailValue = tailValue;
//		preservance.addHead(headValue);
//		preservance.addTail(tailValue);
//
//		if (tailValue == null) {
//			return preservance;
//		}
//
//		if (relevantStatement == null) {
//			return preservance;
//		}
//
//		double valuePreservingNum = 0;
//		double sensivityPreservingNum = 0;
//		for (int i = 0; i < Properties.DYNAMIC_SENSITIVITY_THRESHOLD; i++) {
//			boolean isSuccessful = relevantStatement.mutate(newTestChromosome.getTestCase(), TestFactory.getInstance());
//			if (isSuccessful) {
//				relevantStatement = locateRelevantStatement(rootVariable, newTestChromosome, map);
//				Object newHeadValue = retrieveHeadValue(relevantStatement);
//				t1 = System.currentTimeMillis();
//				Object newTailValue = evaluateTailValue(branch, tailInstruction, newTestChromosome);
//				t2 = System.currentTimeMillis();
//				AbstractMOSA.all10MutateTime += t2 - t1;
//				preservance.addHead(newHeadValue);
//				preservance.addTail(newTailValue);
//				
//				valuePreserving = checkValuePreserving(newHeadValue, newTailValue);
//
//				boolean sensivityPreserving = false;
//				if (newTailValue == null || tailValue == null) {
//					sensivityPreserving = false;
//				} else if (newHeadValue == null || headValue == null) {
//					sensivityPreserving = !newTailValue.equals(tailValue);
//				} else {
//					sensivityPreserving = !newHeadValue.equals(headValue) && !newTailValue.equals(tailValue);
//				}
//
//				if (valuePreserving) {
//					valuePreservingNum += 1;
//				}
//
//				if (sensivityPreserving) {
//					sensivityPreservingNum += 1;
//				}
//			}
//		}
//		double vpRatio = valuePreservingNum / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
//		double spRatio = sensivityPreservingNum / Properties.DYNAMIC_SENSITIVITY_THRESHOLD;
//
//		preservance.sensivityPreserRatio = spRatio;
//		preservance.valuePreservingRatio = vpRatio;
//
//		return preservance;
//	}

	public static boolean checkValuePreserving(Object headValue, Object tailValue) {
		// TODO need to define the similarity of different value
		if (headValue == null || tailValue == null) {
			return false;
		}
		if (headValue.equals(tailValue))
			return true;
		// if headValue and tailValue are primitive type,compare the data value
		if ((headValue.getClass().isPrimitive() || isPrimitiveClass(headValue))
				&& (tailValue.getClass().isPrimitive() || isPrimitiveClass(tailValue))) {
			if (headValue instanceof Character) {
				headValue = Character.getNumericValue((char) headValue);
			}
			if (tailValue instanceof Character) {
				tailValue = Character.getNumericValue((char) tailValue);
			}
			Number head = (Number) headValue;
			Number tail = (Number) tailValue;
			if (Math.abs(head.longValue() - tail.longValue()) <= 10) {
				return true;
			}
			if (Math.abs(head.doubleValue() - tail.doubleValue()) <= 10) {
				return true;
			}
			return false;
		}
		// compare the similarity of string
		String head = headValue.toString();
		String tail = tailValue.toString();
		if (getSimilarityRatio(head, tail) >= Properties.VALUE_SIMILARITY_THRESHOLD) {
			return true;
		}
//		else {
//			int changedLength = 0;
//			for(int i = 0;i < preservance.headValues.size();i++) {
//				head = preservance.headValues.get(i).toString();
//				tail = preservance.tailValues.get(i).toString();
//				
//				if(i == 0)
//					changedLength = head.toCharArray().length - tail.toCharArray().length;
//				if(changedLength != head.toCharArray().length - tail.toCharArray().length)
//					return false;
//			}
//			if(preservance.headValues.size() != 0)
//				return true;			
//		}
		return headValue.equals(tailValue);
	}

	private static boolean isPrimitiveClass(Object object) {
		if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long
				|| object instanceof Float || object instanceof Double || object instanceof Character) {
			return true;
		}
		return false;
	}

	private static float getSimilarityRatio(String head, String tail) {
		// TODO Edit Distance
		if (head == null || tail == null)
			return 0;
		int max = Math.max(head.length(), tail.length());
		float score = 1 - (float) compare(head, tail) / max;

		/**
		 * normalize the score when the max length is too small, which results in that
		 * 0.5, 0.67, 0.75 are large score.
		 */
		if (max <= 3) {
			score = score * (1 + 1.0f / (float) max);
		}

		return score;
	}

	private static float compare(String head, String tail) {
		char ch1, ch2;
		int temp;
		if (head.length() == 0) {
			return tail.length();
		} else if (tail.length() == 0) {
			return head.length();
		}
		int d[][] = new int[head.length() + 1][tail.length() + 1];
		for (int i = 0; i <= head.length(); i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= tail.length(); j++) {
			d[0][j] = j;
		}
		for (int i = 1; i <= head.length(); i++) {
			ch1 = head.charAt(i - 1);
			for (int j = 1; j <= tail.length(); j++) {
				ch2 = tail.charAt(j - 1);
				if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) {
					temp = 0;
				} else {
					temp = 1;
				}
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
			}
		}
		return d[head.length()][tail.length()];
	}

	private static int min(int one, int two, int three) {
		return (one = one < two ? one : two) < three ? one : three;
	}

	private static Map<String, List<Object>> evaluateObservations(Branch branch, List<BytecodeInstruction> observations,
			TestChromosome newTestChromosome) {
		Set<FitnessFunction<?>> set = new HashSet<>();
		BranchCoverageTestFitness ff = BranchCoverageFactory.createBranchCoverageTestFitness(branch, true);
		set.add(ff);
		for (FitnessFunction<?> f : set) {
			newTestChromosome.addFitness(f);
		}
		FitnessFunction<Chromosome> fitness = searchForRelevantFitness(branch, newTestChromosome);

		InstrumentingClassLoader newClassLoader = new InstrumentingClassLoader(observations);

		for (BytecodeInstruction ins : observations) {
			DependencyAnalysis.addTargetClass(ins.getClassName());
		}
		for (Statement s : newTestChromosome.getTestCase()) {
			String className = s.getReturnType().getTypeName();
			if (!className.contains("."))
				continue;
			if (className.contains("[")) {
				className = className.substring(0, className.indexOf("["));
			}

			DependencyAnalysis.addTargetClass(className);
		}

		try {
			for (BytecodeInstruction ins : observations) {
				newClassLoader.loadClass(ins.getClassName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		RuntimeSensitiveVariable.observations.clear();
		for(BytecodeInstruction ins: observations) {
			RuntimeSensitiveVariable.observations.put(ins.toString(), new ArrayList<>());
		}
		
		System.currentTimeMillis();
		((DefaultTestCase) newTestChromosome.getTestCase()).changeClassLoader(newClassLoader);
		newTestChromosome.addFitness(fitness);
		newTestChromosome.clearCachedResults();
		fitness.getFitness(newTestChromosome);

		Map<String, List<Object>> res = new HashMap<>();
		for (String s : RuntimeSensitiveVariable.observations.keySet()) {
			res.put(s, RuntimeSensitiveVariable.observations.get(s));
		}

//		RuntimeSensitiveVariable.observations.clear();
		return res;

	}

	@SuppressWarnings("rawtypes")
	private static void retrieveHeadValue(Statement relevantStartment, TestChromosome newTestChromosome,
			List<PrimitiveStatement> headValues) {
		if (relevantStartment instanceof PrimitiveStatement) {
			PrimitiveStatement pStat = (PrimitiveStatement) relevantStartment;
			headValues.add(pStat);
//			return headValues;
		} else if (relevantStartment instanceof ArrayStatement) {
			// get array variable name
			ArrayStatement as = (ArrayStatement) relevantStartment;
			String code = as.getCode();
			int start = code.indexOf("[] ");
			int end = code.indexOf(" = ");
			String varName = as.getCode().substring(start + 3, end);

			List<Integer> lengths = as.getLengths();
			if (lengths.get(0) == 0) {
				return;
			}
			
			
			for (int k = 0; k < newTestChromosome.getTestCase().size(); k++) {
				Statement ss = newTestChromosome.getTestCase().getStatement(k);
				if (ss instanceof AssignmentStatement && ss.getCode().contains(varName + "[")
						&& ss.getCode().contains("] =")) {
					Statement rootValueStatement = getRootValueStatement(newTestChromosome.getTestCase(), ss);
					retrieveHeadValue(rootValueStatement, newTestChromosome, headValues);
				}
			}
		}else if(relevantStartment instanceof MethodStatement) {
			// get method statement 
			MethodStatement meSta = (MethodStatement)relevantStartment;
			String code = meSta.getCode();
			
			for (int k = 0; k < newTestChromosome.getTestCase().size(); k++) {
				Statement ss = newTestChromosome.getTestCase().getStatement(k);
				if (ss instanceof MethodStatement && ss.getCode().equals(code)) {
					Statement rootValueStatement = getRootValueStatement(newTestChromosome.getTestCase(), ss);
					retrieveHeadValue(rootValueStatement, newTestChromosome, headValues);
				}
			}
		}else if(relevantStartment instanceof AssignmentStatement) {
			// get method statement 
			AssignmentStatement asSta = (AssignmentStatement)relevantStartment;
			String code = asSta.getCode();
			
			for (int k = 0; k < newTestChromosome.getTestCase().size(); k++) {
				Statement ss = newTestChromosome.getTestCase().getStatement(k);
				if (ss instanceof AssignmentStatement && ss.getCode().equals(code)) {
					Statement rootValueStatement = getRootValueStatement(newTestChromosome.getTestCase(), ss);
					retrieveHeadValue(rootValueStatement, newTestChromosome, headValues);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static FitnessFunction<Chromosome> searchForRelevantFitness(Branch targetBranch,
			TestChromosome newTestChromosome) {
		Map<FitnessFunction<?>, Double> fitnessValues = newTestChromosome.getFitnessValues();
		for (FitnessFunction<?> ff : fitnessValues.keySet()) {
			if (ff instanceof BranchFitness) {
				BranchFitness bf = (BranchFitness) ff;
				if (bf.getBranchGoal().getBranch().equals(targetBranch)) {
					return (FitnessFunction<Chromosome>) ff;
				}
			}
		}

		return null;
	}

	// Returns the statement in the test case that modifies rootVariable
	private static Statement locateRelevantStatement(DepVariable rootVariable, TestChromosome newTestChromosome,
			Map<DepVariableWrapper, List<VariableReference>> map) {
		Statement rootValueStatement = null;
		TestCase tc = newTestChromosome.getTestCase();
				
		// Find root statement using object map
		DepVariableWrapper rootVarWrapper = new DepVariableWrapper(rootVariable);
		if (map != null) {
			List<VariableReference> varRefs = map.get(rootVarWrapper);
			
			if (varRefs != null) {
				int minPos = tc.size();
				for (VariableReference varRef : varRefs) {
					int pos = varRef.getStPosition();
					if (pos < minPos) {
						minPos = pos;
					}
				}
				System.currentTimeMillis();
				rootValueStatement = getRootValueStatement(tc, tc.getStatement(minPos));
				if (rootValueStatement != null)
					return rootValueStatement;
			}
		}

		if (rootVariable.isParameter()) {
			if (newTestChromosome.getTestCase().size() == 0)
				return rootValueStatement;
			int paramIndex = rootVariable.getParamOrder();
			List<VariableReference> methodCallParams = getMethodCallParams(tc);
			VariableReference paramRef = methodCallParams.get(paramIndex - 1);
			Statement relevantStatement = getStatementModifyVariable(paramRef);
			rootValueStatement = getRootValueStatement(tc, relevantStatement);

		} else if (rootVariable.isInstaceField() || rootVariable.isStaticField()) {
			Statement fieldStatement = getFieldStatement(tc, rootVariable);
			rootValueStatement = getRootValueStatement(tc, fieldStatement);
		}

		// rootVariable's statement not in test case, return root value statement of
		// a target method parameter
		if (rootValueStatement == null) {
			List<VariableReference> methodCallParams = getMethodCallParams(tc);
			for (VariableReference paramRef : methodCallParams) {
				Statement relevantStatement = getStatementModifyVariable(paramRef);
				rootValueStatement = getRootValueStatement(tc, relevantStatement);
				System.currentTimeMillis();
				if (rootValueStatement != null) {
					break;
				}
			}
		}

		System.currentTimeMillis();
		return rootValueStatement;
	}

	private static Statement getLastFieldStatement(TestCase testCase, DepVariable rootVariable) {
		for (int i = testCase.size() - 1; i >= 0; i--) {
			Statement statement = testCase.getStatement(i);
			if (statement instanceof MethodStatement) {
				if (statementModifiesFiledVar(statement, rootVariable)) {
					return statement;
				}
			}

		}
		return null;
	}

	private static boolean statementModifiesFiledVar(Statement statement, DepVariable rootVariable) {
		String statementFieldName = statement.getReturnValue().getName();
		FieldInsnNode inNode = (FieldInsnNode)rootVariable.getInstruction().getASMNode();
		String varOwner = inNode.owner;
		String filedName = rootVariable.getName();
		if(statement instanceof MethodStatement) {
			MethodStatement meSta = (MethodStatement) statement;
			String methodName = ((MethodStatement) statement).getMethodName().toLowerCase();
			String methodOwner = meSta.getMethod().getOwnerType().toString().split(" ")[1].replace(".", "/");
			if(methodOwner.equals(varOwner) && methodName.equals("set" + filedName)) {
				return true;
			}
		}
		String rootVarName = rootVariable.getName();
		return statementFieldName.equals(rootVarName) || statementFieldName.endsWith("." + rootVarName);
	}

	private static List<VariableReference> getMethodCallParams(TestCase testCase) {
		List<VariableReference> params = new ArrayList<>();

		Statement methodStatement = testCase.getStatement(testCase.size() - 1);
		if (methodStatement instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) methodStatement;

			// Ensure last statement is target method call
			String methodName = ms.getMethod().getNameWithDescriptor();
			if (methodName.equals(Properties.TARGET_METHOD)) {
				params = ms.getParameterReferences();
			}
		}

		return params;
	}

	private static Statement getRootValueStatement(TestCase testCase, Statement statement) {
		// 1. PrimitiveStatement: int int0 = (-2236);
		if (statement == null || statement instanceof PrimitiveStatement) {
			return statement;
		}

		// 2. ArrayStatement: int[] intArray0 = new int[0];
		if (statement instanceof ArrayStatement) {
			// mutate one random element

			// get array variable name
			ArrayStatement as = (ArrayStatement) statement;
			String code = as.getCode();
			int start = code.indexOf("[] ");
			int end = code.indexOf(" = ");
			String varName = as.getCode().substring(start + 3, end);

			// array dimension > 1 or array length == 0
			List<Integer> lengths = as.getLengths();
			if (lengths.size() != 1 || lengths.get(0) == 0) {
				return statement;
			}

			int indexToMutate = lengths.get(0) - 1;
			// chance to return array statement to mutate to change its length
			if (indexToMutate == lengths.get(0)) {
				return statement;
			}

			// return 1 random element root statement to mutate
			for (int i = 0; i < testCase.size(); i++) {
				Statement st = testCase.getStatement(i);
				if (st instanceof AssignmentStatement && st.getCode().contains(varName + "[" + indexToMutate + "] =")) {
					return getRootValueStatement(testCase, st);
				}
			}

			return statement;
		}

		// 3. ConstructorStatement: BooleanFlagExample1 booleanFlagExample1_0 = new
		// BooleanFlagExample1();
		// 4. MethodStatement: Integer integer11 =
		// booleanFlagExample1_0.targetM2(intArray0, int5);
		if (statement instanceof ConstructorStatement || statement instanceof MethodStatement) {
			// modify 1 random parameter
			EntityWithParametersStatement ps = (EntityWithParametersStatement) statement;
			List<VariableReference> params = ps.getParameterReferences();

			if (params.size() == 0) {
				return statement;
			}

			int indexToMutate = Randomness.nextInt(params.size());
			// chance to return whole constructor/method statement
			if (indexToMutate == params.size()) {
				return statement;
			}

			// return 1 random parameter root statement to mutate
			return getRootValueStatement(testCase, getStatementModifyVariable(params.get(indexToMutate)));
		}

		// 5. AssignmentStatement: booleanFlagExample1_0.hashValues = intArray0;
		if (statement instanceof AssignmentStatement) {
			AssignmentStatement as = (AssignmentStatement) statement;
			VariableReference rightVar = as.getValue();
			Statement rightVarStatement = testCase.getStatement(rightVar.getStPosition());

			return getRootValueStatement(testCase, rightVarStatement);
		}

		return statement;
	}

	private static Statement getStatementModifyVariable(VariableReference varRef) {
		return varRef.getTestCase().getStatement(varRef.getStPosition());
	}

	private static Statement getFieldStatement(TestCase testCase, DepVariable rootVariable) {
		for (int i = testCase.size() - 1; i >= 0; i--) {
			Statement statement = testCase.getStatement(i);
			if (statementModifiesVar(statement, rootVariable)) {
				return statement;
			}
		}
		return null;
	}

	private static boolean statementModifiesVar(Statement statement, DepVariable rootVariable) {
		String statementFieldName = statement.getReturnValue().getName();
		String rootVarName = rootVariable.getName();
		return statementFieldName.equals(rootVarName) || statementFieldName.endsWith("." + rootVarName);
	}

	private static Map<Branch, List<ComputationPath>> parseComputationPaths(Branch branch,
			Map<Branch, Set<DepVariable>> branchesInTargetMethod) {

		Map<Branch, List<ComputationPath>> map = new HashMap<>();

		Set<DepVariable> rootVaribles = branchesInTargetMethod.get(branch);
		for (DepVariable rootVar : rootVaribles) {
			List<ComputationPath> paths = ComputationPath.computePath(rootVar, branch);

			for (ComputationPath path : paths) {
				List<ComputationPath> pathList = map.get(branch);
				if (pathList == null) {
					pathList = new ArrayList<>();
				}

				if (!pathList.contains(path)) {
					pathList.add(path);
				}

				map.put(branch, pathList);
			}

		}

		return map;
	}

}
