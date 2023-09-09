package src;

import src.alphavantage.*;
// import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        // new StockSlayer();
        // }
        // });

        // Get and Config API Key
        Env env;
        Config cfg = new Config(null);
        try {
            env = new Env();
            cfg = new Config(env.getAuthTokens().get("ALPHA_VANTAGE_API_KEY"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Query Alpha Vantage API
        Query query = null;
        try {
            query = new Query(cfg, "TIME_SERIES_DAILY", "IBM");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(query.getStatus());
        System.out.println(query.getContent().substring(0, 1000));
    }
}
