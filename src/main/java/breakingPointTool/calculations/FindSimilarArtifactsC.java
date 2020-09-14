package main.java.breakingPointTool.calculations;

import java.util.ArrayList;

import main.java.breakingPointTool.artifact.FileMetricsC;
import main.java.breakingPointTool.artifact.PackageMetricsC;
import main.java.breakingPointTool.artifact.ProjectArtifact;

public class FindSimilarArtifactsC 
{
	// Class level
	private FileMetricsC className;
	private ArrayList<FileMetricsC> otherClasses;  
	private ArrayList<Double> similarity;
	private ArrayList<FileMetricsC> similarClassesList;
	private ArrayList<Double> similarClassesSimilarity;

	// Package level
	private PackageMetricsC packageName;
	private ArrayList<PackageMetricsC> otherPackages;  
	private ArrayList<Double> similarityPackage;
	private ArrayList<PackageMetricsC> similarPackagesList;
	private ArrayList<Double> similarPackagesSimilarity;

	public FindSimilarArtifactsC()
	{
		this.className = null;
		this.otherClasses = new ArrayList<FileMetricsC>();
		this.similarity = new ArrayList<Double>();
		this.similarClassesList = new ArrayList<FileMetricsC>();
		this.similarClassesSimilarity = new ArrayList<Double>();

		this.packageName = null;
		this.otherPackages = new ArrayList<PackageMetricsC>();  
		this.similarityPackage = new ArrayList<Double>();
		this.similarPackagesList = new ArrayList<PackageMetricsC>();
		this.similarPackagesSimilarity = new ArrayList<Double>();
	}

	public ArrayList<FileMetricsC> findAllClasses(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<FileMetricsC> classes = new ArrayList<FileMetricsC>();

		if (typeOfAnalysis == 2)
			version = 0;

		for (int j = 0; j < p.getVersions().get(version).getPackagesC().size(); j++)
		{
			for (int z = 0; z < p.getVersions().get(version).getPackagesC().get(j).getClassInProject().size(); z++)
			{
				//System.out.println("Check class: " + p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getClassName());
				if (!classes.contains(p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getClassName()))
				{
					classes.add(p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z));
				}
			}
		}

		return classes;		
	}

	public ArrayList<PackageMetricsC> findAllPackages(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<PackageMetricsC> packages = new ArrayList<PackageMetricsC>();

		if (typeOfAnalysis == 2)
			version = 0;

		for (int j = 0; j < p.getVersions().get(version).getPackagesC().size(); j++)
		{
			if (!packages.contains(p.getVersions().get(version).getPackagesC().get(j).getPackageName()))
			{
				packages.add(p.getVersions().get(version).getPackagesC().get(j));
			}
		}

		return packages;		
	}

	public ArrayList<FindSimilarArtifactsC>  calculateSimilarityForClasses(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<FileMetricsC> classes = findAllClasses(p, version, typeOfAnalysis);
		ArrayList<FindSimilarArtifactsC> similarClassesList = new ArrayList<FindSimilarArtifactsC>();

		if (typeOfAnalysis == 2)
			version = 0;

		for (int i = 0; i < classes.size(); i++)
		{
			String investigatedClass = classes.get(i).getClassName();
			//System.out.println("Investigated: " + investigatedClass);
			FindSimilarArtifactsC s = new FindSimilarArtifactsC();
			s.setInvestigatedClass(classes.get(i));

			for (int j = 0; j < p.getVersions().get(version).getPackagesC().size(); j++)
			{
				//System.out.println("#########");
				for (int z = 0; z < p.getVersions().get(version).getPackagesC().get(j).getClassInProject().size(); z++)
				{
					if (!investigatedClass.equals(p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getClassName()))
					{
						//System.out.println("Other class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
						s.setOtherClasses(p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z));
						//double numOfClassSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getNumOfClasses(), p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getNumOfClasses());
						//double complexitySimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getComplexity(), p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getComplexity());
						double numfOfFunctionsSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getNumOfFunctions(), p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getNumOfFunctions());
						double commentsDensity = calculateSimilarityBetweenMetrics(classes.get(i).getCommentsDensity(),p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getCommentsDensity());

						double nclocSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getNcloc(), p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getNcloc());
						double statementsSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getStatements(), p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getStatements());
						double technicalDebtSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getTD(),p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getTD());

						double similarityValue = commentsDensity + numfOfFunctionsSimilarity + nclocSimilarity
								+ statementsSimilarity + technicalDebtSimilarity;
						//System.out.println("check class similarity: " + p.getVersions().get(version).getPackagesC().get(j).getClassInProject().get(z).getClassName());
						//System.out.println("metrics check similarity: " + numOfClassSimilarity + " " + complexitySimilarity + " " + numfOfFunctionsSimilarity + " "
						//	+ nclocSimilarity+" " + statementsSimilarity + " " + technicalDebtSimilarity);
						similarityValue = similarityValue / 5;
						s.setSimilarity(similarityValue);

					}
				}	

			}
			similarClassesList.add(s);		
		}

		findTheFiveMostSimilar(similarClassesList);
		return similarClassesList ;
	}

	public ArrayList<FindSimilarArtifactsC>  calculateSimilarityForPackages(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<PackageMetricsC> packages = findAllPackages(p, version, typeOfAnalysis);
		ArrayList<FindSimilarArtifactsC> similarPackagesList = new ArrayList<FindSimilarArtifactsC>();

		if (typeOfAnalysis == 2)
			version = 0;

		for (int i = 0; i < packages.size(); i++)
		{
			String investigatedPackage = packages.get(i).getPackageName();
			//System.out.println("Investigated: " + investigatedClass);
			FindSimilarArtifactsC s = new FindSimilarArtifactsC();
			s.setInvestigatedPackage(packages.get(i));

			for (int j = 0; j < p.getVersions().get(version).getPackagesC().size(); j++)
			{
				if (!investigatedPackage.equals(p.getVersions().get(version).getPackagesC().get(j).getPackageName()))
				{
					//System.out.println("Other class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
					s.setOtherPackages(p.getVersions().get(version).getPackagesC().get(j));
					//double numOfClassSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getNumOfClasses(), p.getVersions().get(version).getPackagesC().get(j).getNumOfClasses());
					//double complexitySimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getComplexity(), p.getVersions().get(version).getPackagesC().get(j).getComplexity());
					double numfOfFunctionsSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getNumOfFunctions(), p.getVersions().get(version).getPackagesC().get(j).getNumOfFunctions());
					double commentsDensity = calculateSimilarityBetweenMetrics(packages.get(i).getCommentsDensity(),p.getVersions().get(version).getPackagesC().get(j).getCommentsDensity());

					double nclocSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getNcloc(), p.getVersions().get(version).getPackagesC().get(j).getNcloc());
					double statementsSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getStatements(), p.getVersions().get(version).getPackagesC().get(j).getStatements());
					double technicalDebtSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getTD(),p.getVersions().get(version).getPackagesC().get(j).getTD());

					double similarityValue = commentsDensity + numfOfFunctionsSimilarity + nclocSimilarity
							+ statementsSimilarity + technicalDebtSimilarity;
					/*System.out.println("check class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
						System.out.println("metrics check: " + numOfClassSimilarity + " " + complexitySimilarity + " " + numfOfFunctionsSimilarity + " "
								+ nclocSimilarity+" " + statementsSimilarity + " " + technicalDebtSimilarity);*/
					similarityValue = similarityValue / 5;
					s.setSimilarityPackage(similarityValue);
				}		
			}
			similarPackagesList.add(s);		
		}

		findTheFiveMostSimilarPackages(similarPackagesList);
		return similarPackagesList;
	}	

	private double calculateSimilarityBetweenMetrics(double metric1, double metric2) 
	{
		double Similarity = 0;
		if (metric1 != 0 && metric2 != 0) 
		{
			Similarity = 100 - (Math.abs(metric1 - metric2) / Math.max(metric1, metric2) * 100);
		}
		return Similarity;
	}

	public void findTheFiveMostSimilar(ArrayList<FindSimilarArtifactsC> similarClasses)
	{	

		for (int i = 0; i < similarClasses.size(); i++)
		{	
			//System.out.println("Size of other classes: " + similarClasses.get(i).getOtherClasses().size());
			for (int j = 1; j < similarClasses.get(i).getOtherClasses().size(); j++)
			{
				int k = j-1;
				double key = similarClasses.get(i).getSimilarity().get(j);
				FileMetricsC  p = similarClasses.get(i).getOtherClasses().get(j);
				
				while (k >= 0 && similarClasses.get(i).getSimilarity().get(k) > key)
				{
					similarClasses.get(i).getSimilarity().set(k + 1, similarClasses.get(i).getSimilarity().get(k));
					similarClasses.get(i).getOtherClasses().set(k + 1, similarClasses.get(i).getOtherClasses().get(k));
					k = k - 1;
				}
				similarClasses.get(i).getSimilarity().set(k +1, key);
				similarClasses.get(i).getOtherClasses().set(k + 1, p);
			}
		}

		for (int i = 0; i < similarClasses.size(); i++)
		{
			/*System.out.println("For Class: " + similarClasses.get(i).getName().getClassName());
			System.out.println("The most similar packages are: " + similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 1).getClassName()
					+ " with similarity: " + similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-1)
					+ "\n" + similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 2).getClassName()
			+ " with similarity: " + similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-2)
			+ "\n" + similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 3).getClassName()
			+ " with similarity: " + similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-3)
			+ "\n" + similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 4).getClassName()
			+ " with similarity: " + similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-4)
			+ "\n" + similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 5).getClassName()
			+ " with similarity: " + similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-5)
			+ "\n");*/

			for (int k = 1; k < similarClasses.get(i).getOtherClasses().size() + 1; k++)
			{
				similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - k));
				similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-k));
			}

			/*similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 1));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 2));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 3));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 4));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 5));

			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-1));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-2));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-3));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-4));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-5));		*/	
		}	

	}

	public void findTheFiveMostSimilarPackages(ArrayList<FindSimilarArtifactsC> similarPackages)
	{
		// This function sorts arrays, does not return the 5 most similar
		for (int i = 0; i < similarPackages.size(); i++)
		{			
			for (int j = 1; j < similarPackages.get(i).getOtherPackages().size(); j++)
			{
				int k = j-1;
				double key = similarPackages.get(i).getSimilarityPackage().get(j);
				PackageMetricsC  p = similarPackages.get(i).getOtherPackages().get(j);
				while (k >= 0 && similarPackages.get(i).getSimilarityPackage().get(k) > key)
				{
					similarPackages.get(i).getSimilarityPackage().set(k + 1, similarPackages.get(i).getSimilarityPackage().get(k));
					similarPackages.get(i).getOtherPackages().set(k + 1, similarPackages.get(i).getOtherPackages().get(k));
					k = k - 1;
				}
				similarPackages.get(i).getSimilarityPackage().set(k +1, key);
				similarPackages.get(i).getOtherPackages().set(k + 1, p);
			}
		}

		for (int i = 0; i < similarPackages.size(); i++)
		{
			/*System.out.println("For Package: " + similarPackages.get(i).getPackage().getPackageName());
			System.out.println("The most similar packages are: " + similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 1).getPackageName()
					+ " with similarity: " + similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-1)
					+ "\n" + similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 2).getPackageName()
			+ " with similarity: " + similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-2)
			+ "\n" + similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 3).getPackageName()
			+ " with similarity: " + similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-3)
			+ "\n" + similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 4).getPackageName()
			+ " with similarity: " + similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-4)
			+ "\n" + similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 5).getPackageName()
			+ " with similarity: " + similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-5)
			+ "\n");*/
			for (int k = 1; k < similarPackages.get(i).getOtherPackages().size() + 1; k++)
			{
				similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - k));
				similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-k));
			}

			/*similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 1));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 2));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 3));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 4));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 5));

			similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-1));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-2));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-3));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-4));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-5));	*/		
		}	

	}

	public void setInvestigatedClass(FileMetricsC cl) 
	{
		this.className = cl;
	}

	public void setOtherClasses(FileMetricsC cl)
	{
		this.otherClasses.add(cl);
	}

	public void setSimilarity(double sim)
	{
		this.similarity.add(sim);
	}

	public void setSimilarClass(FileMetricsC s)
	{
		this.similarClassesList.add(s);
	}

	public void setSimilarClassSimilarity(Double s)
	{
		this.similarClassesSimilarity.add(s);
	}

	public FileMetricsC getName()
	{
		return this.className;
	}

	public ArrayList<FileMetricsC> getOtherClasses()
	{
		return this.otherClasses;
	}

	public ArrayList<Double> getSimilarity()
	{
		return this.similarity;
	}

	public ArrayList<FileMetricsC> getSimilarClasses()
	{
		return this.similarClassesList;
	}

	public ArrayList<Double> getSimilarClassesSimilarity()
	{
		return this.similarClassesSimilarity;
	}

	public void setInvestigatedPackage(PackageMetricsC pk) 
	{
		this.packageName = pk;
	}

	public void setOtherPackages(PackageMetricsC pk)
	{
		this.otherPackages.add(pk);
	}

	public void setSimilarityPackage(double sim)
	{
		this.similarityPackage.add(sim);
	}

	public void setSimilarPackage(PackageMetricsC s)
	{
		this.similarPackagesList.add(s);
	}

	public void setSimilarPackageSimilarity(Double s)
	{
		this.similarPackagesSimilarity.add(s);
	}

	public PackageMetricsC getPackage()
	{
		return this.packageName;
	}

	public ArrayList<PackageMetricsC> getOtherPackages()
	{
		return this.otherPackages;
	}

	public ArrayList<Double> getSimilarityPackage()
	{
		return this.similarityPackage;
	}

	public ArrayList<PackageMetricsC> getSimilarPackages()
	{
		return this.similarPackagesList;
	}

	public ArrayList<Double> getSimilarPackagesSimilarity()
	{
		return this.similarPackagesSimilarity;
	}

}
