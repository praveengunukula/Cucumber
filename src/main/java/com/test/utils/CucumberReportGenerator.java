package com.test.utils;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.json.support.Status;


import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CucumberReportGenerator {
	
	public static void generateReport() {
        File reportOutputDirectory = new File("target/cucumber-reports");
        List<String> jsonFiles = Arrays.asList("target/cucumber-reports/report.json"); // Path to your Cucumber JSON report file(s)

        String projectName = "Your Project Name";
        
        // Create Configuration object and set necessary configurations
        Configuration configuration = new Configuration(reportOutputDirectory, projectName);
        configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
        configuration.addPresentationModes(PresentationMode.PARALLEL_TESTING);

        // Create a Set<Status> to specify not failing statuses
        Set<Status> notFailingStatuses =new HashSet<Status>();
        notFailingStatuses.add(Status.PASSED);
        notFailingStatuses.add(Status.SKIPPED);
        configuration.setNotFailingStatuses(notFailingStatuses);

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
    }
}
