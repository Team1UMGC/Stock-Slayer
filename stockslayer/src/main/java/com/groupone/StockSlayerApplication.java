package com.groupone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.groupone")
public class StockSlayerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(StockSlayerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StockSlayerApplication.class);
    }

    // Sample login credentials
    private static final String VALID_USERNAME = "test";
    private static final String VALID_PASSWORD = "password";

    @Controller
    class StockController {
        private List<Stock> heldStocks = new ArrayList<>();
        private List<Stock> allStocks = createSampleStocks(); // Create some sample stocks

        @GetMapping("/main")
        public String main(Model model) {
            model.addAttribute("heldStocks", heldStocks);
            model.addAttribute("allStocks", allStocks);
            return "main";
        }

        @PostMapping("/search")
        public String searchStock(@RequestParam("stockName") String stockName, Model model) {
            // Search for a stock by name in allStocks and populate 'stockInfo'
            Stock stockInfo = findStockByName(stockName);
            model.addAttribute("stockInfo", stockInfo);
            model.addAttribute("heldStocks", heldStocks);
            model.addAttribute("allStocks", allStocks);
            return "main";
        }

        @PostMapping("/buy")
        public String buyStock(@RequestParam("symbol") String symbol, @RequestParam("name") String name,
                               @RequestParam("price") double price, @RequestParam("buyShares") int buyShares) {
            // Need to add stock buying logic here
            // Update heldStocks with newly purchased stock
            heldStocks.add(new Stock(symbol, name, price, buyShares));
            return "redirect:/main";
        }

        @PostMapping("/sell/{index}")
        public String sellStock(@PathVariable("index") int index) {
            // Add stock selling logic here
            // Update heldStocks after selling
            if (index >= 0 && index < heldStocks.size()) {
                heldStocks.remove(index);
            }
            return "redirect:/main";
        }

        @PostMapping("/sortShares")
        public String sortShares() {
            // Add sorting logic by shares here
            Collections.sort(heldStocks, Comparator.comparingInt(Stock::getShares));
            return "redirect:/main";
        }

        @PostMapping("/sortHighestValue")
        public String sortHighestValue() {
            Collections.sort(heldStocks, (s1, s2) -> {
                double value1 = s1.getPrice() * s1.getShares();
                double value2 = s2.getPrice() * s2.getShares();
                return Double.compare(value2, value1); // Reverse order for highest value first
            });
            return "redirect:/main";
        }

        @PostMapping("/sortLowestValue")
        public String sortLowestValue() {
            Collections.sort(heldStocks, (s1, s2) -> {
                double value1 = s1.getPrice() * s1.getShares();
                double value2 = s2.getPrice() * s2.getShares();
                return Double.compare(value1, value2); // Regular order for lowest value first
            });
            return "redirect:/main";
        }

        class Stock {
            private String symbol;
            private String name;
            private double price;
            private int shares;

            public Stock(String symbol, String name, double price, int shares) {
                this.symbol = symbol;
                this.name = name;
                this.price = price;
                this.shares = shares;
            }

            public String getSymbol() {
                return symbol;
            }

            public String getName() {
                return name;
            }

            public double getPrice() {
                return price;
            }

            public int getShares() {
                return shares;
            }
        }

        private Stock findStockByName(String stockName) {
            // Refine stock searching logic
            for (Stock stock : allStocks) {
                if (stock.getName().equalsIgnoreCase(stockName)) {
                    return stock;
                }
            }
            return null; // stock not found
        }

        private List<Stock> createSampleStocks() {
            // Sample stocks, to be replaced with real world options from API
            List<Stock> sampleStocks = new ArrayList<>();
            sampleStocks.add(new Stock("AAPL", "Apple Inc.", 130.0, 12));
            sampleStocks.add(new Stock("GOOGL", "Alphabet Inc.", 230.0, 20));
            sampleStocks.add(new Stock("MSFT", "Microsoft Corporation", 100.0, 7));
            return sampleStocks;
        }
    }

    @Controller
    class LoginController {

        @GetMapping("/login")
        public String loginForm() {
            return "login";
        }

        @PostMapping("/login/authenticate")
        public String authenticate(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {
            if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
                return "redirect:/main";
            } else {
                // Authentication failed, return to login page
                return "login";
            }
        }
    }
}
