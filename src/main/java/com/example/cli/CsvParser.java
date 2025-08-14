package com.example.cli;


import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    public List<Car> parse(String filePath) {
        List<Car> cars = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) { // skip header
                    isFirstLine = false;
                    continue;
                }
                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    String brand = fields[0].trim();
                    String dateStr = fields[1].trim().replace("\"", "");
                    LocalDate releaseDate = LocalDate.parse(dateStr, formatter);

                    Car car = new Car();
                    car.setBrand(brand);
                    car.setReleaseDate(releaseDate);
                    cars.add(car);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing CSV: " + e.getMessage());
        }
        return cars;
    }

}
