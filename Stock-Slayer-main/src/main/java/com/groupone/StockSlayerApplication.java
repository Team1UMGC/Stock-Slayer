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

}