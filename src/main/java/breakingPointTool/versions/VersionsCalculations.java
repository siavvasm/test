package main.java.breakingPointTool.versions;

import java.util.ArrayList;

import main.java.breakingPointTool.artifact.ClassMetrics;
import main.java.breakingPointTool.artifact.PackageMetrics;


public class VersionsCalculations 
{
	
	private ArrayList<ArrayList<PackageMetrics>> packageList;
	private ArrayList<ArrayList<ClassMetrics>> classList;
	
	public VersionsCalculations()
	{
		this.packageList = new ArrayList<ArrayList<PackageMetrics>>();
		this.classList = new ArrayList<ArrayList<ClassMetrics>>();
	}
	
	public void setPackageInList(ArrayList<PackageMetrics> pm)
	{
		this.packageList.add(pm);
	}
	
	public void setClassInList(ArrayList<ClassMetrics> cm)
	{
		this.classList.add(cm);
	}
	
	public ArrayList<ArrayList<PackageMetrics>> getPackageList()
	{
		return this.packageList;
	}
	
	public ArrayList<ArrayList<ClassMetrics>> getClassList()
	{
		return this.classList;
	}
	
}
