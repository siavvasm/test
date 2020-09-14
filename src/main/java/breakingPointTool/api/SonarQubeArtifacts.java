package main.java.breakingPointTool.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SonarQubeArtifacts 
{
	private ArrayList<String> classesIDs;
	private ArrayList<String> packagesIDs;
	private String server;

	public SonarQubeArtifacts(String server) 
	{
		this.classesIDs = new ArrayList<String>();
		this.packagesIDs = new ArrayList<String>();
		this.server = server;
	}

	public SonarQubeArtifacts()
	{
		
	}

	public void getArtifactsName(String projectName, String artifactType) throws JSONException
	{

		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet getRequest = new HttpGet(this.server + "/api/components/tree?component=" + projectName +
					"&qualifiers=" + artifactType);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException(
						"Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			while ((output = br.readLine()) != null) 
			{
				JSONObject obj = new JSONObject(output);
				JSONArray array = obj.getJSONArray("components");

				for (int i = 0; i < array.length(); i++) 
				{
					//String metric = array.getJSONObject(i).getString("metric");
					String value = array.getJSONObject(i).getString("key");
					if (value.equals("xml"))
						continue;
					
					if (value.contains("test/"))
						continue;
					
					if (artifactType.equals(("FIL")))
 					{
						this.classesIDs.add(value);
					}
					else
					{
						this.packagesIDs.add(value);
					}

				}
			}

			httpClient.close();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}


		System.out.println("Artifact " + artifactType + " from sonar api retrieved with success!");
	}

	public ArrayList<String> getClassesId()
	{
		return this.classesIDs;
	}

	public ArrayList<String> getPackagesId()
	{
		return this.packagesIDs;
	}

	public void clearData()
	{
		this.classesIDs.clear();
		this.packagesIDs.clear();
	}

}
