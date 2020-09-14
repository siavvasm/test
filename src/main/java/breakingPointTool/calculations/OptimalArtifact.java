package main.java.breakingPointTool.calculations;

import java.sql.SQLException;
import java.util.ArrayList;

public class OptimalArtifact 
{	
    private double optimalDit;
    private double optimalNocc;
    private double optimalMpc;
    private double optimalRfc;
    private double optimalLcom;
    private double optimalDac;
    private double optimalNom;
    private double optimalWmpc;
    private double optimalSize1;
    private double optimalSize2;

    public void calculateOptimalClass(ArrayList<FindSimilarArtifacts> similarClasses, int version) throws SQLException
    {
    	ArrayList<OptimalArtifact> optimalClassesList = new ArrayList<OptimalArtifact>();
    	for (int i = 0; i < similarClasses.size(); i++)
    	{
    		OptimalArtifact optimClass = new OptimalArtifact();
    		// Values from considered class
    		double Dit = similarClasses.get(i).getName().getDit();
    		double Nocc = similarClasses.get(i).getName().getNocc();
    		double Mpc = similarClasses.get(i).getName().getMpc();
    		double Rfc = similarClasses.get(i).getName().getRfc();
    		double Lcom = similarClasses.get(i).getName().getLcom();
    		double Dac = similarClasses.get(i).getName().getDac();
    		double Nom = similarClasses.get(i).getName().getNom();
    		double Wmpc = similarClasses.get(i).getName().getWmpc();
    		double Size1 = similarClasses.get(i).getName().getSize1();
    		double Size2 = similarClasses.get(i).getName().getSize2();
    		
    		System.out.println("Find optimal for class: " + similarClasses.get(i).getName().getClassName());

    		// Check only the 5 or less most similar
        	int sizeOfArtifacts = similarClasses.get(i).getSimilarClasses().size();
        	
        	if (sizeOfArtifacts > 5)
        		sizeOfArtifacts = 4;
    		
    		// Values from similar classes
    		for (int j = 0; j < sizeOfArtifacts; j++)
    		{
    			System.out.println("Class name: " + similarClasses.get(i).getSimilarClasses().get(j).getClassName() );

    			if (similarClasses.get(i).getSimilarClasses().get(j).getDit() < Dit)
    				Dit = similarClasses.get(i).getSimilarClasses().get(j).getDit();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getNocc() < Nocc)
    				Nocc = similarClasses.get(i).getSimilarClasses().get(j).getNocc();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getMpc() < Mpc)
    				Mpc = similarClasses.get(i).getSimilarClasses().get(j).getMpc();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getRfc() < Rfc)
    			    Rfc = similarClasses.get(i).getSimilarClasses().get(j).getRfc();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getLcom() < Lcom)
    				Lcom = similarClasses.get(i).getSimilarClasses().get(j).getLcom();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getDac() < Dac)
    				Dac = similarClasses.get(i).getSimilarClasses().get(j).getDac();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getNom() < Nom)
    				Nom = similarClasses.get(i).getSimilarClasses().get(j).getNom();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getWmpc() < Wmpc)
    				Wmpc = similarClasses.get(i).getSimilarClasses().get(j).getWmpc();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getSize1() < Size1)
    				Size1 = similarClasses.get(i).getSimilarClasses().get(j).getSize1();
    			if (similarClasses.get(i).getSimilarClasses().get(j).getSize2() < Size2)
    				Size2 = similarClasses.get(i).getSimilarClasses().get(j).getSize2();
    			
    			/*System.out.println("Clasas name:" + similarClasses.get(i).getSimilarClasses().get(j).getClassName());
    			System.out.println("try to find optimal: " + similarClasses.get(i).getSimilarClasses().get(j).getDit() + " " + similarClasses.get(i).getSimilarClasses().get(j).getNocc() + " " +
    					similarClasses.get(i).getSimilarClasses().get(j).getMpc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getRfc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getLcom() + 
        				" " + similarClasses.get(i).getSimilarClasses().get(j).getDac()  + " " + similarClasses.get(i).getSimilarClasses().get(j).getWmpc() + " " +
        				similarClasses.get(i).getSimilarClasses().get(j).getCc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize1() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize2());*/

    		}
    		
    		optimClass.setOptimalClassValues(Dit, Nocc, Mpc, Rfc, Lcom, Dac, Nom, Wmpc, Size1, Size2);
    		
    		System.out.println("For class: " + similarClasses.get(i).getName().getClassName());
    		System.out.println("With metrics: " + similarClasses.get(i).getName().getDit() + " " + similarClasses.get(i).getName().getNocc() + " " +
    				similarClasses.get(i).getName().getMpc() + " " + similarClasses.get(i).getName().getRfc() + " " + similarClasses.get(i).getName().getLcom() + 
    				" " + similarClasses.get(i).getName().getDac() + " " + similarClasses.get(i).getName().getNom() + " " +
    				similarClasses.get(i).getName().getWmpc() + " " + similarClasses.get(i).getName().getSize1() + " " + similarClasses.get(i).getName().getSize2());
    		System.out.println("The optimal class is: " + Dit + " " + Nocc + " "  + Mpc +" "+ Rfc +" "+ Lcom +" "+ Dac +" "+ Nom +" "+ Wmpc +" "+ Size1 + " "+ Size2);
    		Results rs = new Results();
    		optimalClassesList.add(optimClass);
    		rs.calculateInterest(similarClasses.get(i).getName(), optimClass, version);
    	}
	}
    
    public void calculateOptimalPackage(ArrayList<FindSimilarArtifacts> similarPackages, int version) throws SQLException
    {
    	ArrayList<OptimalArtifact> optimalClassesList = new ArrayList<OptimalArtifact>();
    	for (int i = 0; i < similarPackages.size(); i++)
    	{
    		OptimalArtifact optimClass = new OptimalArtifact();
    		// Values from considered class
    		double Dit = similarPackages.get(i).getPackage().getDit();
    		double Nocc = similarPackages.get(i).getPackage().getNocc();
    		double Mpc = similarPackages.get(i).getPackage().getMpc();
    		double Rfc = similarPackages.get(i).getPackage().getRfc();
    		double Lcom = similarPackages.get(i).getPackage().getLcom();
    		double Dac = similarPackages.get(i).getPackage().getDac();
    		double Nom = similarPackages.get(i).getPackage().getNom();
    		double Wmpc = similarPackages.get(i).getPackage().getWmpc();
    		double Size1 = similarPackages.get(i).getPackage().getSize1();
    		double Size2 = similarPackages.get(i).getPackage().getSize2();
    		
    		System.out.println("Find optimal for class: " + similarPackages.get(i).getPackage().getPackageName());

    		// Check only the 5 or less most similar
        	int sizeOfArtifacts = similarPackages.get(i).getSimilarPackages().size();
        	
        	if (sizeOfArtifacts > 5)
        		sizeOfArtifacts = 4;
    		
    		// Values from similar classes
    		for (int j = 0; j < sizeOfArtifacts; j++)
    		{
    			System.out.println("Package Name: " + similarPackages.get(i).getSimilarPackages().get(j).getPackageName());

    			if (similarPackages.get(i).getSimilarPackages().get(j).getDit() > Dit)
    				Dit = similarPackages.get(i).getSimilarPackages().get(j).getDit();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getNocc() > Nocc)
    				Nocc = similarPackages.get(i).getSimilarPackages().get(j).getNocc();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getMpc() < Mpc)
    				Mpc = similarPackages.get(i).getSimilarPackages().get(j).getMpc();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getRfc() < Rfc)
    			    Rfc = similarPackages.get(i).getSimilarPackages().get(j).getRfc();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getLcom() < Lcom)
    				Lcom = similarPackages.get(i).getSimilarPackages().get(j).getLcom();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getDac() < Dac)
    				Dac = similarPackages.get(i).getSimilarPackages().get(j).getDac();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getNom() < Nom)
    				Nom = similarPackages.get(i).getSimilarPackages().get(j).getNom();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getWmpc() < Wmpc)
    				Wmpc = similarPackages.get(i).getSimilarPackages().get(j).getWmpc();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getSize1() < Size1)
    				Size1 = similarPackages.get(i).getSimilarPackages().get(j).getSize1();
    			if (similarPackages.get(i).getSimilarPackages().get(j).getSize2() < Size2)
    				Size2 = similarPackages.get(i).getSimilarPackages().get(j).getSize2();
    			
    			/*System.out.println("Clasas name:" + similarClasses.get(i).getSimilarClasses().get(j).getClassName());
    			System.out.println("try to find optimal: " + similarClasses.get(i).getSimilarClasses().get(j).getDit() + " " + similarClasses.get(i).getSimilarClasses().get(j).getNocc() + " " +
    					similarClasses.get(i).getSimilarClasses().get(j).getMpc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getRfc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getLcom() + 
        				" " + similarClasses.get(i).getSimilarClasses().get(j).getDac()  + " " + similarClasses.get(i).getSimilarClasses().get(j).getWmpc() + " " +
        				similarClasses.get(i).getSimilarClasses().get(j).getCc() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize1() + " " + similarClasses.get(i).getSimilarClasses().get(j).getSize2());*/

    		}
    		
    		optimClass.setOptimalClassValues(Dit, Nocc, Mpc, Rfc, Lcom, Dac, Nom, Wmpc, Size1, Size2);
    		
    		System.out.println("For package: " + similarPackages.get(i).getPackage().getPackageName());
    		System.out.println("With metrics: " + similarPackages.get(i).getPackage().getDit() + " " + similarPackages.get(i).getPackage().getNocc() + " " +
    				similarPackages.get(i).getPackage().getMpc() + " " + similarPackages.get(i).getPackage().getRfc() + " " + similarPackages.get(i).getPackage().getLcom() + 
    				" " + similarPackages.get(i).getPackage().getDac() + " " + similarPackages.get(i).getPackage().getNom() + " " +
    				similarPackages.get(i).getPackage().getWmpc() + " " + similarPackages.get(i).getPackage().getSize1() + " " + similarPackages.get(i).getPackage().getSize2());
    		System.out.println("The optimal class is: " + Dit + " " + Nocc + " "  + Mpc +" "+ Rfc +" "+ Lcom +" "+ Dac +" "+ Nom +" "+ Wmpc +" "+ Size1 + " "+ Size2);
    		Results rs = new Results();
    		optimalClassesList.add(optimClass);
    		rs.calculateInterestPackage(similarPackages.get(i).getPackage(), optimClass, version);
    	}
	}
    
    public void setOptimalClassValues(double Dit, double Nocc, double Mpc, double Rfc, double Lcom, double Dac, double Nom, double Wmpc,
    		double Size1, double Size2)
    {
    	this.optimalDit = Dit;
    	this.optimalNocc = Nocc;
    	this.optimalMpc = Mpc;
    	this.optimalRfc = Rfc;
    	this.optimalLcom = Lcom;
    	this.optimalDac = Dac;
    	this.optimalNom = Nom;
    	this.optimalWmpc = Wmpc;
    	this.optimalSize1 = Size1;
    	this.optimalSize2 = Size2;  	
    }
    
    public double getDit()
    {
    	return this.optimalDit;
    }
    
    public double getNocc()
    {
    	return this.optimalNocc;
    }
    
    public double getMpc()
    {
    	return this.optimalMpc;
    }
    
    public double getRfc()
    {
    	return this.optimalRfc;
    }
    
    public double getLcom()
    {
    	return this.optimalLcom;
    }

    public double getDac()
    {
    	return this.optimalDac;
    }
    
    public double getNom()
    {
    	return this.optimalNom;
    }
    
    public double getWmpc()
    {
    	return this.optimalWmpc;
    }
    
    public double getSize1()
    {
    	return this.optimalSize1;
    }
    
    public double getSize2()
    {
    	return this.optimalSize2;
    }
}
