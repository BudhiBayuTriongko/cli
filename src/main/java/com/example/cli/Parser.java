package com.example.cli;

import java.io.File;
import java.util.List;

public interface Parser {
	  List<Car> parse(File f) throws Exception;
}
