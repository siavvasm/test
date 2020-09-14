package tests.java.breakingPointTool.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import main.java.breakingPointTool.externalTools.MetricsCalculator;
import main.java.breakingPointTool.externalTools.RippleEffectChangeProneness;
import main.java.breakingPointTool.externalTools.SemiCalculator;

class externalTools {

	@Test
	void Semi() throws IOException, InterruptedException 
	{
		String projectName = "Neurasmus";
		String language = "C++";
		String path = "/Users/angelikitsintzira/Desktop/BreakingPoint";
		String path2 = "/Users/angelikitsintzira/Desktop/BreakingPoint/configurations.txt";
		SemiCalculator semi = new SemiCalculator();
		semi.executeSemiCalculator(language, 0, path, projectName, path2);
		
		File f = new File("clusters.txt");
		assertTrue(f.exists());
	}
	
	@Test
	void MetricsCalculator() throws IOException, InterruptedException 
	{
		String projectName = "HolisunArassistance";
		String jarName = projectName;
		String path = "/Users/angelikitsintzira/Desktop/BreakingPoint";
		MetricsCalculator metricsCalc = new MetricsCalculator();
		metricsCalc.executeOneVersion(jarName, 0, path, projectName);
		
		File f = new File("output0.csv");
		assertTrue(f.exists());
	}
	
	@Test
	void InterestProbability() throws IOException, InterruptedException 
	{
		String projectName = "HolisunArassistance";
		String jarName = projectName;
		String path = "/Users/angelikitsintzira/Desktop/BreakingPoint";
		RippleEffectChangeProneness rem = new RippleEffectChangeProneness();
		rem.ExtractJar(jarName, 0, path, projectName);
		
		File f = new File("rem_and_cpm_metrics_classLevel.csv");
		assertTrue(f.exists());
		
		f = new File("rem_and_cpm_metrics_packageLevel.csv");
		assertTrue(f.exists());
	}

}
