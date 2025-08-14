package com.example.cli;
import java.time.LocalDate;
import java.util.Map;

public class Car {
    private String brand;
    private String type;
    private String model;
    private LocalDate releaseDate;

    private Map<String, Double> price;  
    private Map<String, Double> prices;

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Map<String, Double> getPrice() {
        return price;
    }
    public void setPrice(Map<String, Double> price) {
        this.price = price;
    }

    public Map<String, Double> getPrices() {
        return prices;
    }
    public void setPrices(Map<String, Double> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", releaseDate=" + releaseDate +
                ", type='" + type + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                ", prices=" + prices +
                '}';
    }
}
