package edu.sjsu.android.stockmarketviewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

/**
 * Class in charge of sending GET request to Tiingo REST API and parsing the response as JSON
 */
public class JSONParser {
    private static final String API_KEY = "ff16d6d11bf37c6e62c61e1f2f6ee34d7e3bb211";

    public JSONObject getCompanyInfo(String symbol){
        String urlString = "https://api.tiingo.com/tiingo/daily/"+symbol+"?token="+API_KEY;
        String result = "";
        try {
            URL url = new URL(urlString);
            InputStream stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            String inputLine;
            while((inputLine = in.readLine())!=null){
                result = result +inputLine;
            }
            in.close();
            JSONObject info = new JSONObject(result);
            return info;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public JSONArray getCurrentStock(String symbol){
        String urlString = "https://api.tiingo.com/iex/?tickers="+symbol+"&token="+API_KEY;
        String result = "";
        try {
            URL url = new URL(urlString);
            InputStream stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            String inputLine;
            while((inputLine = in.readLine())!=null){
                result = result +inputLine;
            }
            in.close();
            JSONArray info = new JSONArray(result);
            return info;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public JSONArray querySymbol(String input) {
        String urlString = null;
        try {
            urlString = "https://api.tiingo.com/tiingo/utilities/search?query="+ URLEncoder.encode(input, "UTF-8") +"&token="+API_KEY;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String result = "";
        try {
            URL url = new URL(urlString);
            InputStream stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            String inputLine;
            while((inputLine = in.readLine())!=null){
                result = result +inputLine;
            }
            in.close();
            JSONArray info = new JSONArray(result);
            return info;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public JSONArray getHistoricalData(String symbol){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        String startDate = ""+ calendar.get(Calendar.YEAR)+"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+calendar.get(Calendar.DAY_OF_MONTH);
        String urlString = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?startDate="+startDate+"&resampleFreq=daily&token="+API_KEY;
        String result = "";
        try {
            URL url = new URL(urlString);
            InputStream stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            String inputLine;
            while((inputLine = in.readLine())!=null){
                result = result +inputLine;
            }
            in.close();
            JSONArray info = new JSONArray(result);
            return info;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Double getMarketCap(String symbol){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        String startDate = ""+ calendar.get(Calendar.YEAR)+"-"+ (calendar.get(Calendar.MONTH)+1) +"-"+calendar.get(Calendar.DAY_OF_MONTH);
        String urlString = "https://api.tiingo.com/tiingo/fundamentals/"+symbol+"/daily?startDate="+startDate+"&token="+API_KEY;
        String result = "";
        try {
            URL url = new URL(urlString);
            InputStream stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(reader);
            String inputLine;
            while((inputLine = in.readLine())!=null){
                result = result +inputLine;
            }
            in.close();
            JSONArray info = new JSONArray(result);
            return info.getJSONObject(info.length()-1).getDouble("marketCap");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
