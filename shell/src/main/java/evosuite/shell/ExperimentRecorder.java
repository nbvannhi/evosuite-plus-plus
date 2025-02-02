package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;

import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.CommonUtility;

import evosuite.shell.experiment.SFConfiguration;

public class ExperimentRecorder {
	private String successfulMethodsFile;
	
	public ExperimentRecorder() {
		successfulMethodsFile = Settings.getReportFolder() + "/successfulMethods.txt";
		FileUtils.writeFile(successfulMethodsFile, "#Project " + projectId + "\n", false);
	}

	public void logSuccessfulMethods(String className, String methodName) {
		FileUtils.writeFile(successfulMethodsFile, CommonUtility.getMethodId(className, methodName) + "\n", true);
	}


	public void record(String className, String methodName, EvoTestResult result) {
		// override when needed.
	}

	public void recordError(String className, String methodName, Exception e) {
		// override when needed.
	}
	
	public void recordEndIterations(String methodName, String className) {
		// override when needed.
	}

	public void record(String className, String methodName, TestGenerationResult r) {
		// override when needed.
	}
	
	public void recordSeedingToJson(String className, String methodName, EvoTestResult r) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void recordEvoSeedTime(String className, String methodName, EvoTestResult r) {
		// TODO Auto-generated method stub
		
	}

	public void recordCoverageOnDiffTime(String className, String methodName, EvoTestResult result) {
		// TODO Auto-generated method stub
		
	}
}
