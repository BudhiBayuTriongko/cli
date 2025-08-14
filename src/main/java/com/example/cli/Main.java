package com.example.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Command(name = "CarParserCLI", mixinStandardHelpOptions = true, version = "1.0", description = "Parses CSV and XML car files.")
public class Main implements Callable<Integer> {

	@Option(names = "-csv", description = "Path to CSV file", required = true)
	private String csvFile;

	@Option(names = "-xml", description = "Path to XML file", required = true)
	private String xmlFile;

	@Option(names = "--sort", description = "Sort by field: brand, price, date")
	private String sortBy;

	@Option(names = "--filterBrand", description = "Filter by brand name (case-insensitive)")
	private String filterBrand;

	@Option(names = "--filterPrice", description = "Filter by price in USD")
	private Double filterPrice;

	@Option(names = "--filterDate", description = "Filter by release date (format: yyyy-MM-dd or yyyy,dd,MM)")
	private String filterDate;

	@Option(names = "--outputFormat", description = "Output format: table, xml, json")
	private String outputFormat = "table"; // default table

	@Override
	public Integer call() {
		List<Car> allCars = new ArrayList<>();

		System.out.println("=== Parsing CSV: " + csvFile + " ===");
		List<Car> csvCars = new CsvParser().parse(csvFile);

		System.out.println("=== Parsing XML: " + xmlFile + " ===");
		List<Car> xmlCars = new XmlParser().parse(xmlFile);

		int minSize = Math.min(csvCars.size(), xmlCars.size());

		allCars = this.combineAllCars(minSize, allCars, csvCars, xmlCars);

		System.out.println("\n=== Combined Car Data ===");
		allCars.forEach(System.out::println);

		System.out.println("\n=== Combined Car Data (" + outputFormat + ") ===");
		allCars = this.setFilter(filterBrand, filterPrice, filterDate, allCars);
		allCars = this.setSort(filterBrand, filterDate, filterDate, allCars);
		this.output(allCars);

		

		return 0;
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}
	
	private List<Car> combineAllCars (int minSize, List<Car> allCars, List<Car> csvCars, List<Car> xmlCars ){
		for (int i = 0; i < minSize; i++) {
			Car combinedCar = new Car();

			// from CSV
			combinedCar.setBrand(csvCars.get(i).getBrand());
			combinedCar.setReleaseDate(csvCars.get(i).getReleaseDate());

			// from XML
			combinedCar.setType(xmlCars.get(i).getType());
			combinedCar.setPrice(xmlCars.get(i).getPrice());
			combinedCar.setPrices(xmlCars.get(i).getPrices());

			allCars.add(combinedCar);
		}
		return allCars;
	}

	private List<Car> setFilter(String filterBrand, Double filterPrice, String filterDate, List<Car> allCars) {
		// FILTER
		if (filterBrand != null) {
			allCars = allCars.stream().filter(car -> car.getBrand() != null
					&& car.getBrand().replace("\"", "").trim().equalsIgnoreCase(filterBrand.trim())).toList();
		}

		if (filterPrice != null) {
			allCars = allCars.stream()
					.filter(car -> car.getPrice() != null && car.getPrice().getOrDefault("USD", 0.0) == filterPrice)
					.toList();
		}

		if (filterDate != null) {
			try {
				LocalDate date;

				if (filterDate.contains("-")) {
					date = LocalDate.parse(filterDate.trim());
				} else if (filterDate.contains(",")) {
					String[] parts = filterDate.split(",");
					int year = Integer.parseInt(parts[0].trim());
					int day = Integer.parseInt(parts[1].trim());
					int month = Integer.parseInt(parts[2].trim());
					date = LocalDate.of(year, month, day);
				} else {
					throw new IllegalArgumentException("Invalid date format");
				}

				allCars = allCars.stream()
						.filter(car -> car.getReleaseDate() != null && car.getReleaseDate().equals(date)).toList();
			} catch (Exception e) {
				System.err.println("Invalid date format for --filterDate: " + e.getMessage());
			}
		}

		return allCars;
	}

	private List<Car> setSort(String filterBrand, String filterPrice, String filterDate, List<Car> allCars) {
		// SORT
		if ("releaseYear".equalsIgnoreCase(sortBy)) {
			// Sort Release Year (latest first)
			allCars.sort(Comparator.comparing(car -> car.getReleaseDate().getYear(), Comparator.reverseOrder()));
		} else if ("price".equalsIgnoreCase(sortBy)) {
			// Sort Price (highest first) in USD
			allCars.sort(
					Comparator.comparing(car -> car.getPrice().getOrDefault("USD", 0.0), Comparator.reverseOrder()));
		}

		return allCars;
	}

	private void printAsTable(List<Car> cars) {
		String format = "| %-15s | %-12s | %-10s | %-10s |  %-20s | %-40s |%n";
		System.out.format(
				"+-----------------+--------------+------------+------------+------------+-----------------------------+%n");
		System.out.format(
				"| Brand           | Release Date | Type       | Model      | Price      |Prices                                   |%n");
		System.out.format(
				"+-----------------+--------------+------------+------------+------------+-----------------------------+%n");

		for (Car car : cars) {
			System.out.format(format, car.getBrand(), car.getReleaseDate(), car.getType(), car.getModel(),car.getPrice(),
					car.getPrices());
		}

		System.out.format(
				"+-----------------+--------------+------------+------------+------------+-----------------------------+%n");
	}

	private void printAsJSON(List<Car> cars) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

			String jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cars);
			System.out.println(jsonOutput);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printAsXML(List<Car> cars) {
		try {
			JAXBContext context = JAXBContext.newInstance(CarListWrapper.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(new CarListWrapper(cars), System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Wrapper untuk JAXB XML output
	@jakarta.xml.bind.annotation.XmlRootElement(name = "cars")
	public static class CarListWrapper {
		private List<Car> cars;

		public CarListWrapper() {
		}

		public CarListWrapper(List<Car> cars) {
			this.cars = cars;
		}

		@jakarta.xml.bind.annotation.XmlElement(name = "car")
		public List<Car> getCars() {
			return cars;
		}

		public void setCars(List<Car> cars) {
			this.cars = cars;
		}
	}
	
	private void output(List<Car> allCars) {
		switch (outputFormat.toLowerCase()) {
		case "json":
			printAsJSON(allCars);
			break;
		case "xml":
			printAsXML(allCars);
			break;
		default:
			printAsTable(allCars);
			break;
		}
	}

}
