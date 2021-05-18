package feature.smartseed.testcase;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.runtime.sandbox.Sandbox;
import org.junit.Before;
import org.junit.Test;

import common.SF100Project;
import evosuite.shell.EvoTestResult;
import sf100.CommonTestUtil;

public class SF100OverallTest {
	@Before
	public void beforeTest() {
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
//		Properties.APPLY_OBJECT_RULE = true;
		Properties.ADOPT_SMART_MUTATION = false;
		
		Properties.INSTRUMENT_CONTEXT = true;
		Properties.CHROMOSOME_LENGTH = 200;
		
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 0;
		
		Properties.TIMEOUT = 1000;
		
		Properties.ENABLE_TRACEING_EVENT = true;
		Properties.APPLY_SMART_SEED = true;
		Properties.APPLY_GRADEINT_ANALYSIS_IN_SMARTSEED = true;
//		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
	}
	

	@Test
	public void testBugExample1() {
		String projectId = SF100Project.P36;
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"edu.mscd.cs.jclo.JCLOTests#main([Ljava/lang/String;)V"//94
//				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.soops.CEN4010.JMCA.JParser.JavaCharStream#AdjustBuffSize()V" //19 
//				"com.soops.CEN4010.JMCA.JParser.SimpleNode#dump(Ljava/lang/String;Ljava/io/Writer;)V"//19
//				"jigcell.sbml2.ConservationRelationFinder#validateConservationRelations(Ljava/util/List;)Z"//34
//				"ghm.follow.font.FontSelectionPanel#getSelectedFontSize()I"//41
//				"glengineer.agents.ParallelGroupAgent#containsGapsOnly()Z"//40
//				"com.mytdev.resources4j.ResourcesInjector#setLocale(Ljava/util/Locale;)Lcom/mytdev/resources4j/ResourcesInjector;"//48
//				"ghm.follow.font.FontSelectionPanel#setSelectedFontFamily(Ljava/lang/String;)V"//41
//				"macaw.util.ValidationUtility#isEmptyValue(Ljava/lang/String;)Z"//69
//				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.densebrain.rif.server.transport.WebServiceDescriptor#equals(Ljava/lang/Object;)Z"//4
//				"com.mytdev.resources4j.ResourcesInjector#setLocale(Ljava/util/Locale;)Lcom/mytdev/resources4j/ResourcesInjector;"//48
//				"de.huxhorn.lilith.swing.ApplicationPreferences#resolveGroovyConditionScriptFile(Ljava/lang/String;)Ljava/io/File;"//43
//				"com.werken.saxpath.XPathReader#match(I)Lcom/werken/saxpath/Token;"//24
//				"Codes#getKeywordCode(Ljava/lang/String;)B"//50
//				"com.werken.saxpath.XPathReader#match(I)Lcom/werken/saxpath/Token;"//24
//				"com.mentorgen.tools.profile.instrument.PerfMethodAdapter#visitInsn(I)V"//51
//				"com.soops.CEN4010.JMCA.JParser.SimpleNode#dump(Ljava/lang/String;Ljava/io/Writer;)V"//19
//				"wheel.asm.ClassWriter#newFieldItem(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lwheel/asm/Item;"//80
//				"com.ib.client.EReader#readIntMax()I"//1
//				"fr.unice.gfarce.interGraph.SimpleTableDemo#getLigne(Ljava/lang/Object;)Ljava/lang/Object;"//8
//				"com.yahoo.platform.yui.compressor.YUICompressor#main([Ljava/lang/String;)V"//22 
//				"com.werken.saxpath.XPathLexer#nextToken()Lcom/werken/saxpath/Token;"//82
				"net.sourceforge.schemaspy.Config#isHighQuality()Z"//36
//				"de.huxhorn.lilith.swing.ApplicationPreferences#createCondition(Ljava/lang/String;Ljava/lang/String;)Lde/huxhorn/sulky/conditions/Condition;"//43
//				"glengineer.blocks.Scheme#recognizeVerticalGapsAtLines()V"//40
//				"ghm.follow.font.FontSelectionPanel#getSelectedFontSize()I"//41
				
		};
		
		int repeatTime = 5;
		int budget = 10000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
//		"generateMOSuite", "MOSUITE", "DynaMOSA",
//		"generateSuite", "Evosuite", "MONOTONIC_GA",
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample7() {
		String projectId = SF100Project.P22;
		String[] targetMethods = new String[]{
				"com.yahoo.platform.yui.compressor.YUICompressor#main([Ljava/lang/String;)V"//22 
				
		};
		
		int repeatTime = 5;
		int budget = 10000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
//		"generateMOSuite", "MOSUITE", "DynaMOSA",
//		"generateSuite", "Evosuite", "MONOTONIC_GA",
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample8() {
		String projectId = SF100Project.P43;
		String[] targetMethods = new String[]{
				"de.huxhorn.lilith.swing.ApplicationPreferences#createCondition(Ljava/lang/String;Ljava/lang/String;)Lde/huxhorn/sulky/conditions/Condition;"//43 
				
		};
		
		int repeatTime = 5;
		int budget = 10000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
//		"generateMOSuite", "MOSUITE", "DynaMOSA",
//		"generateSuite", "Evosuite", "MONOTONIC_GA",
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample2() {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = "58_fps370";
//		String projectId = "24_saxpath";
//		String projectId = "60_sugar";
		String projectId = SF100Project.P19;
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"edu.mscd.cs.jclo.JCLOTests#main([Ljava/lang/String;)V"//94
//				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.soops.CEN4010.JMCA.JParser.JavaCharStream#AdjustBuffSize()V" //19 
				"com.soops.CEN4010.JMCA.JParser.SimpleNode#dump(Ljava/lang/String;Ljava/io/Writer;)V"//19
//				"jigcell.sbml2.ConservationRelationFinder#validateConservationRelations(Ljava/util/List;)Z"//34
		};
		
		int repeatTime = 3;
		int budget = 10000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample3() {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = "58_fps370";
//		String projectId = "24_saxpath";
//		String projectId = "60_sugar";
		String projectId = SF100Project.P29;
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"edu.mscd.cs.jclo.JCLOTests#main([Ljava/lang/String;)V"//94
				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.soops.CEN4010.JMCA.JParser.JavaCharStream#AdjustBuffSize()V" //19 
//				"com.soops.CEN4010.JMCA.JParser.SimpleNode#dump(Ljava/lang/String;Ljava/io/Writer;)V"//19
//				"jigcell.sbml2.ConservationRelationFinder#validateConservationRelations(Ljava/util/List;)Z"//34
		};
		
		int repeatTime = 3;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample4() {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = "58_fps370";
//		String projectId = "24_saxpath";
//		String projectId = "60_sugar";
		String projectId = SF100Project.P34;
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"edu.mscd.cs.jclo.JCLOTests#main([Ljava/lang/String;)V"//94
//				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.soops.CEN4010.JMCA.JParser.JavaCharStream#AdjustBuffSize()V" //19 
//				"com.soops.CEN4010.JMCA.JParser.SimpleNode#dump(Ljava/lang/String;Ljava/io/Writer;)V"//19
				"jigcell.sbml2.ConservationRelationFinder#validateConservationRelations(Ljava/util/List;)Z"//34
		};
		
		int repeatTime = 3;
		int budget = 10000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample5() {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = "58_fps370";
//		String projectId = "24_saxpath";
//		String projectId = "60_sugar";
		String projectId = SF100Project.P94;
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
				"edu.mscd.cs.jclo.JCLOTests#main([Ljava/lang/String;)V"//94
//				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.soops.CEN4010.JMCA.JParser.JavaCharStream#AdjustBuffSize()V" //19 
//				"com.soops.CEN4010.JMCA.JParser.SimpleNode#dump(Ljava/lang/String;Ljava/io/Writer;)V"//19
//				"jigcell.sbml2.ConservationRelationFinder#validateConservationRelations(Ljava/util/List;)Z"//34
		};
		
		int repeatTime = 3;
		int budget = 10000;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testBugExample6() {
		
//		String projectId = "84_ifx-framework";
//		String projectId = "27_gangup";
//		String projectId = "83_xbus";
//		String projectId = "80_wheelwebtool";
//		String projectId = "58_fps370";
//		String projectId = "24_saxpath";
//		String projectId = "60_sugar";
		String projectId = SF100Project.P43;
		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.CreditAuthAddRsSequence2#equals(Ljava/lang/Object;)Z"
//				"net.sourceforge.ifxfv3.beans.CreditAuthModRsSequence2#equals(Ljava/lang/Object;)Z"
//				"edu.mscd.cs.jclo.JCLOTests#main([Ljava/lang/String;)V"//94
//				"apbs_mem_gui.FileEditor#getEnergy(Ljava/lang/String;)[Ljava/lang/String;"//29
//				"com.soops.CEN4010.JMCA.JParser.JavaCharStream#AdjustBuffSize()V" //19 
//				"jigcell.sbml2.ConservationRelationFinder#validateConservationRelations(Ljava/util/List;)Z"//34
				"de.huxhorn.lilith.data.logging.ExtendedStackTraceElement#parseStackTraceElement(Ljava/lang/String;)Lde/huxhorn/lilith/data/logging/ExtendedStackTraceElement;"
		};
		
		int repeatTime = 10;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		boolean aor = false;
		boolean ass = true;
		List<EvoTestResult> results = CommonTestUtil.evoTestSingleMethodSmartSeedProbability(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA", 0.5, 0.5, ass);
		
		double coverage = 0;
		double initCoverage = 0;
		double time = 0;
		double iteration  = 0;
		for(EvoTestResult res: results) {
			
			if(res == null) {
				repeatTime--;
				continue;
			}
			
			coverage += res.getCoverage();
			initCoverage += res.getInitialCoverage();
			time += res.getTime();
			iteration += res.getAge();
		}
		
		System.out.println("coverage: " + coverage/repeatTime);
		System.out.println("initCoverage: " + initCoverage/repeatTime);
		System.out.println("time: " + time/repeatTime);
		System.out.println("iteration: " + iteration/repeatTime);
		System.out.println("repeat: " + repeatTime);
	}
	
	@Test
	public void testProtectedExample() {
		String projectId = "10_water-simulator";
		String[] targetMethods = new String[]{
				"simulator.WSA.BehaviourQueryConsumers#handleInform(Ljade/lang/acl/ACLMessage;)V"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleDynaMOSA1() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRq#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleDynaMOSA2() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.MediaAcctAdjInqRs#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
	@Test
	public void testEqualExampleMonotonicGA() {
		String projectId = "84_ifx-framework";
		String[] targetMethods = new String[]{
				"net.sourceforge.ifxfv3.beans.AcctInqRq#equals(Ljava/lang/Object;)Z"
				};
		
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
		
		boolean aor = true;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, 
				seed, aor, "generateSuite", "Evosuite", "MONOTONIC_GA");
		EvoTestResult resultF = resultsF.get(0);
		
		System.currentTimeMillis();
	}
	
//	@Test
//	public void test11Example() {
//		String projectId = "11_imsmart";
//		String[] targetMethods = new String[]{
//				"com.momed.cms.MContentManagerFileNet#checkInContent(Ljava/io/File;)Z"
//				};
//		
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "branch";
//		
//		
//		boolean aor = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, 
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		EvoTestResult resultF = resultsF.get(0);
//		
//		System.currentTimeMillis();
//	}
//	
//	@Test
//	public void test20Example() {
//		String projectId = "20_nekomud";
//		String[] targetMethods = new String[]{
//				"net.sourceforge.nekomud.nio.Connection#handleRead(Ljava/nio/channels/SelectionKey;)V"
//				};
//		
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "branch";
//		
//		
//		boolean aor = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, 
//				seed, aor, "generateMOSuite", "MOSUITE", "DynaMOSA");
//		EvoTestResult resultF = resultsF.get(0);
//		
//		System.currentTimeMillis();
//	}
//	
//	
//	
	@Test
	public void testGangupBasicRulesExample() {
		String projectId = "27_gangup";
		String[] targetMethods = new String[]{
				"module.BasicRules#checkRules(Lstate/Action;Lstate/GameState;)Z"
				};
		
		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
		int repeatTime = 1;
		int budget = 100;
		Long seed = null;
		
		String fitnessApproach = "branch";
		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
		
		boolean aor = false;
		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
				targetMethods, fitnessApproach, repeatTime, budget, true, seed, aor);
//		
		
	}
//	
//	@Test
//	public void testCascadingCallExample() {
//		Class<?> clazz = regression.objectconstruction.testgeneration.example.cascadecall.CascadingCallExample.class;
//
//		String methodName = "targetM";
//		int parameterNum = 0;
//
//		String targetClass = clazz.getCanonicalName();
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);
//
//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
//		String cp = "target/test-classes";
//
//		String fitnessApproach = "fbranch";
//
//		int timeBudget = 100;
//		EvoTestResult resultT = null;
//		EvoTestResult resultF = null;
//
//		try {
//			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		} catch (Exception e) {
//			resultT = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		}
//
//		Properties.APPLY_OBJECT_RULE = false;
//		
//		try {
//			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		} catch (Exception e) {
//			resultF = TestUtility.evosuite(targetClass, targetMethod, cp, timeBudget, true, fitnessApproach);
//		}
//
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//
//		assert ageT <= 1;
//		assert timeT <= 1;
//		assert ageT < ageF;
//		assert timeT <= timeF;
//		assert coverageT == 1.0;
//	}
//	
//	// pending debugging
//	@Test
//	public void testCorinaPngencoderExample() {
//		String projectId = "35_corina";
//		String[] targetMethods = new String[]{
//				"corina.map.PngEncoderB#pngEncode(Z)[B"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = 1590296724051L;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testDsachatFrameExample() {
//		String projectId = "12_dsachat";
//		String[] targetMethods = new String[]{
//				"dsachat.gm.gui.InternalGmHeroFrame#valueChanged(Ljavax/swing/event/TreeSelectionEvent;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = 1590297701552L;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	
//	
//	@Test
//	public void testJfxExample() {
//		String projectId = "84_ifx-framework";
//		String[] targetMethods = new String[]{
//				"net.sourceforge.ifxfv3.beans.ChkOrdAudRs_TypeSequence2#equals(Ljava/lang/Object;)Z"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testJhandballMovesExample() {
//		String projectId = "56_jhandballmoves";
//		String[] targetMethods = new String[]{
//				"visu.handball.moves.model.HandballModel#setMoveName(Ljava/lang/String;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testJwbfArticleExample() {
//		String projectId = "23_jwbf";
//		String[] targetMethods = new String[]{
//				"net.sourceforge.jwbf.core.contentRep.Article#getRevisionId()Ljava/lang/String;"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testLilithExample() {
//		String projectId = "43_lilith";
//		String[] targetMethods = new String[]{
//				"de.huxhorn.lilith.data.logging.protobuf.LoggingEventProtobufDecoder#convert(Lde/huxhorn/lilith/data/logging/protobuf/generated/LoggingProto$LoggingEvent;)Lde/huxhorn/lilith/data/logging/LoggingEvent;"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testObjectexplorerExample() {
//		String projectId = "63_objectexplorer";
//		String[] targetMethods = new String[]{
//				"de.paragon.explorer.figure.ListBoxFigureBuilder#setStaticAttributesVisible(Lde/paragon/explorer/figure/ListBoxFigure;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testOpenhreExample() {
//		String projectId = "75_openhre";
//		String[] targetMethods = new String[]{
//				"com.browsersoft.openhre.hl7.impl.regular.ExpressionNodeList#addExpressionList(Lcom/browsersoft/openhre/hl7/impl/regular/ExpressionNodeList;)V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testOpenjmsExample() {
//		String projectId = "66_openjms";
//		String[] targetMethods = new String[]{
//				"org.exolab.jms.net.rmi.RMIManagedConnectionAcceptor#close()V"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testSummaExample() {
//		String projectId = "44_summa";
//		String[] targetMethods = new String[]{
//				"dk.statsbiblioteket.summa.common.filter.object.MUXFilter#pump()Z"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testWekaArjarchiveentryExample() {
//		String projectId = "101_weka";
//		String[] targetMethods = new String[]{
//				"org.apache.commons.compress.archivers.arj.ArjArchiveEntry#isDirectory()Z"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
//	
//	@Test
//	public void testXbusFtpsenderExample() {
//		String projectId = "83_xbus";
//		String[] targetMethods = new String[]{
//				"net.sf.xbus.technical.ftp.FTPSender#execute(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
//				};
//		
//		List<EvoTestResult> resultsT = new ArrayList<EvoTestResult>();
//		List<EvoTestResult> resultsF = new ArrayList<EvoTestResult>();
//		int repeatTime = 1;
//		int budget = 100;
//		Long seed = null;
//		
//		String fitnessApproach = "fbranch";
//		
//		resultsT = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		Properties.APPLY_OBJECT_RULE = false;
//		resultsF = CommonTestUtil.evoTestSingleMethod(projectId,  
//				targetMethods, fitnessApproach, repeatTime, budget, true, seed);
//		
//		EvoTestResult resultT = resultsT.get(0);
//		EvoTestResult resultF = resultsF.get(0);
//		
//		int ageT = resultT.getAge();
//		int timeT = resultT.getTime();
//		double coverageT = resultT.getCoverage();
//		int ageF = resultF.getAge();
//		int timeF = resultF.getTime();
//		double coverageF = resultF.getCoverage();
//		
//		assert ageT < ageF;
//		assert timeT < timeF;
//		assert coverageT > coverageF;
//	}
	
}
