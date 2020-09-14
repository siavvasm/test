package main.java.breakingPointTool.calculations;

import java.sql.SQLException;
import java.util.ArrayList;

import main.java.breakingPointTool.artifact.FileMetricsC;
import main.java.breakingPointTool.artifact.PackageMetricsC;
import main.java.breakingPointTool.database.DatabaseGetData;
import main.java.breakingPointTool.database.DatabaseSaveDataC;

public class ResultsC 
{
	private static final double MINUTES_IN_HOUR = 60.0;
	private static final double HOURLY_LINES_OF_CODE = 25.0;
	private static final double HOURLY_WAGE = 45.81;

	private double interest;
	private double principal;
	private double breakingPoint;

	private double fitnessValueLinesOfCode;
	private double fitnessValueComplexity;
	private double fitnessValueCoupling;
	private double fitnessValueCohesion;
	//private double fitnessValueNumOfFunctions;
	//private double fitnessValueCommentsDensity;

	public ResultsC()
	{
		this.interest = 0;
		this.principal = 0;
		this.breakingPoint = 0;
	}

	public void calculateInterest(FileMetricsC investigatedClass, OptimalArtifactC optimalClass, int version) throws SQLException
	{
		// Interest calculation based on lines of code, coupling, cohesion, cyclomatic complexity
		double k = investigatedClass.getAverageNocChange();

		this.fitnessValueLinesOfCode = calculateFitnessValueMin(optimalClass.getLinesOfCode(), investigatedClass.getNcloc());
		this.fitnessValueComplexity = calculateFitnessValueMin(optimalClass.getComplexity(), investigatedClass.getComplexity());
		this.fitnessValueCohesion = calculateFitnessValueMin(optimalClass.getCohesion(), investigatedClass.getCohesion());
		this.fitnessValueCoupling = calculateFitnessValueMin(optimalClass.getCoupling(), investigatedClass.getCoupling());

		this.interest = this.fitnessValueLinesOfCode + this.fitnessValueComplexity + this.fitnessValueCohesion +  this.fitnessValueCoupling;

		this.interest = this.interest / 4;
		this.interest = 1  - this.interest ;
		this.interest = this.interest * k;  	
		this.interest = (this.interest / HOURLY_LINES_OF_CODE) * HOURLY_WAGE;
		System.out.println("Class Name: " + investigatedClass.getClassName());
		System.out.println("Interest is: " + interest);
		System.out.println("K: " + k);
		System.out.println("Interest in currency: " + interest);

		calculatePrincipal(investigatedClass);
		calculateBreakingPoint();

		if (Double.isInfinite(this.breakingPoint))
		{
			// It means infinity
			this.breakingPoint = -1;
		}
		else if (Double.isNaN(breakingPoint))
		{
			// It means it is not defined
			this.breakingPoint = -2;
		}

		double rate = calculateInterestProbability(investigatedClass.getClassName(), version);
		DatabaseSaveDataC  saveDataInDatabase = new DatabaseSaveDataC ();
		System.out.println("Before saved in database: " + investigatedClass.getClassName() + " " + version + " " +
				this.breakingPoint + " " + this.principal + " " + this.interest + " " + k + " " + rate);
		saveDataInDatabase.saveBreakingPointInDatabase(investigatedClass.getClassName(), version, this.breakingPoint, this.principal, this.interest, k, rate);
		saveDataInDatabase.updatePrincipal(investigatedClass.getClassName(), version, this.principal);

	}

	public void calculateInterestPackage(PackageMetricsC investigatedPackage, OptimalArtifactC optimalClass, int version) throws SQLException
	{
		double k = investigatedPackage.getAverageNocChange();

		System.out.println("Investigate package: ");
		System.out.println(investigatedPackage.getNcloc() + " " + investigatedPackage.getComplexity() +
				" " + investigatedPackage.getCohesion() + " " + investigatedPackage.getCoupling());

		System.out.println("Optimal package: ");
		System.out.println(optimalClass.getLinesOfCode() + " " + optimalClass.getComplexity() +
				" " + optimalClass.getCohesion() + " " + optimalClass.getCoupling());


		this.fitnessValueLinesOfCode = calculateFitnessValueMin(optimalClass.getLinesOfCode(), investigatedPackage.getNcloc());
		this.fitnessValueComplexity = calculateFitnessValueMin(optimalClass.getComplexity(), investigatedPackage.getComplexity());
		this.fitnessValueCohesion = calculateFitnessValueMin(optimalClass.getCohesion(), investigatedPackage.getCohesion());
		this.fitnessValueCoupling = calculateFitnessValueMin(optimalClass.getCoupling(), investigatedPackage.getCoupling());

		this.interest = this.fitnessValueLinesOfCode + this.fitnessValueComplexity + this.fitnessValueCohesion +  this.fitnessValueCoupling;

		this.interest = this.interest / 4;
		this.interest = 1  - this.interest ;
		this.interest = this.interest * k;  	
		this.interest = (this.interest / HOURLY_LINES_OF_CODE) * HOURLY_WAGE;

		System.out.println("Package Name: " + investigatedPackage.getPackageName());
		System.out.println("Interest is: " + interest);
		System.out.println("K: " + k);
		System.out.println("Interest is in money: " + interest);

		calculatePrincipalPackage(investigatedPackage);
		calculateBreakingPoint();

		if (Double.isInfinite(this.breakingPoint))
		{
			// It means infinity
			this.breakingPoint = -1;
		}
		else if (Double.isNaN(breakingPoint))
		{
			// It means it is not defined
			this.breakingPoint = -2;
		}

		double rate = calculateInterestProbability(investigatedPackage.getPackageName(), version);
		DatabaseSaveDataC saveDataInDatabase = new DatabaseSaveDataC();
		System.out.println("Before saved in database: " + investigatedPackage.getPackageName() + " " + version + " " +
				this.breakingPoint + " " + this.principal + " " + this.interest + " " + k + " " + rate);
		saveDataInDatabase.saveBreakingPointInDatabase(investigatedPackage.getPackageName(), version, this.breakingPoint, this.principal, this.interest, k, rate);
		saveDataInDatabase.updatePrincipal(investigatedPackage.getPackageName(), version, this.principal);

	}
	
	public void calculateInterestOnePackage(PackageMetricsC investigatedPackage, String projectName, int version) throws SQLException
	{
		ArrayList<Double> interests = new ArrayList<Double>();

		DatabaseGetData dbCall = new DatabaseGetData();
		interests.addAll(dbCall.getInterestForArtifactC(projectName, version));
		
		for (int i = 0; i < interests.size()-1; i++ )
		{
			this.interest =  this.interest +  interests.get(i);
		}
		
		double k = investigatedPackage.getAverageNocChange();

		System.out.println("----- Only one package -----");
		System.out.println("Package Name: " + investigatedPackage.getPackageName());
		System.out.println("Interest is: " + interest);
		System.out.println("K: " + k);
		
		calculatePrincipalPackage(investigatedPackage);
		calculateBreakingPoint();

		if (Double.isInfinite(this.breakingPoint))
		{
			// It means infinity
			this.breakingPoint = -1;
		}
		else if (Double.isNaN(breakingPoint))
		{
			// It means it is not defined
			this.breakingPoint = -2;
		}

		double rate = calculateInterestProbability(investigatedPackage.getPackageName(), version);
		
		DatabaseSaveDataC saveDataInDatabase = new DatabaseSaveDataC();
		System.out.println("Before saved in database: " + investigatedPackage.getPackageName() + " " + version + " " +
				this.breakingPoint + " " + this.principal + " " + this.interest + " " + k + " " + rate);
		saveDataInDatabase.saveBreakingPointInDatabase(investigatedPackage.getPackageName(), version, this.breakingPoint, this.principal, this.interest, k, rate);
		saveDataInDatabase.updatePrincipal(investigatedPackage.getPackageName(), version, this.principal);
		
		
	}

	public double calculateInterestProbability(String artifact, int version)
	{
		ArrayList<Double> locs = new ArrayList<Double>();
		double flag = 0.0;
		// Get k values from database
		DatabaseGetData dbCall = new DatabaseGetData();
		locs.addAll(dbCall.getLoCForArtifactC(artifact, version));

		// bug when version are less than full

		if (locs.size() == 0 || locs.size() == 1)
			return 0.0;

		if (locs.size() == 2)
		{
			double diff = locs.get(1)  - locs.get(0);

			if (diff != 0)
				return 1.0;  		
			else
				return 0.0;
		}

		for (int i = 0; i < locs.size()-1; i++ )
		{
			double diff = locs.get(i + 1) - locs.get(i);

			if (diff != 0)
			{
				flag++;
			}
		}

		return flag / (locs.size()-1);

	}

	public void calculatePrincipalPackage(PackageMetricsC testedClass)
	{
		this.principal = (testedClass.getTD() / MINUTES_IN_HOUR ) * HOURLY_WAGE;
		System.out.println("Principal is: " + principal);
	}

	public void calculatePrincipal(FileMetricsC testedClass)
	{
		this.principal = (testedClass.getTD() / MINUTES_IN_HOUR ) * HOURLY_WAGE;
		System.out.println("Principal is: " + principal);
	}

	public void calculateBreakingPoint()
	{
		//TODO maybe i should change NaN to a number, lets say -1
		this.breakingPoint = this.principal / this.interest;
		System.out.println("Breaking point is: " + breakingPoint + "\n");
	}

	public double  calculateFitnessValueMin (double optimal, double actual)
	{
		if (optimal == 0)
			return 0;
		else 
			return optimal / actual;
	}

	public double  calculateFitnessValueMax (double optimal, double actual)
	{
		if (optimal == 0)
			return 0;
		else 
			return actual / optimal;
	}

	public double getPrincipal()
	{
		return this.principal;
	}

	public double getInterest()
	{
		return this.interest;
	}

	public double getBreakingPoint()
	{
		return this.breakingPoint;
	}
}
