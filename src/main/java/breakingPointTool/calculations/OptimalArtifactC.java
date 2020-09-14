package main.java.breakingPointTool.calculations;

import java.sql.SQLException;
import java.util.ArrayList;

public class OptimalArtifactC 
{
	private double optimalComplexity;
   // private double optimalNumOfFunctions;
    private double optimalLinesOfCode;
    //private double optimalCommentsDensity;
    private double optimalCoupling;
    private double optimalCohesion;
    
    public void calculateOptimalClass(ArrayList<FindSimilarArtifactsC> similarClasses, int version) throws SQLException
    {
    	ArrayList<OptimalArtifactC > optimalClassesList = new ArrayList<OptimalArtifactC >();
    	
    	for (int i = 0; i < similarClasses.size(); i++)
    	{
    		OptimalArtifactC  optimClass = new OptimalArtifactC ();
    		// Values from considered class
    		double complexity = similarClasses.get(i).getName().getComplexity();
    		double linesOfCode = similarClasses.get(i).getName().getNcloc();
    		double coupling = similarClasses.get(i).getName().getCoupling();
    		double cohesion = similarClasses.get(i).getName().getCohesion();
    		//double functions = similarClasses.get(i).getName().getNumOfFunctions();
    		//double commentsDensity = similarClasses.get(i).getName().getCommentsDensity();

    		//System.out.println("Arxikopoihsh optimal: " + complexity + " " + functions + " " + linesOfCode + " " + commentsDensity);
    		
    		System.out.println("Find optimal for class: " + similarClasses.get(i).getName().getClassName());
    		
    		// Check only the 5 or less most similar
        	int sizeOfArtifacts = similarClasses.get(i).getSimilarClasses().size();
        	
        	if (sizeOfArtifacts > 5)
        		sizeOfArtifacts = 4;
        		
    		for (int j = 0; j < sizeOfArtifacts; j++)
    		{
    			System.out.println("Class name: " + similarClasses.get(i).getSimilarClasses().get(j).getClassName() );
    			/*System.out.println("Ypoloipes times: " + similarClasses.get(i).getSimilarClasses().get(j).getComplexity() + " " + 
    					similarClasses.get(i).getSimilarClasses().get(j).getNumOfFunctions() + " "+ similarClasses.get(i).getSimilarClasses().get(j).getNcloc()
    					+ " " + similarClasses.get(i).getSimilarClasses().get(j).getCommentsDensity());
    			*/
    			
    			/*
    			if (similarClasses.get(i).getSimilarClasses().get(j).getComplexity() + similarClasses.get(i).getSimilarClasses().get(j).getNumOfFunctions() 
    					+ similarClasses.get(i).getSimilarClasses().get(j).getNcloc() + similarClasses.get(i).getSimilarClasses().get(j).getCommentsDensity() == 0)
    			{
    				continue;
    			}
    			
    			
    			if (similarClasses.get(i).getSimilarClasses().get(j).getCommentsDensity() == 100.0)
    			{
    				continue;
    			}*/
    			
    			if (similarClasses.get(i).getSimilarClasses().get(j).getComplexity() < complexity)
    				complexity = similarClasses.get(i).getSimilarClasses().get(j).getComplexity();
    			
    			if (similarClasses.get(i).getSimilarClasses().get(j).getNcloc() < linesOfCode)
    				linesOfCode = similarClasses.get(i).getSimilarClasses().get(j).getNcloc();
    			
    			if (similarClasses.get(i).getSimilarClasses().get(j).getCohesion() < cohesion)
    				cohesion = similarClasses.get(i).getSimilarClasses().get(j).getCohesion();
    			
    			if (similarClasses.get(i).getSimilarClasses().get(j).getCoupling() < coupling)
    				coupling = similarClasses.get(i).getSimilarClasses().get(j).getCoupling();
    			/*
    			if (similarClasses.get(i).getSimilarClasses().get(j).getNumOfFunctions() < functions)
    				functions = similarClasses.get(i).getSimilarClasses().get(j).getNumOfFunctions();
    			
    			if (similarClasses.get(i).getSimilarClasses().get(j).getCommentsDensity() > commentsDensity)
   			     commentsDensity = similarClasses.get(i).getSimilarClasses().get(j).getCommentsDensity();
   			     */
    			
    			/*System.out.println("Clasas name:" + similarClasses.get(i).getSimilarClasses().get(j).getClassName());
    			System.out.println("try to find optimal: " + similarClasses.get(i).getSimilarClasses().get(j).getDit() + " " + similarClasses.get(i).getSimilarClasses().get(j).getNocc() + " " +
    					similarClasses.get(i).getSimilarClasses().get(j).getMpc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getRfc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getLcom() + 
        				" " + similarClasses.get(i).getSimilarClasses().get(j).getDac()  + " " + similarClasses.get(i).getSimilarClasses().get(j).getWmpc() + " " +
        				similarClasses.get(i).getSimilarClasses().get(j).getCc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize1() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize2());*/

    		}
    		
    		optimClass.setOptimalClassValues(complexity, linesOfCode, cohesion, coupling);
    		
			/*
			 * System.out.println("For class: " +
			 * similarClasses.get(i).getName().getClassName());
			 * System.out.println("With metrics: " +
			 * similarClasses.get(i).getName().getComplexity() + " " +
			 * similarClasses.get(i).getName().getNumOfFunctions() + " " +
			 * similarClasses.get(i).getName().getLinesOfCode() + " " +
			 * similarClasses.get(i).getName().getCommentsDensity());
			 * System.out.println("The optimal class is: " + complexity + " " + functions +
			 * " " + linesOfCode +" "+ commentsDensity);
			 */
    		ResultsC rs = new ResultsC();
    		optimalClassesList.add(optimClass);
    		rs.calculateInterest(similarClasses.get(i).getName(), optimClass, version);
    	}
	}
    
    public void calculateOptimalPackage(ArrayList<FindSimilarArtifactsC> similarPackages, int version) throws SQLException
    {
    	ArrayList<OptimalArtifactC> optimalClassesList = new ArrayList<OptimalArtifactC>();
    	for (int i = 0; i < similarPackages.size(); i++)
    	{
    		OptimalArtifactC optimClass = new OptimalArtifactC();    		
    		// Values from considered class
    		double complexity = similarPackages.get(i).getPackage().getComplexity();   		
    		double linesOfCode = similarPackages.get(i).getPackage().getNcloc();
    		double coupling = similarPackages.get(i).getPackage().getCoupling();
    		double cohesion = similarPackages.get(i).getPackage().getCohesion();
    		
    		/*
    		double functions = similarPackages.get(i).getPackage().getNumOfFunctions();
    		double commentsDensity = similarPackages.get(i).getPackage().getCommentsDensity();
    		*/
    		System.out.println("Find optimal package for: " + similarPackages.get(i).getPackage().getPackageName());
    		
    		// Check only the 5 or less most similar
        	int sizeOfArtifacts = similarPackages.get(i).getSimilarPackages().size();
        	
        	if (sizeOfArtifacts > 5)
        		sizeOfArtifacts = 4;
    		// Values from similar classes
    		for (int j = 0; j < sizeOfArtifacts; j++)
    		{  		
    			System.out.println("Package Name: " + similarPackages.get(i).getSimilarPackages().get(j).getPackageName());
    			
    			if (similarPackages.get(i).getSimilarPackages().get(j).getComplexity() < complexity)
    				complexity = similarPackages.get(i).getSimilarPackages().get(j).getComplexity();
    			
    			if (similarPackages.get(i).getSimilarPackages().get(j).getNcloc() < linesOfCode)
    				linesOfCode = similarPackages.get(i).getSimilarPackages().get(j).getNcloc();
    			
    			if (similarPackages.get(i).getSimilarPackages().get(j).getCohesion() < cohesion)
    				cohesion = similarPackages.get(i).getSimilarPackages().get(j).getCohesion();
    			
    			if (similarPackages.get(i).getSimilarPackages().get(j).getCoupling() < coupling)
    				coupling = similarPackages.get(i).getSimilarPackages().get(j).getCoupling();
    			
    			/*
    			if (similarPackages.get(i).getSimilarPackages().get(j).getCommentsDensity() > commentsDensity)
   			     	commentsDensity = similarPackages.get(i).getSimilarPackages().get(j).getCommentsDensity();
   			
    			if (similarPackages.get(i).getSimilarPackages().get(j).getNumOfFunctions() < functions)
    				functions = similarPackages.get(i).getSimilarPackages().get(j).getNumOfFunctions();
    				*/
    			/*System.out.println("Clasas name:" + similarClasses.get(i).getSimilarClasses().get(j).getClassName());
    			System.out.println("try to find optimal: " + similarClasses.get(i).getSimilarClasses().get(j).getDit() + " " + similarClasses.get(i).getSimilarClasses().get(j).getNocc() + " " +
    					similarClasses.get(i).getSimilarClasses().get(j).getMpc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getRfc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getLcom() + 
        				" " + similarClasses.get(i).getSimilarClasses().get(j).getDac()  + " " + similarClasses.get(i).getSimilarClasses().get(j).getWmpc() + " " +
        				similarClasses.get(i).getSimilarClasses().get(j).getCc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize1() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize2());*/

    		}
    		
    		optimClass.setOptimalClassValues(complexity, linesOfCode, cohesion, coupling);
    		
			/*
			 * System.out.println("For package: " +
			 * similarPackages.get(i).getPackage().getPackageName());
			 * System.out.println("With metrics: " +
			 * similarPackages.get(i).getPackage().getComplexity() + " " +
			 * similarPackages.get(i).getPackage().getNumOfFunctions() + " " +
			 * similarPackages.get(i).getPackage().getNcloc() + " " +
			 * similarPackages.get(i).getPackage().getCommentsDensity());
			 * System.out.println("The optimal class is: " + complexity + " " + functions +
			 * " " + linesOfCode +" "+ commentsDensity);
			 */

    		ResultsC rs = new ResultsC();
    		optimalClassesList.add(optimClass);
    		rs.calculateInterestPackage(similarPackages.get(i).getPackage(), optimClass, version);
    	}
	}

    public void setOptimalClassValues(double complexity, double linesOfCode,  double cohesion, double coupling)
    {
    	this.optimalComplexity = complexity;
        this.optimalLinesOfCode = linesOfCode;
        this.optimalCoupling = coupling;
        this.optimalCohesion = cohesion;
    }
    
    public double getComplexity()
    {
    	return this.optimalComplexity;
    }
    
    public double getLinesOfCode()
    {
    	return this.optimalLinesOfCode;
    }
    
    public double getCohesion()
    {
    	return this.optimalCohesion;
    }
    
    public double getCoupling()
    {
    	return this.optimalCoupling;
    }

}
