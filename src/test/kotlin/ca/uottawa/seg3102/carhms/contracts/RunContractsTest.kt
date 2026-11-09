package ca.uottawa.seg3102.carhms.contracts

import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("contracts")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ca.uottawa.seg3102.carhms.contracts")
class RunContractsTest
