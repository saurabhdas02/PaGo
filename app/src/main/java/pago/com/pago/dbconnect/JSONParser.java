package pago.com.pago.dbconnect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method,
			List<NameValuePair> params) {

		// Making HTTP request
		try {
			
			// check for request method
			if(method == "POST"){
				// request method is POST
				// defaultHttpClient
                Log.d("my", "method equals POST is working");

                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.d("my", "HTTP client is working");

                HttpPost httpPost = new HttpPost(url);
                Log.d("my", "HTTP post is working");

                httpPost.setEntity(new UrlEncodedFormEntity(params));
                Log.d("my", "url encoded");


                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.d("my", "HTTP response is working");

                HttpEntity httpEntity = httpResponse.getEntity();
                Log.d("my", "HTTP entity is working");

                is = httpEntity.getContent();
                Log.d("my", "getcontent is working");


            }else if(method == "GET"){
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "iso-8859-1");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}			
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
           // Log.d("my", "buffer reader created");

            StringBuilder sb = new StringBuilder();
           // Log.d("my", "string builder object crated");

            String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
           //     Log.d("my", "line appended");

            }
			is.close();
          //  Log.d("my", "inputstram closed");

            json = sb.toString();
           // Log.d("my", "string buffer to string conversion");

        } catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

        Log.d("JSON Parser", json);
        // try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}

}
