package com.groupone;

import com.groupone.api.DatabaseAPI;
import com.groupone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.crazzyghost.alphavantage.*;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class StockSlayerApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    DatabaseAPI databaseAPI;

    public static void main(String[] args) {
        SpringApplication.run(StockSlayerApplication.class, args);
    }

    /**
     * Runs the database on launch
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        databaseAPI.initDatabase();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StockSlayerApplication.class);
    }
//
//    private static final String VALID_USERNAME = "test";
//    private static final String VALID_PASSWORD = "password";
//
//    @Controller
//    class StockController {
//        private List<Stock> heldStocks = new ArrayList<>();
//        private Map<String, Double> stockPrices = new ConcurrentHashMap<>();
//        private double userFunds = 10000.0; //
//        private final API_Stock apiStock;
//
//        public StockController(API_Stock apiStock) {
//            this.apiStock = apiStock;
//            initializeStockPrices();
//        }
//
//        private void initializeStockPrices() {
//            try {
//                List<String> symbols = Arrays.asList("GOOGL", "TSLA", "AMZN"); //
//                for (String symbol : symbols) { //
//                    TimeSeriesResponse response = apiStock.getIntraDayResponse(Interval.ONE_MIN, OutputSize.COMPACT, symbol);
//                    if (response != null && !response.getStockUnits().isEmpty()) {
//                        StockUnit latestData = response.getStockUnits().get(0);
//                        double latestPrice = latestData.getClose();
//                        stockPrices.put(symbol, latestPrice);
//                    }
//                }
//            } catch (configNotDefinedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @GetMapping("/main")
//        public String main(Model model) {
//            model.addAttribute("heldStocks", heldStocks);
//            model.addAttribute("stockPrices", stockPrices);
//            model.addAttribute("userFunds", userFunds);
//
//            return "main";
//        }
//
//        @PostMapping("/search")
//        public String searchStock(@RequestParam("stockName") String stockName, Model model) {
//            try {
//                TimeSeriesResponse stockInfoResponse = apiStock.getIntraDayResponse(Interval.ONE_MIN, OutputSize.COMPACT, stockName);
//                if (stockInfoResponse != null && !stockInfoResponse.getStockUnits().isEmpty()) {
//                    StockUnit stockUnit = stockInfoResponse.getStockUnits().get(0);
//                    double stockPrice = stockUnit.getClose();
//                    model.addAttribute("stockName", stockName);
//                    model.addAttribute("stockPrice", stockPrice);
//                } else {
//                    model.addAttribute("stockName", stockName);
//                    model.addAttribute("stockPrice", "N/A");
//                }
//            } catch (configNotDefinedException e) {
//                e.printStackTrace();
//            }
//
//            model.addAttribute("heldStocks", heldStocks);
//            model.addAttribute("stockPrices", stockPrices);
//            model.addAttribute("userFunds", userFunds);
//
//            return "main";
//        }
//
//        @PostMapping("/buy")
//        public String buyStock(@RequestParam("symbol") String symbol, @RequestParam("price") double price,
//                               @RequestParam("buyShares") int buyShares, Model model) {
//            if (stockPrices.containsKey(symbol)) {
//                double totalCost = price * buyShares;
//                if (userFunds >= totalCost && buyShares > 0) {
//                    userFunds -= totalCost;
//                    heldStocks.add(new Stock(symbol, price, buyShares));
//                }
//            }
//
//            model.addAttribute("heldStocks", heldStocks);
//            model.addAttribute("stockPrices", stockPrices);
//            model.addAttribute("userFunds", userFunds);
//            return "main";
//        }
//
//        @PostMapping("/sell/{index}")
//        public String sellStock(@PathVariable("index") int index, Model model) {
//            if (index >= 0 && index < heldStocks.size()) {
//                Stock stockToSell = heldStocks.get(index);
//                String symbol = stockToSell.getSymbol();
//                double stockPrice = stockPrices.getOrDefault(symbol, 0.0);
//                double revenue = stockPrice * stockToSell.getShares();
//                userFunds += revenue;
//                heldStocks.remove(index);
//            }
//
//            model.addAttribute("heldStocks", heldStocks);
//            model.addAttribute("stockPrices", stockPrices);
//            model.addAttribute("userFunds", userFunds);
//            return "main";
//        }
//
//        @PostMapping("/sortShares")
//        public String sortShares() {
//            Collections.sort(heldStocks, Comparator.comparingInt(Stock::getShares));
//            return "redirect:/main";
//        }
//
//        @PostMapping("/sortHighestValue")
//        public String sortHighestValue() {
//            Collections.sort(heldStocks, (s1, s2) -> { TODO, could be made into its own private function to reduce repeat code
//                double value1 = s1.getPrice() * s1.getShares();
//                double value2 = s2.getPrice() * s2.getShares();
//                return Double.compare(value2, value1);
//            });
//            return "redirect:/main";
//        }
//
//        @PostMapping("/sortLowestValue")
//        public String sortLowestValue() {
//            Collections.sort(heldStocks, (s1, s2) -> {
//                double value1 = s1.getPrice() * s1.getShares();
//                double value2 = s2.getPrice() * s2.getShares();
//                return Double.compare(value1, value2);
//            });
//            return "redirect:/main";
//        }
//    }
//
//    @Controller
//    class LoginController {
//
//        @GetMapping("/login")
//        public String loginForm() {
//            return "login";
//        }
//
//        @PostMapping("/login/authenticate")
//        public String authenticate(@RequestParam("username") String username,
//                                   @RequestParam("password") String password) {
//            if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
//                return "redirect:/main";
//            } else {
//                return "login";
//            }
//        }
//    }
//
//    class Stock {
//        private String symbol;
//        private double price;
//        private int shares;
//
//        public Stock(String symbol, double price, int shares) {
//            this.symbol = symbol;
//            this.price = price;
//            this.shares = shares;
//        }
//
//        public String getSymbol() {
//            return symbol;
//        }
//
//        public double getPrice() {
//            return price;
//        }
//
//        public int getShares() {
//            return shares;
//        }
//    }
//
//    @Controller
//    class API_Stock {
//        private final Config apiConfig;
//
//        public API_Stock(@Value("${ALPHA_VANTAGE_API_KEY}") String apiKey) {
//            this.apiConfig = Config.builder().key(apiKey).timeOut(10).build();
//        }
//
//        public void printIntraDay(Interval interval, OutputSize outputSize, String symbol) throws configNotDefinedException {
//            if (apiConfig == null) {
//                throw new configNotDefinedException("Configuration Data not Defined!");
//            }
//
//            AlphaVantage.api().init(apiConfig);
//            System.out.println(
//                    AlphaVantage.api()
//                            .timeSeries()
//                            .intraday()
//                            .forSymbol(symbol)
//                            .interval(interval)
//                            .outputSize(outputSize)
//                            .fetchSync()
//                            .toString()
//            );
//        }
//
//        public TimeSeriesResponse getIntraDayResponse(Interval interval, OutputSize outputSize, String symbol) throws configNotDefinedException {
//            if (apiConfig == null) {
//                throw new configNotDefinedException("Configuration Data not Defined!");
//            }
//            AlphaVantage.api().init(apiConfig);
//            TimeSeriesResponse response = AlphaVantage.api()
//                    .timeSeries()
//                    .intraday()
//                    .forSymbol(symbol)
//                    .interval(interval)
//                    .outputSize(outputSize)
//                    .fetchSync();
//
//            return response;
//        }
//
//        public List<StockUnit> getLastTenDays(String symbol) throws configNotDefinedException {
//            if (apiConfig == null) throw new configNotDefinedException("Configuration Data not Defined!");
//
//            AlphaVantage.api().init(apiConfig);
//            TimeSeriesResponse response = AlphaVantage.api()
//                    .timeSeries()
//                    .daily()
//                    .forSymbol(symbol)
//                    .fetchSync();
//            return response.getStockUnits().subList(0, 9);
//        }
//    }
//
//    class configNotDefinedException extends Exception {
//        public configNotDefinedException(String statement) {
//            super(statement);
//        }
//    }
}
