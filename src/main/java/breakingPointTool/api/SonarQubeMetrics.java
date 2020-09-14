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
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SonarQubeMetrics {
	private ArrayList<Double> classes;
	private ArrayList<Double> complexity;
	private ArrayList<Double> functions;
	private ArrayList<Double> ncloc;
	private ArrayList<Double> statements;
	private ArrayList<Double> comment_lines_density;
	private ArrayList<Integer> technical_debt;
	private ArrayList<String> artifactNames;

	private ArrayList<Double> codeSmells;
	private ArrayList<Double> vulnerabilities;
	private ArrayList<Double> bugs;
	private ArrayList<Double> duplicated_lines_density;

	private String server;

	public SonarQubeMetrics(String server) {
		this.server = server;
	}

	public void getMetricCommentsDensityFromApi(ArrayList<String> classesIDs) throws JSONException {
		comment_lines_density = new ArrayList<>();

		for (String clIDs : classesIDs) {
			try {

				CloseableHttpClient httpClient = HttpClients.createDefault();

				HttpGet getRequest = new HttpGet(this.server + "/api/measures/component?"
						+ "metricKeys=comment_lines_density" + "&component=" + clIDs);
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
					JSONArray array = obj.getJSONObject("component").getJSONArray("measures");

					for (int i = 0; i < array.length(); i++) 
					{
						//String metric = array.getJSONObject(i).getString("metric");
						String value = array.getJSONObject(i).getString("value");
						//System.out.println(metric + ": " + value);
						this.comment_lines_density.add(Double.parseDouble(value));
					}
				}

				httpClient.close();

			} catch (ClientProtocolException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		System.out.println("Comments Density from sonar api retrieved with success!");

	}

	public void getMetricsFromApiSonarClassLevel(ArrayList<String> classesIDs, String language)
			throws JSONException {
		classes = new ArrayList<>();
		complexity = new ArrayList<>();
		functions = new ArrayList<>();
		ncloc = new ArrayList<>();
		statements = new ArrayList<>();
		technical_debt = new ArrayList<>();
		artifactNames = new ArrayList<>();

		bugs = new ArrayList<>();
		codeSmells = new ArrayList<>();
		vulnerabilities = new ArrayList<>();
		duplicated_lines_density = new ArrayList<>();

		for (String clIDs : classesIDs) {
			if (clIDs.contains("xml")) {
				continue;

			}
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet getRequest = new HttpGet(this.server + "/api/measures/component?"
						+ "metricKeys=code_smells,bugs,vulnerabilities,duplicated_lines_density,classes,complexity,functions,ncloc,statements"
						+ "&component=" + clIDs);
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
					JSONArray array = obj.getJSONObject("component").getJSONArray("measures");
					String name = obj.getJSONObject("component").getString("path");

					String[] names = name.split("\\.java");
					setArtifactnames(names[0]);

					for (int i = 0; i < array.length(); i++) {
						String metric = array.getJSONObject(i).getString("metric");
						String value = array.getJSONObject(i).getString("value");
						//System.out.println(metric + ": " + value);
						findIssue(metric, Double.parseDouble(value));
					}
					
					checkSizes(array);


				} // end of while

				httpClient.close();

			} catch (ClientProtocolException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		System.out.println("Metrics from sonar api retrieved with success!");
	}

	public void getMetricsFromSonarPackageLevel(ArrayList<String> packagesIDs, String language)
			throws JSONException, SAXException, ParserConfigurationException {
		classes = new ArrayList<>();
		complexity = new ArrayList<>();
		functions = new ArrayList<>();
		ncloc = new ArrayList<>();
		statements = new ArrayList<>();
		technical_debt = new ArrayList<>();
		artifactNames = new ArrayList<>();

		bugs = new ArrayList<>();
		codeSmells = new ArrayList<>();
		vulnerabilities = new ArrayList<>();
		duplicated_lines_density = new ArrayList<>();

		for (String clIDs : packagesIDs) {
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();

				HttpGet getRequest = new HttpGet(this.server + "/api/measures/component?"
						+ "metricKeys=code_smells,bugs,vulnerabilities,duplicated_lines_density,classes,complexity,functions,ncloc,statements"
						+ "&component=" + clIDs);
				getRequest.addHeader("accept", "application/json");

				HttpResponse response = httpClient.execute(getRequest);

				if (response.getStatusLine().getStatusCode() != 200) {
					throw new RuntimeException(
							"Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

				String output;

				while ((output = br.readLine()) != null) {
					JSONObject obj = new JSONObject(output);
					JSONArray array = obj.getJSONObject("component").getJSONArray("measures");

					//System.out.println(array);
					String name = obj.getJSONObject("component").getString("path");

					setArtifactnames(name);

					for (int i = 0; i < array.length(); i++) {
						String metric = array.getJSONObject(i).getString("metric");
						String value = array.getJSONObject(i).getString("value");
						findIssue(metric, Double.parseDouble(value));
					}

					checkSizes(array);
				}

				httpClient.close();

			} catch (ClientProtocolException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		System.out.println("Metrics from sonar api retrieved with success!");
	}

	public void getTDFromApiSonar(ArrayList<String> classesIDs) {
		for (String clIDs : classesIDs) {
			if (clIDs.contains("xml")) {
				continue;

			}
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();

				HttpGet getRequest = new HttpGet(this.server + "/api/issues/search?" + "facetMode=effort"
						+ "&facets=types" + "&componentKeys=" + clIDs);
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

					String[] technicalDebt = output.split("effortTotal");
					String[] temp = technicalDebt[1].split(",");
					temp[0] = temp[0].replaceAll(":", "");
					temp[0] = temp[0].replaceAll("\"", "");
					this.technical_debt.add(Integer.parseInt(temp[0]));
					// System.out.println("TD: " + Integer.parseInt(temp[0]));
					//System.out.println(output);
				}

				httpClient.close();

			} catch (ClientProtocolException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		System.out.println("Technical Debt from sonar api retrieved with success!");
	}

	public void setBugs(Double b) {
		this.bugs.add(b);
	}

	public void setCodeSmells(Double b) {
		this.codeSmells.add(b);
	}

	public void setVulnerabilities(Double b) {
		this.vulnerabilities.add(b);
	}

	public void setNumofClasses(Double b) {
		this.classes.add(b);
	}

	public void setArtifactnames(String n) {
		this.artifactNames.add(n);
	}

	public void setComplexity(Double cs) {
		this.complexity.add(cs);
	}

	public void setncloc(Double v) {
		this.ncloc.add(v);
	}

	public void setStatements(Double d) {
		this.statements.add(d);
	}

	public void setFunctions(Double c) {
		this.functions.add(c);
	}

	public void setTechnicalDebt(Integer td) {
		this.technical_debt.add(td);
	}

	public void setCommentsDensity(Double cd) {
		this.comment_lines_density.add(cd);
	}

	public void setDuplicationsDensity(Double cd) {
		this.duplicated_lines_density.add(cd);
	}

	public ArrayList<Double> getCommentsDensity() {
		return this.comment_lines_density;
	}

	public ArrayList<String> getArtifactNames() {
		return this.artifactNames;
	}

	public ArrayList<Double> getNumOfClasses() {
		return this.classes;
	}

	public ArrayList<Double> getComplexity() {
		return this.complexity;
	}

	public ArrayList<Double> getStatements() {
		return this.statements;
	}

	public ArrayList<Double> getFunctions() {
		return this.functions;
	}

	public ArrayList<Double> getNcloc() {
		return this.ncloc;
	}

	public ArrayList<Integer> getTechnicalDebt() {
		return this.technical_debt;
	}

	public ArrayList<Double> getBugs() {
		return this.bugs;
	}

	public ArrayList<Double> getCodeSmells() {
		return this.codeSmells;
	}

	public ArrayList<Double> getVulnerabilities() {
		return this.vulnerabilities;
	}

	public ArrayList<Double> getDuplicationsDensity() {
		return this.duplicated_lines_density;
	}

	public void findIssue(String metricName, Double value) {
		/*
		 * SonarQube API does not return metrics to a specific order so we have to check
		 * every time what metric we got and to add it in the right array in the right
		 * order.
		 */
		if (metricName.equals("classes"))
			setNumofClasses(value);
		else if (metricName.equals("complexity"))
			setComplexity(value);
		else if (metricName.equals("statements"))
			setStatements(value);
		else if (metricName.equals("functions"))
			setFunctions(value);
		else if (metricName.equals("ncloc"))
			setncloc(value);
		else if (metricName.equals("code_smells"))
			setCodeSmells(value);
		else if (metricName.equals("bugs"))
			setBugs(value);
		else if (metricName.equals("vulnerabilities"))
			setVulnerabilities(value);
		else if (metricName.equals("duplicated_lines_density"))
			setDuplicationsDensity(value);
	}
	
	public void checkSizes(JSONArray array)
	{
		if (array.length() < 9)
		{
			if (this.classes.size() < this.artifactNames.size())
				setNumofClasses(0.0);
			if (this.complexity.size() < this.artifactNames.size())
				setComplexity(0.0);
			if (this.statements.size() < this.artifactNames.size())
				setStatements(0.0);
			if (this.functions.size() < this.artifactNames.size())
				setFunctions(0.0);
			if(this.ncloc.size() < this.artifactNames.size())
				setncloc(0.0);
			if(this.codeSmells.size() < this.artifactNames.size())
				setCodeSmells(0.0);
			if(this.bugs.size() < this.artifactNames.size())
				setBugs(0.0);
			if (this.vulnerabilities.size() < this.artifactNames.size())
				setVulnerabilities(0.0);
			if(this.duplicated_lines_density.size() < this.artifactNames.size())
				setDuplicationsDensity(0.0);
		}
	}
}
