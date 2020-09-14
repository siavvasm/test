package tests.java.GitClone;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.junit.jupiter.api.Test;

import main.java.breakingPointTool.GitClone.GitCloneProject;

public class GitCloneProjectTest 
{
	@Test
	public void CreateRepository() throws IOException, InvalidRemoteException, TransportException, GitAPIException
	{
		// Create a folder that will act as the remote repository 
		String dir = System.getProperty("user.dir");
		File remoteDir = new File(dir + "/remote", "");
		remoteDir.delete(); 
		remoteDir.mkdirs();

		// Create a bare repository 
		FileKey fileKey = FileKey.exact(remoteDir, FS.DETECTED); 
		Repository remoteRepo = fileKey.open(false); 
		remoteRepo.create(true);
		
		// Clone the bare repository 
		File cloneDir = new File(dir + "/clone", "");
		cloneDir.delete(); 
		cloneDir.mkdirs(); 
		
		Git git = Git.cloneRepository().setURI(remoteRepo.getDirectory().getAbsolutePath()).setDirectory(cloneDir).call();
		System.out.println(remoteRepo.getDirectory().getAbsolutePath());
		
		// Let's do the first commit 
		// Create a new file 
		File newFile = new File(cloneDir, "myNewFile"); 
		newFile.createNewFile(); 
		FileUtils.writeStringToFile(newFile, "Test content file", "ISO-8859-1");
		// Commit the new file 
		git.add().addFilepattern(newFile.getName()).call(); 
		git.commit().setMessage("First commit").setAuthor("test", "test@example.com ").call();
		
		// Push the commit on the bare repository 
		RefSpec refSpec = new RefSpec("master"); 
		git.push().setRemote("origin").setRefSpecs(refSpec).call();
		
		
		
		ArrayList<String> shas = new ArrayList<String>();

		GitCloneProject gitTest = new GitCloneProject();
		gitTest.cloneCommits("", "", shas, remoteRepo.getDirectory().getAbsolutePath(), "remote", 1);
		

	}

}
