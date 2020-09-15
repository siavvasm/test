package main.java.breakingPointTool.calculations;

import java.util.ArrayList;

import main.java.breakingPointTool.artifact.PackageMetrics;

public class FindSimilarPackages 
{
	private PackageMetrics packageName;
	private ArrayList<PackageMetrics> otherPackages;  
	private ArrayList<Double> similarity;
	private ArrayList<PackageMetrics> similarPackagesList;
	private ArrayList<Double> similarPackagesSimilarity;
	
	public FindSimilarPackages()
	{
		this.packageName = null;
		this.otherPackages = new ArrayList<PackageMetrics>();
		this.similarity = new ArrayList<Double>();
		this.similarPackagesList = new ArrayList<PackageMetrics>();
		this.similarPackagesSimilarity = new ArrayList<Double>();
	}
	
	public ArrayList<FindSimilarPackages> calculateSimilarityForArtifacts(ArrayList<PackageMetrics> packagemetrics)
	{
		ArrayList<FindSimilarPackages> similarPackagesList = new ArrayList<FindSimilarPackages>();
		// For every class find their similar
		for (int i = 0; i < packagemetrics.size(); i++)
		{
			FindSimilarPackages similarPackage = new FindSimilarPackages();
			similarPackage.setInvestigatedPackage(packagemetrics.get(i));
			// Check all classes to find similar
			for (int j = 0; j < packagemetrics.size(); j++)
			{
				// Don't check similarity with itself
				if (i != j)
				{
					similarPackage.setOtherPackages(packagemetrics.get(j));
					double numOfClassSimilarity = calculateSimilarityBetweenMetrics(packagemetrics.get(i).getNumOfClasses(), packagemetrics.get(j).getNumOfClasses());
					double complexitySimilarity = calculateSimilarityBetweenMetrics(packagemetrics.get(i).getComplexity(), packagemetrics.get(j).getComplexity());
					double numfOfFunctionsSimilarity = calculateSimilarityBetweenMetrics(packagemetrics.get(i).getFunctions(), packagemetrics.get(j).getFunctions());
					double nclocSimilarity = calculateSimilarityBetweenMetrics(packagemetrics.get(i).getNcloc(), packagemetrics.get(j).getNcloc());
					double statementsSimilarity = calculateSimilarityBetweenMetrics(packagemetrics.get(i).getStatements(), packagemetrics.get(j).getStatements());
					double technicalDebtSimilarity = calculateSimilarityBetweenMetrics(packagemetrics.get(i).getTD(), packagemetrics.get(j).getTD());
					double similarity = numOfClassSimilarity + complexitySimilarity + numfOfFunctionsSimilarity + nclocSimilarity
							+ statementsSimilarity + technicalDebtSimilarity;
					similarity = similarity / 6;
					similarPackage.setSimilarity(similarity);
				}
				
			}
			similarPackagesList.add(similarPackage);
		}

		findTheFiveMostSimilar(similarPackagesList);
		return similarPackagesList;
	}
	
	private double calculateSimilarityBetweenMetrics(double metric1, double metric2) 
	{
        double similarity = 0;
        if (metric1 != 0 && metric2 != 0) 
        {
            similarity = 100 - (Math.abs(metric1 - metric2) / (double) Math.max(metric1, metric2) * 100);
        }
        return similarity;
    }
	
	public void findTheFiveMostSimilar(ArrayList<FindSimilarPackages> similarPackages)
	{
/*		
		for (int i = 0; i < similarClasses.size(); i++)
		{
			FindSimilarPackages currentClass = similarClasses.get(i);
			currentClass.sort();
			for(int j = 0; j <5; j ++) {
				currentClass.similarPackagesList.add(currentClass.otherPackages.get(j));
				currentClass.similarPackagesSimilarity.add(currentClass.similarity.get(j));
			}
			similarClasses.set(i, currentClass);
		}*/
		for (int i = 0; i < similarPackages.size(); i++)
		{
			int index1 = 0;
			double value1 = 0;
			int index2 = 0;
			double value2 = 0;
			int index3 = 0;
			double value3 = 0;
			int index4 = 0;
			double value4 = 0;
			int index5 = 0;
			double value5 = 0;
			for (int j = 0; j < similarPackages.get(i).getOtherPackages().size(); j++)
			{
				if (similarPackages.get(i).getSimilarity().get(j) > value1)
				{
					value1 = similarPackages.get(i).getSimilarity().get(j);
					index1 = j;
				}
				else if (similarPackages.get(i).getSimilarity().get(j) > value2)
				{
					value2 = similarPackages.get(i).getSimilarity().get(j);
					index2 = j;
				}
				else if (similarPackages.get(i).getSimilarity().get(j) > value3)
				{
					value3 = similarPackages.get(i).getSimilarity().get(j);
					index3 = j;
				}
				else if (similarPackages.get(i).getSimilarity().get(j) > value4)
				{
					value4 = similarPackages.get(i).getSimilarity().get(j);
					index4 = j;
				}
				else if (similarPackages.get(i).getSimilarity().get(j) > value5)
				{
					value5 = similarPackages.get(i).getSimilarity().get(j);
					index5 = j;
				}
			}
			
			System.out.println("For package: " + similarPackages.get(i).getName().getPackageName() +
					" the 5 most similar packages are: " + similarPackages.get(i).getOtherPackages().get(index1).getPackageName()
					+ " with similarity : " + value1 + "\n"
					+ similarPackages.get(i).getOtherPackages().get(index2).getPackageName()
					+ " with similarity : " + value2 + "\n" + 
					similarPackages.get(i).getOtherPackages().get(index3).getPackageName()
					+ " with similarity : " + value3 + "\n" +
					similarPackages.get(i).getOtherPackages().get(index4).getPackageName()
					+ " with similarity : " + value4 + "\n" + 
					similarPackages.get(i).getOtherPackages().get(index5).getPackageName()
					+ " with similarity : " + value5 + "\n");
			
			similarPackages.get(i).setSimilarPackages(similarPackages.get(i).getOtherPackages().get(index1));
			similarPackages.get(i).setSimilarPackages(similarPackages.get(i).getOtherPackages().get(index2));
			similarPackages.get(i).setSimilarPackages(similarPackages.get(i).getOtherPackages().get(index3));
			similarPackages.get(i).setSimilarPackages(similarPackages.get(i).getOtherPackages().get(index4));
			similarPackages.get(i).setSimilarPackages(similarPackages.get(i).getOtherPackages().get(index5));
			
			similarPackages.get(i).setSimilarPackageSimilarity(value1);
			similarPackages.get(i).setSimilarPackageSimilarity(value2);
			similarPackages.get(i).setSimilarPackageSimilarity(value3);
			similarPackages.get(i).setSimilarPackageSimilarity(value4);
			similarPackages.get(i).setSimilarPackageSimilarity(value5);
		}
	}
	
	public FindSimilarPackages sort()
	{
		for(int i = 0 ; i < this.otherPackages.size()-1; i ++)
		{
			for(int  j =1; j < this.otherPackages.size(); j++)
			{
				if (this.similarity.get(i) < this.similarity.get(j))
				{
					PackageMetrics temp= this.otherPackages.get(j);
					this.otherPackages.set(j,this.otherPackages.get(i));
					this.otherPackages.set(i, temp);
					
					Double tempD= this.similarity.get(j);
					this.similarity.set(j,this.similarity.get(i));
					this.similarity.set(i, tempD);
				}
			}
		}
		return this;
	}
	
	public void setInvestigatedPackage(PackageMetrics cl) 
	{
		this.packageName = cl;
	}
	
	public void setOtherPackages(PackageMetrics cl)
	{
		this.otherPackages.add(cl);
	}
	
	public void setSimilarity(double sim)
	{
		this.similarity.add(sim);
	}
	
	public void setSimilarPackages(PackageMetrics s)
	{
		this.similarPackagesList.add(s);
	}
	
	public void setSimilarPackageSimilarity(Double s)
	{
		this.similarPackagesSimilarity.add(s);
	}
	
	public PackageMetrics getName()
	{
		return this.packageName;
	}
	
	public ArrayList<PackageMetrics> getOtherPackages()
	{
		return this.otherPackages;
	}
	
	public ArrayList<Double> getSimilarity()
	{
		return this.similarity;
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
