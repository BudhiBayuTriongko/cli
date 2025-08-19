1.	Build maven project using   (mvn clean package)

2.	Run the project using (java -jar target/CarParserCLI-1.0.jar -csv CarsBrand.csv -xml CarsType.xml) default

3.	Debug in eclipse using (java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar 		target/CarParserCLI-1.0.jar -csv CarsBrand.csv -xml CarsType.xml)

4.	Using Filtering (java -jar target/CarParserCLI-1.0.jar -csv=CarsBrand.csv -xml=CarsType.xml --filterBrand=Toyota --	filterDate=2023-01-15)

5.	Using sort (java -jar target/CarParserCLI-1.0.jar -csv CarsBrand.csv -xml CarsType.xml --sort releaseYear)

6.	Output table as a parameter (java -jar target/CarParserCLI-1.0.jar -csv=CarsBrand.csv -xml=CarsType.xml --	outputFormat=table)

7.	Output xml as a parameter using (java -jar target/CarParserCLI-1.0.jar -csv=CarsBrand.csv -xml=CarsType.xml --	outputFormat=xml)

8.	Output json as a parameter using (java -jar target/CarParserCLI-1.0.jar -csv=CarsBrand.csv -xml=CarsType.xml --	outputFormat=json)