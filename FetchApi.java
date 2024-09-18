import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FetchApi {

	public static ArrayList<Object>getMatch(JSONArray arr){
		ArrayList<Object>al=new ArrayList<Object>();
		int maxscore=Integer.MIN_VALUE;
		String name="";
		try {
			for(int i=0;i<arr.size();i++) {
	        	 JSONObject newobj = (JSONObject) arr.get(i);
	        	 int t1s=Integer.parseInt(newobj.get("t1s").toString().substring(0,newobj.get("t1s").toString().lastIndexOf('/')));
	        	 int t2s=Integer.parseInt(newobj.get("t2s").toString().substring(0,newobj.get("t2s").toString().lastIndexOf('/')));
	        	 if (t1s > maxscore) {
	        		 maxscore = t1s;
	                 name = newobj.get("t1").toString();
	             }
	             if (t2s > maxscore) {
	                 maxscore = t2s;
	                 name = newobj.get("t2").toString();
	             }
	         }
			  al.add(maxscore);
			  al.add(name);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return al;
	}
	
	public static int countMatch(JSONArray arr) {
		int count=0;
		try {
			for(int i=0;i<arr.size();i++) {
		       	 JSONObject newobj = (JSONObject) arr.get(i);
		       	 
		       	 int t1=Integer.parseInt(newobj.get("t1s").toString().substring(0,newobj.get("t1s").toString().lastIndexOf('/')));
		       	 int t2=Integer.parseInt(newobj.get("t2s").toString().substring(0,newobj.get("t2s").toString().lastIndexOf('/')));
		       	 
		       	 if (t1+t2>300) {
		                count++;
		            }
				}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		return count;
	}
	public static void main(String[] args) {
		try {
			 URL url = new URL("https://api.cuvora.com/car/partner/cricket-data");
			 HttpURLConnection con=(HttpURLConnection)url.openConnection();
			 con.setRequestMethod("GET");
			 con.setRequestProperty("apiKey","test-creds@2320");
			 con.connect();
			 int res=con.getResponseCode();
			 if(res!=200) {
				 throw new RuntimeException("HttpResponseCode: "+res);
			 }
			 else {
				 String output="";
				 StringBuilder sb=new StringBuilder();
				 BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				 while((output=br.readLine())!=null) {
					 sb.append(output);
				 }
				 con.disconnect();
				 JSONParser parse = new JSONParser();
				 JSONObject obj = (JSONObject) parse.parse(sb.toString());
				 JSONArray data = (JSONArray) obj.get("data");
				 
				 ArrayList<Object>ans=getMatch(data);
				 System.out.println("Highest Score : " + ans.get(0).toString() + " and Team Name is : " + ans.get(1).toString());
				 int totalmatch=countMatch(data);
				 
				 System.out.println("NumberOfMatcheswithtotal300PlusScore: "+totalmatch);
			 }
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
