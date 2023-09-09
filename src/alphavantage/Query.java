package src.alphavantage;

import java.io.*;
import java.net.*;
import java.util.*;

public class Query {

    int status;
    String content;

    public Query(Config cfg, String function, String symbol) throws MalformedURLException, IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("function", function);
        params.put("symbol", symbol);
        params.put("apikey", cfg.getApiKey());

        String paramURLheader = ParameterStringBuilder.getParamsString(params);

        URL url = new URL(cfg.BASE_URL + paramURLheader);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        this.status = connection.getResponseCode();
        if (status == 200) { // connection OK
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer contentBuffer = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                contentBuffer.append(inputLine);
            }
            in.close();
            this.content = contentBuffer.toString();
        }
        connection.disconnect();
    }

    public int getStatus() {
        return this.status;
    }

    public String getContent() {
        return this.content;
    }

    private class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
        }
    }
}
