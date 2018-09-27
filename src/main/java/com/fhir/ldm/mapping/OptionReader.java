package com.fhir.ldm.mapping;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class OptionReader {

	public String serverBase = null;
	public String identifier = null;

	public OptionReader(String[] args) throws IOException {

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		Options options = new Options();
		options.addOption("i", "id", true, "research subject id (optional)");
		options.addOption("s", "server", true, "server url");
		options.addOption("h", "help", false, "print this help message");



		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);


			if (line.hasOption("server")) {
				// print the value of block-size
				serverBase = line.getOptionValue("server");
			}
			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("haloextract", options);
				return;
			}

			if (line.hasOption("id")) {
				// print the value of block-size
				identifier = line.getOptionValue("id");
			}

		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}

	}


}
