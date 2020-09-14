package main.java.breakingPointTool.GitClone;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import main.java.breakingPointTool.main.BreakingPointTool;

// Clone Project from Git
public class GitCloneProject 
{
	private String cloneProjectPath;
	
	public GitCloneProject()
	{
		this.cloneProjectPath = null;
	}
	
	public String getProjectPath()
	{
		return this.cloneProjectPath;
	}
	
	// Clone Git Projects with and without credentials
	public void cloneCommits(String username, String password, ArrayList<String> sha, String git, String projectName, int version) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException
	{
		String javaRunningDirectory = System.getProperty("user.dir");
		//this.cloneProjectPath = javaRunningDirectory + "/Projects/" + projectName;
		
		this.cloneProjectPath = BreakingPointTool.BASE_DIR + "/Projects/" + projectName;
		
		int ver = 0;
	
		// Public repository, no need for authorization
		if (password.length() < 2)
		{
			for (int i = 0; i < sha.size(); i++)
			{
				ver = i;
				if (sha.size() == 1)
					ver = version - 1;
				//Clone repository
				try (Git result = Git.cloneRepository()
						.setURI(git)
						.setDirectory(new File(this.cloneProjectPath + "/" + projectName + ver))
						.call()) {
					System.out.println("Having repository: " + result.getRepository().getDirectory());
				}
			}
		}
		//Private repository, need of authorization
		else
		{
			for (int i = 0; i < sha.size(); i++)
			{
				ver = i;
				if (sha.size() == 1)
					ver = version - 1;
				// Clone repository in folder for each version, with credentials
				try (Git result = Git.cloneRepository()
						.setURI(git)
						.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
						.setDirectory(new File(this.cloneProjectPath + "/" + projectName + ver))
						.call()) {

					result.checkout().setName(sha.get(i)).call();

					System.out.println("Cloned repository: " + result.getRepository().getDirectory());
				}
			}
		}
	}
	
}
