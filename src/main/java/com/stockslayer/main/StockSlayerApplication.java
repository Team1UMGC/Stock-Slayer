package com.groupone.stockslayer;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class StockSlayerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StockSlayer();
        });

        SpringApplication.run(StockSlayerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StockSlayerApplication.class);
    }
}
