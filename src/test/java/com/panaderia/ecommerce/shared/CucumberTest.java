package com.panaderia.ecommerce.shared;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "com.panaderia.ecommerce.shared",
        "com.panaderia.ecommerce.shared.testing.steps"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports.html",
        "json:target/cucumber-report.json"
    }
)
public class CucumberTest {
}