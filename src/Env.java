package src;

import java.io.*;
import java.util.HashMap;

public class Env {
    private File envFile;
    private HashMap<String, String> authTokens = new HashMap<String, String>();

    Env() throws IOException {
        this.envFile = new File(".env");
        BufferedReader br = new BufferedReader(new FileReader(this.envFile));

        String str;
        while ((str = br.readLine()) != null) {
            String[] lineSplit = str.split("=");
            this.authTokens.put(lineSplit[0], lineSplit[1]);
        }

        br.close();
    }

    public File getEnvFile() {
        return this.envFile;
    }

    public HashMap<String, String> getAuthTokens() {
        return this.authTokens;
    }
}
