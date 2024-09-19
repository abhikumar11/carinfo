// Import classes for reading data from the API
import java.io.BufferedReader;  // Used to read the response line by line from the input stream
import java.io.InputStreamReader;  // Wraps an InputStreamReader for more efficient reading of the input stream

// Import classes for handling URLs and HTTP connections
import java.net.HttpURLConnection;  // Used to establish HTTP connections and send HTTP requests
import java.net.URL;  // Represents the API URL from which data will be fetched

// Import classes for working with lists
import java.util.ArrayList;  // Provides resizable array implementation of the List interface to store match data

// Import JSON parsing library
import org.json.simple.parser.JSONParser;  // Used to parse the JSON response from the API into a JSONObject
import org.json.simple.JSONArray;  // Represents a JSON array, useful for handling the list of match data
import org.json.simple.JSONObject;  // Represents a JSON object, used to access individual match details

public class FetchApi {

    // Method to find the highest score and the team that achieved it
    public static ArrayList<Object> getMatch(JSONArray arr) {
        ArrayList<Object> al = new ArrayList<Object>();  // List to store result
        int maxscore = Integer.MIN_VALUE;  // Variable to store the highest score, initialized to the smallest value
        String name = "";  // Variable to store the name of the team with the highest score
        
        try {
            // Loop through the array of match data
            for (int i = 0; i < arr.size(); i++) {
                JSONObject newobj = (JSONObject) arr.get(i);  // Fetch each match object
                
                // Extract and parse the score for team 1 (t1s), remove any trailing part after '/'
                int t1s = Integer.parseInt(newobj.get("t1s").toString().substring(0, newobj.get("t1s").toString().lastIndexOf('/')));
                
                // Extract and parse the score for team 2 (t2s), similarly remove trailing part after '/'
                int t2s = Integer.parseInt(newobj.get("t2s").toString().substring(0, newobj.get("t2s").toString().lastIndexOf('/')));
                
                // Update the highest score and corresponding team name if team 1's score is higher
                if (t1s > maxscore) {
                    maxscore = t1s;
                    name = newobj.get("t1").toString();  // Fetch team 1's name
                }
                
                // Update the highest score and corresponding team name if team 2's score is higher
                if (t2s > maxscore) {
                    maxscore = t2s;
                    name = newobj.get("t2").toString();  // Fetch team 2's name
                }
            }
            
            // Add the highest score and team name to the result list
            al.add(maxscore);
            al.add(name);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());  // Print error message if something goes wrong
        }
        return al;  // Return the result
    }

    // Method to count the number of matches where the total score of both teams exceeds 300
    public static int countMatch(JSONArray arr) {
        int count = 0;  // Counter to store the number of matches
        
        try {
            // Loop through the array of match data
            for (int i = 0; i < arr.size(); i++) {
                JSONObject newobj = (JSONObject) arr.get(i);  // Fetch each match object
                
                // Extract and parse the scores for team 1 and team 2, similarly as before
                int t1 = Integer.parseInt(newobj.get("t1s").toString().substring(0, newobj.get("t1s").toString().lastIndexOf('/')));
                int t2 = Integer.parseInt(newobj.get("t2s").toString().substring(0, newobj.get("t2s").toString().lastIndexOf('/')));
                
                // Check if the combined score of both teams exceeds 300
                if (t1 + t2 > 300) {
                    count++;  // Increment the counter if condition is met
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());  // Print error message if something goes wrong
        }
        
        return count;  // Return the count of matches
    }

    public static void main(String[] args) {
        try {
            // Define the API URL
            URL url = new URL("https://api.cuvora.com/car/partner/cricket-data");
            
            // Establish HTTP connection to the API
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");  // Set HTTP method to GET
            con.setRequestProperty("apiKey", "test-creds@2320");  // Set API key in request header
            
            con.connect();  // Open the connection
            int res = con.getResponseCode();  // Get response code from the server
            
            // Check if the response is successful (HTTP status 200)
            if (res != 200) {
                throw new RuntimeException("HttpResponseCode: " + res);  // Throw an error if response is not OK
            } else {
                String output = "";  // Variable to store line-wise response from the API
                StringBuilder sb = new StringBuilder();  // StringBuilder to accumulate the entire response
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));  // Read response
                
                // Read each line from the response and append it to StringBuilder
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                
                con.disconnect();  // Disconnect the connection after reading the response
                
                // Parse the response string into a JSONObject
                JSONParser parse = new JSONParser();
                JSONObject obj = (JSONObject) parse.parse(sb.toString());
                
                // Extract the 'data' array from the JSON object
                JSONArray data = (JSONArray) obj.get("data");
                
                // Get the highest score and team name using getMatch method
                ArrayList<Object> ans = getMatch(data);
                System.out.println("Highest Score : " + ans.get(0).toString() + " and Team Name is : " + ans.get(1).toString());
                
                // Get the count of matches with total score > 300 using countMatch method
                int totalmatch = countMatch(data);
                System.out.println("NumberOfMatcheswithtotal300PlusScore: " + totalmatch);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());  // Print error message in case of an exception
        }
    }
}
