package src;

import src.alphavantage.*;
import javax.swing.SwingUtilities;

// import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StockSlayer();
            }
        });

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

        System.out.println(cfg.getApiKey());
    }
}
