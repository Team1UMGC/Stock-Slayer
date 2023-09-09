package src.alphavantage;

public class Config {
    public final String BASE_URL = "https://www.alphavantage.co/query?";
    private String apiKey;

    public Config(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return this.apiKey;
    }
}
