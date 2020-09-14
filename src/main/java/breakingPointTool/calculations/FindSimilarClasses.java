package main.java.breakingPointTool.calculations;

import java.util.ArrayList;

import main.java.breakingPointTool.artifact.ClassMetrics;


public class FindSimilarClasses 
{
	private ClassMetrics className;
	private ArrayList<ClassMetrics> otherClasses;  
	private ArrayList<Double> similarity;
	private ArrayList<ClassMetrics> similarClassesList;
	private ArrayList<Double> similarClassesSimilarity;
	
	public FindSimilarClasses()
	{
		this.className = null;
		this.otherClasses = new ArrayList<ClassMetrics>();
		this.similarity = new ArrayList<Double>();
		this.similarClassesList = new ArrayList<ClassMetrics>();
		this.similarClassesSimilarity = new ArrayList<Double>();
	}
	
	public ArrayList<FindSimilarClasses> calculateSimilarityForArtifacts(ArrayList<ClassMetrics> classmetrics)
	{		
		ArrayList<FindSimilarClasses> similarClassesList = new ArrayList<FindSimilarClasses>();
		// For every class find their similar -- > calculation for 1 version, need iteration in order to have 2 or more versions
		for (int i = 0; i < classmetrics.size(); i++)
		{
			FindSimilarClasses similarClass = new FindSimilarClasses();
			similarClass.setInvestigatedClass(classmetrics.get(i));
			//System.out.println("incestigated: " + classmetrics.get(i).getClassName());
			// Check all classes to find similar
			for (int j = 0; j < classmetrics.size(); j++)
			{
				// Don't check similarity with itself
				if (i != j)
				{
					similarClass.setOtherClasses(classmetrics.get(j));
					double numOfClassSimilarity = calculateSimilarityBetweenMetrics(classmetrics.get(i).getNumOfClasses(), classmetrics.get(j).getNumOfClasses());
					double complexitySimilarity = calculateSimilarityBetweenMetrics(classmetrics.get(i).getComplexity(), classmetrics.get(j).getComplexity());
					double numfOfFunctionsSimilarity = calculateSimilarityBetweenMetrics(classmetrics.get(i).getFunctions(), classmetrics.get(j).getFunctions());
					double nclocSimilarity = calculateSimilarityBetweenMetrics(classmetrics.get(i).getNcloc(), classmetrics.get(j).getNcloc());
					double statementsSimilarity = calculateSimilarityBetweenMetrics(classmetrics.get(i).getStatements(), classmetrics.get(j).getStatements());
					double technicalDebtSimilarity = calculateSimilarityBetweenMetrics(classmetrics.get(i).getTD(), classmetrics.get(j).getTD());
					double similarityValue = numOfClassSimilarity + complexitySimilarity + numfOfFunctionsSimilarity + nclocSimilarity
							+ statementsSimilarity + technicalDebtSimilarity;
					/*System.out.println("check class: " + classmetrics.get(j).getClassName());
					System.out.println("metrics check: " + numOfClassSimilarity + " " + complexitySimilarity + " " + numfOfFunctionsSimilarity + " "
							+ nclocSimilarity+" " + statementsSimilarity + " " + technicalDebtSimilarity);*/
					similarityValue = similarityValue / 6;
					similarClass.setSimilarity(similarityValue);
				}
				
			}
			similarClassesList.add(similarClass);

		}
		
		findTheFiveMostSimilar(similarClassesList);
		return similarClassesList;
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
	
	public void findTheFiveMostSimilar(ArrayList<FindSimilarClasses> similarClasses)
	{
		for (int i = 0; i < similarClasses.size(); i++)
		{
			/*String currentClass = similarClasses.get(i).getClass().getName();
			double max  = 0;
			int index = 0;
			for (int j = 0; j < this.otherClasses.size(); j++)
			{
				if (max < this.similarity.get(j))
				{
					max = this.similarity.get(j);
					index = j;
				}					
			}
			
			
			
		}*/
			
			
					
			/*FindSimilarClasses currentClass = similarClasses.get(i);
			currentClass.sort();
			System.out.println("For class: " + currentClass.getName().getClassName() + " the most similar are: ");
			for (int j = 0; j < NUM_SIMILAR_ARTIFACTS; j++) 
			{
				currentClass.similarClassesList.add(currentClass.otherClasses.get(j));
				currentClass.similarClassesSimilarity.add(currentClass.similarity.get(j));
				System.out.println(currentClass.otherClasses.get(j).getClassName() + " with similarity: " + currentClass.similarity.get(j));
				
			}
			similarClasses.set(i, currentClass);*/
			
			
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
			for (int j = 0; j < similarClasses.get(i).getOtherClasses().size(); j++)
			{
				if (similarClasses.get(i).getSimilarity().get(j) > value1)
				{
					value1 = similarClasses.get(i).getSimilarity().get(j);
					index1 = j;
				}
				else if (similarClasses.get(i).getSimilarity().get(j) > value2)
				{
					value2 = similarClasses.get(i).getSimilarity().get(j);
					index2 = j;
				}
				else if (similarClasses.get(i).getSimilarity().get(j) > value3)
				{
					value3 = similarClasses.get(i).getSimilarity().get(j);
					index3 = j;
				}
				else if (similarClasses.get(i).getSimilarity().get(j) > value4)
				{
					value4 = similarClasses.get(i).getSimilarity().get(j);
					index4 = j;
				}
				else if (similarClasses.get(i).getSimilarity().get(j) > value5)
				{
					value5 = similarClasses.get(i).getSimilarity().get(j);
					index5 = j;
				}
			}
					
			System.out.println("For class: " + similarClasses.get(i).getName().getClassName() +
					" the 5 most similar classes are: " + similarClasses.get(i).getOtherClasses().get(index1).getClassName()
					+ "with similarity : " + value1 + "\n"
					+ similarClasses.get(i).getOtherClasses().get(index2).getClassName()
					+ "with similarity : " + value2 + "\n" + 
					similarClasses.get(i).getOtherClasses().get(index3).getClassName()
					+ "with similarity : " + value3 + "\n" +
					similarClasses.get(i).getOtherClasses().get(index4).getClassName()
					+ "with similarity : " + value4 + "\n" + 
					similarClasses.get(i).getOtherClasses().get(index5).getClassName()
					+ "with similarity : " + value5 + "\n");
			
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(index1));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(index2));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(index3));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(index4));
			similarClasses.get(i).setSimilarClass(similarClasses.get(i).getOtherClasses().get(index5));
			
			similarClasses.get(i).setSimilarClassSimilarity(value1);
		    similarClasses.get(i).setSimilarClassSimilarity(value2);
		    similarClasses.get(i).setSimilarClassSimilarity(value3);
		    similarClasses.get(i).setSimilarClassSimilarity(value4);
		    similarClasses.get(i).setSimilarClassSimilarity(value5);
		}
	}
	
	public FindSimilarClasses sort()
	{
		for(int i = 0 ; i < this.otherClasses.size()-1; i ++)
		{
			for(int  j =1; j < this.otherClasses.size(); j++)
			{
				if (this.similarity.get(i) < this.similarity.get(j))
				{
					ClassMetrics temp= this.otherClasses.get(j);
					this.otherClasses.set(j,this.otherClasses.get(i));
					this.otherClasses.set(i, temp);
					
					Double tempD= this.similarity.get(j);
					this.similarity.set(j,this.similarity.get(i));
					this.similarity.set(i, tempD);
				}
			}
		}
		return this;
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
}
