package main.java.breakingPointTool.artifact;

import java.util.ArrayList;

import main.java.breakingPointTool.versions.Versions;


public class ProjectArtifact 
{
	private String projectName;
	private int numOfVersion;
	private ArrayList<Versions> versions;
	
	public ProjectArtifact ()
	{
		this.projectName = null;
		this.numOfVersion = 0;
		this.versions = new ArrayList<Versions>();
	}
	
	public void setNumOfVersions(int n)
	{
		this.numOfVersion = n;
	}
	
	public int getNumOfVersions()
	{
		return this.numOfVersion;
	}
	
	public void setprojectName(String projName)
	{
		this.projectName = projName;
	}
	
	public void setVersion(Versions v)
	{
		this.versions.add(v);
	}
	
	public String getProjectName()
	{
		return this.projectName;
	}
	
	public ArrayList<Versions> getVersions()
	{
		return versions;
	}

}
