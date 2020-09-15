package main.java.breakingPointTool.calculations;

import java.util.ArrayList;

import main.java.breakingPointTool.artifact.ClassMetrics;
import main.java.breakingPointTool.artifact.PackageMetrics;
import main.java.breakingPointTool.artifact.ProjectArtifact;

public class FindSimilarArtifacts 
{
	// Class level
	private ClassMetrics className;
	private ArrayList<ClassMetrics> otherClasses;  
	private ArrayList<Double> similarity;
	private ArrayList<ClassMetrics> similarClassesList;
	private ArrayList<Double> similarClassesSimilarity;
	
	// Package level
	private PackageMetrics packageName;
	private ArrayList<PackageMetrics> otherPackages;  
	private ArrayList<Double> similarityPackage;
	private ArrayList<PackageMetrics> similarPackagesList;
	private ArrayList<Double> similarPackagesSimilarity;
	
	public FindSimilarArtifacts()
	{
		this.className = null;
		this.otherClasses = new ArrayList<ClassMetrics>();
		this.similarity = new ArrayList<Double>();
		this.similarClassesList = new ArrayList<ClassMetrics>();
		this.similarClassesSimilarity = new ArrayList<Double>();
		
		this.packageName = null;
		this.otherPackages = new ArrayList<PackageMetrics>();  
		this.similarityPackage = new ArrayList<Double>();
		this.similarPackagesList = new ArrayList<PackageMetrics>();
		this.similarPackagesSimilarity = new ArrayList<Double>();
	}
	
	public ArrayList<ClassMetrics> findAllClasses(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<ClassMetrics> classes = new ArrayList<ClassMetrics>();
		
		if (typeOfAnalysis == 2)
			version = 0;

		for (int j = 0; j < p.getVersions().get(version).getPackages().size(); j++)
		{
			for (int z = 0; z < p.getVersions().get(version).getPackages().get(j).getClassInProject().size(); z++)
			{
				if (!classes.contains(p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName()))
				{
					classes.add(p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z));
				}
			}
		}

		return classes;		
	}

	public ArrayList<PackageMetrics> findAllPackages(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<PackageMetrics> packages = new ArrayList<PackageMetrics>();
		
		if (typeOfAnalysis == 2)
			version = 0;
		
		for (int j = 0; j < p.getVersions().get(version).getPackages().size(); j++)
		{
			if (!packages.contains(p.getVersions().get(version).getPackages().get(j).getPackageName()))
			{
				packages.add(p.getVersions().get(version).getPackages().get(j));
			}
		}

		return packages;		
	}
	
	public ArrayList<FindSimilarArtifacts>  calculateSimilarityForClasses(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<ClassMetrics> classes = findAllClasses(p, version, typeOfAnalysis);
		ArrayList<FindSimilarArtifacts> similarClassesList = new ArrayList<FindSimilarArtifacts>();
		
		if (typeOfAnalysis == 2)
			version = 0;

		for (int i = 0; i < classes.size(); i++)
		{
			String investigatedClass = classes.get(i).getClassName();
			//System.out.println("Investigated: " + investigatedClass);
			FindSimilarArtifacts s = new FindSimilarArtifacts();
			s.setInvestigatedClass(classes.get(i));
			
			for (int j = 0; j < p.getVersions().get(version).getPackages().size(); j++)
			{
				//System.out.println("#########");
				for (int z = 0; z < p.getVersions().get(version).getPackages().get(j).getClassInProject().size(); z++)
				{
					if (!investigatedClass.equals(p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName()))
					{
						//System.out.println("Other class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
						s.setOtherClasses(p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z));
						double numOfClassSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getNumOfClasses(), p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getNumOfClasses());
						double complexitySimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getComplexity(), p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getComplexity());
						double numfOfFunctionsSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getFunctions(), p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getFunctions());
						double nclocSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getNcloc(), p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getNcloc());
						double statementsSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getStatements(), p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getStatements());
						double technicalDebtSimilarity = calculateSimilarityBetweenMetrics(classes.get(i).getTD(),p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getTD());
						double similarityValue = numOfClassSimilarity + complexitySimilarity + numfOfFunctionsSimilarity + nclocSimilarity
								+ statementsSimilarity + technicalDebtSimilarity;
						/*System.out.println("check class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
						System.out.println("metrics check: " + numOfClassSimilarity + " " + complexitySimilarity + " " + numfOfFunctionsSimilarity + " "
								+ nclocSimilarity+" " + statementsSimilarity + " " + technicalDebtSimilarity);*/
						similarityValue = similarityValue / 6;
						s.setSimilarity(similarityValue);
					}
				}	
				//System.out.println("-----");			
			}
			similarClassesList.add(s);		
		}

		findTheFiveMostSimilar(similarClassesList);
		return similarClassesList ;
	}
	
	public ArrayList<FindSimilarArtifacts>  calculateSimilarityForPackages(ProjectArtifact p, int version, int typeOfAnalysis)
	{
		ArrayList<PackageMetrics> packages = findAllPackages(p, version, typeOfAnalysis);
		ArrayList<FindSimilarArtifacts> similarPackagesList = new ArrayList<FindSimilarArtifacts>();
		
		if (typeOfAnalysis == 2)
			version = 0;


		for (int i = 0; i < packages.size(); i++)
		{
			String investigatedPackage = packages.get(i).getPackageName();
			//System.out.println("Investigated: " + investigatedClass);
			FindSimilarArtifacts s = new FindSimilarArtifacts();
			s.setInvestigatedPackage(packages.get(i));
			
			for (int j = 0; j < p.getVersions().get(version).getPackages().size(); j++)
			{
					if (!investigatedPackage.equals(p.getVersions().get(version).getPackages().get(j).getPackageName()))
					{
						//System.out.println("Other class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
						s.setOtherPackages(p.getVersions().get(version).getPackages().get(j));
						double numOfClassSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getNumOfClasses(), p.getVersions().get(version).getPackages().get(j).getNumOfClasses());
						double complexitySimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getComplexity(), p.getVersions().get(version).getPackages().get(j).getComplexity());
						double numfOfFunctionsSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getFunctions(), p.getVersions().get(version).getPackages().get(j).getFunctions());
						double nclocSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getNcloc(), p.getVersions().get(version).getPackages().get(j).getNcloc());
						double statementsSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getStatements(), p.getVersions().get(version).getPackages().get(j).getStatements());
						double technicalDebtSimilarity = calculateSimilarityBetweenMetrics(packages.get(i).getTD(),p.getVersions().get(version).getPackages().get(j).getTD());
						double similarityValue = numOfClassSimilarity + complexitySimilarity + numfOfFunctionsSimilarity + nclocSimilarity
								+ statementsSimilarity + technicalDebtSimilarity;
						/*System.out.println("check class: " + p.getVersions().get(version).getPackages().get(j).getClassInProject().get(z).getClassName());
						System.out.println("metrics check: " + numOfClassSimilarity + " " + complexitySimilarity + " " + numfOfFunctionsSimilarity + " "
								+ nclocSimilarity+" " + statementsSimilarity + " " + technicalDebtSimilarity);*/
						similarityValue = similarityValue / 6;
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
	
	public void findTheFiveMostSimilar(ArrayList<FindSimilarArtifacts> similarClasses)
	{	
		for (int i = 0; i < similarClasses.size(); i++)
		{			
			for (int j = 1; j < similarClasses.get(i).getOtherClasses().size(); j++)
			{
				int k = j-1;
				double key = similarClasses.get(i).getSimilarity().get(j);
				ClassMetrics  p = similarClasses.get(i).getOtherClasses().get(j);
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
				
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 1));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 2));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 3));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 4));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(similarClasses.get(i).getOtherClasses().size() - 5));
			
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-1));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-2));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-3));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-4));
			similarClasses.get(i).setSimilarClassSimilarity(similarClasses.get(i).getSimilarity().get(similarClasses.get(i).getSimilarity().size()-5));			
		}	
	}
	
	public void findTheFiveMostSimilarPackages(ArrayList<FindSimilarArtifacts> similarPackages)
	{	
		for (int i = 0; i < similarPackages.size(); i++)
		{			
			for (int j = 1; j < similarPackages.get(i).getOtherPackages().size(); j++)
			{
				int k = j-1;
				double key = similarPackages.get(i).getSimilarityPackage().get(j);
				PackageMetrics  p = similarPackages.get(i).getOtherPackages().get(j);
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
				
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 1));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 2));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 3));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 4));
			similarPackages.get(i).setSimilarPackage(similarPackages.get(i).getOtherPackages().get(similarPackages.get(i).getOtherPackages().size() - 5));
			
			similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-1));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-2));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-3));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-4));
		    similarPackages.get(i).setSimilarPackageSimilarity(similarPackages.get(i).getSimilarityPackage().get(similarPackages.get(i).getSimilarityPackage().size()-5));			
		}	
	}
	
	public void setInvestigatedClass(ClassMetrics cl) 
	{
		this.className = cl;
	}
	
	public void setOtherClasses(ClassMetrics cl)
	{
		this.otherClasses.add(cl);
	}
	
	public void setSimilarity(double sim)
	{
		this.similarity.add(sim);
	}
	
	public void setSimilarClass(ClassMetrics s)
	{
		this.similarClassesList.add(s);
	}
	
	public void setSimilarClassSimilarity(Double s)
	{
		this.similarClassesSimilarity.add(s);
	}
	
	public ClassMetrics getName()
	{
		return this.className;
	}
	
	public ArrayList<ClassMetrics> getOtherClasses()
	{
		return this.otherClasses;
	}
	
	public ArrayList<Double> getSimilarity()
	{
		return this.similarity;
	}
	
	public ArrayList<ClassMetrics> getSimilarClasses()
	{
		return this.similarClassesList;
	}
	
	public ArrayList<Double> getSimilarClassesSimilarity()
	{
		return this.similarClassesSimilarity;
	}
	
	public void setInvestigatedPackage(PackageMetrics pk) 
	{
		this.packageName = pk;
	}
	
	public void setOtherPackages(PackageMetrics pk)
	{
		this.otherPackages.add(pk);
	}
	
	public void setSimilarityPackage(double sim)
	{
		this.similarityPackage.add(sim);
	}
	
	public void setSimilarPackage(PackageMetrics s)
	{
		this.similarPackagesList.add(s);
	}
	
	public void setSimilarPackageSimilarity(Double s)
	{
		this.similarPackagesSimilarity.add(s);
	}
	
	public PackageMetrics getPackage()
	{
		return this.packageName;
	}
	
	public ArrayList<PackageMetrics> getOtherPackages()
	{
		return this.otherPackages;
	}
	
	public ArrayList<Double> getSimilarityPackage()
	{
		return this.similarityPackage;
	}
	
	public ArrayList<PackageMetrics> getSimilarPackages()
	{
		return this.similarPackagesList;
	}
	
	public ArrayList<Double> getSimilarPackagesSimilarity()
	{
		return this.similarPackagesSimilarity;
	}

}
