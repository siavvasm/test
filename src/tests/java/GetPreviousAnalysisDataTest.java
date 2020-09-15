package tests.java;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

import main.java.breakingPointTool.artifact.ClassMetrics;
import main.java.breakingPointTool.artifact.FileMetricsC;
import main.java.breakingPointTool.artifact.PackageMetrics;
import main.java.breakingPointTool.artifact.PackageMetricsC;
import main.java.breakingPointTool.database.GetAnalysisDataJava;

class GetPreviousAnalysisDataTest {

	/*
	 * Tests for class GetAnalysisData
	 */
	
	@Test
	  public void testGetAnalysisResultsJava() throws Exception 
	  {
			String projectName = "Holisun Arassistance";
			String className = "com/holisun/arassistance/adapters/ChatAdapter";
			String packageName = "com/holisun/arassistance";
			
			int version = 3;

			GetAnalysisDataJava classMetrics = new GetAnalysisDataJava();
			classMetrics.getAnalysisDataBPTJava(projectName, "FIL", Integer.toString(version));
			classMetrics.getAnalysisDataPrincipalSonar(projectName, "FIL", version);
			
			HashMap<String, ClassMetrics> cl = classMetrics.getClassMetricsMap();
			
			//Check that two objects are equal and size
		    assertEquals(cl.get(className).getCodeSmells(), 4);
		    assertEquals(cl.get(className).getAverageLocChange(), 0.33333334);
		    assertEquals(cl.get(className).getSize1(), 38);
			
			GetAnalysisDataJava packageMetrics = new GetAnalysisDataJava();
			packageMetrics.getAnalysisDataBPTJava(projectName, "DIR", Integer.toString(version));
			packageMetrics.getAnalysisDataPrincipalSonar(projectName, "DIR", version);
			
			HashMap<String, PackageMetrics> pk = packageMetrics.getPackageMetricsMap();
			
			//Check that two objects are equal and size
			assertEquals(pk.get(packageName).getCodeSmells(), 8);
		    assertEquals(pk.get(packageName).getAverageLocChange(), 5.6666665);
		    assertEquals(pk.get(packageName).getSize1(), 51);
			
	  }
	
	@Test
	public void testGetAnalysisResultsC() throws Exception 
	  {
			String projectName = "Neurasmus";
			String className = "imd-emulator/sec_primitives.cpp";
			String packageName = "imd-emulator";
			
			int version = 3;

			// Get metrics from Database from previous version
			GetAnalysisDataJava classMetrics = new GetAnalysisDataJava();
			classMetrics.getAnalysisDataC(projectName, "FIL", version);
			classMetrics.getAnalysisDataBPTC(projectName, "FIL", version);
			
			HashMap<String, FileMetricsC> cl = classMetrics.getClassMetricsCMap();
			
			//Check that two objects are equal and size
			assertEquals(cl.get(className).getCodeSmells(), 34);
			assertEquals(cl.get(className).getAverageNocChange(), 4);
			assertEquals(cl.get(className).getNcloc(), 36);
			
			GetAnalysisDataJava packageMetrics = new GetAnalysisDataJava();				
			packageMetrics.getAnalysisDataC(projectName, "DIR", version);
			packageMetrics.getAnalysisDataBPTC(projectName, "DIR", version);
				
			HashMap<String, PackageMetricsC> pk = packageMetrics.getPackageMetricsCMap();
			
			//Check that two objects are equal and size
			assertEquals(pk.get(packageName).getCodeSmells(), 667);
		    assertEquals(pk.get(packageName).getAverageNocChange(), 162.33333);
		    assertEquals(pk.get(packageName).getNcloc(), 1070);
	  }

}
