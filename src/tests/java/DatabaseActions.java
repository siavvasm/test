package tests.java;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import main.java.breakingPointTool.database.DatabaseGetData;

class DatabaseActions {

	/*
	 * Test for class DatabaseGetData 
	 */

	@Test
	public void testDBGetDataJava() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "HolisunArassistance";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		ArrayList<String> kees = new ArrayList<String>();
		kees = dbCall.getProjectsKees();

		//Check that two objects are equal
		assertEquals(kees.get(0), "arassistance0");
	}

	@Test
	public void testDBGetClassesJava() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "HolisunArassistance";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		dbCall.getClassesForProject(projectName, dbCall.getProjectsKees().get(0));

		ArrayList<String> classes = new ArrayList<String>();

		classes = dbCall.getClassesId();

		//Check that two objects are equal
		assertEquals(classes.size(),24);
	}

	@Test
	public void testDBGetCPackagesJava() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "HolisunArassistance";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		dbCall.getDirectoriesForProject(projectName, dbCall.getProjectsKees().get(0));

		ArrayList<String> packages = new ArrayList<String>();

		packages = dbCall.getPackagesId();

		//Check that two objects are equal
		assertEquals(packages.size(),7);
	}


	@Test
	public void testDBGetKArtifact() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "HolisunArassistance";
		String className = "com/holisun/arassistance/adapters/ChatAdapter";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		ArrayList<Double> k = new ArrayList<Double>();

		k = dbCall.getKForArtifact(className);

		//Check that two objects are equal
		assertEquals(k.get(4), 0.25);

		assertEquals(k.size(), 5);
	}

	@Test
	public void testDBGetKArtifactC() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "Neurasmus";
		String className = "imd-emulator";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		ArrayList<Double> k = new ArrayList<Double>();

		k = dbCall.getKForArtifactC(className);

		//Check that two objects are equal
		assertEquals(k.get(8), 104.375);

		assertEquals(k.size(), 9);
	}

	@Test
	public void testDBGeLocKArtifact() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "HolisunArassistance";
		String className = "com/holisun/arassistance/adapters/ChatAdapter";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		ArrayList<Double> loc = new ArrayList<Double>();

		loc = dbCall.getLoCForArtifact(className, 3);

		//Check that two objects are equal
		assertEquals(loc.get(3), 38);

		assertEquals(loc.size(), 4);
	}

	@Test
	public void testDBGeLocKArtifactC() throws Exception 
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		String projectName = "Neurasmus";
		String className = "imd-emulator";
		DatabaseGetData dbCall = new DatabaseGetData(projectName);	

		ArrayList<Double> loc = new ArrayList<Double>();

		loc = dbCall.getLoCForArtifactC(className, 3);

		//Check that two objects are equal
		assertEquals(loc.get(3), 1070);

		assertEquals(loc.size(), 4);
	}
	
	@Test
	public void getData() throws IOException, InstantiationException, IllegalAccessException
	{
		fileConfiguration f = new fileConfiguration();
		f.readConfigurationFile();

		DatabaseGetData dbCall = new DatabaseGetData();	
		dbCall.clearData();
	}


}
