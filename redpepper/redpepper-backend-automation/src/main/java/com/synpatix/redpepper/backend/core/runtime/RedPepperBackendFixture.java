package com.synpatix.redpepper.backend.core.runtime;

import com.mongo.test.domain.impl.test.TestResult;
import com.mongo.test.domain.impl.test.TestResult.ResultKind;
import com.synaptix.component.IComponent;
import com.synaptix.redpepper.commons.init.Check;
import com.synaptix.redpepper.commons.init.Display;
import com.synpatix.redpepper.backend.core.SynaptixBackendFixture;

/**
 * 
 * 
 * @author Nicolas Sauvage
 * 
 */
public class RedPepperBackendFixture extends SynaptixBackendFixture {

	public RedPepperBackendFixture() {
		super();
	}

	@Check("(\\w+).([.|\\w|\\[|\\]|=]+) is ([.|\\w|\\[|\\]|=| |/|:]+)")
	public TestResult getOperationProperty(String componentName, String propertyName, String expected) {
		String value = getProperty(componentName, propertyName);
		if (value == null) {
			return new TestResult(propertyName + " is null", ResultKind.FAILURE);
		} else if (value.equals(expected)) {
			return new TestResult();
		} else {
			return new TestResult(propertyName + " is " + value + " (should be: " + expected + ")");
		}
	}

	@Check("(\\w+).([.|\\w|\\[|\\]|=]+) null")
	public TestResult checkOperationPropertyNull(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("null")) {
			return new TestResult(propertyName + " is null", ResultKind.SUCCESS);
		} else {
			return new TestResult(propertyName + " is " + value);
		}
	}

	@Check("(\\w+).([.|\\w|\\[|\\]|=]+) not null")
	public TestResult checkOperationPropertyNotNull(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("null")) {
			return new TestResult(propertyName + " is " + value);
		} else {
			return new TestResult(propertyName + " is not null", ResultKind.SUCCESS);
		}
	}

	@Check("(\\w+).([.|\\w|\\[|\\]|=]+) empty")
	public TestResult checkOperationPropertyempty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("[]")) {
			return new TestResult(propertyName + " is empty", ResultKind.SUCCESS);
		} else {
			return new TestResult(propertyName + " is " + value);
		}
	}

	@Check("(\\w+).([.|\\w|\\[|\\]|=]+) not empty")
	public TestResult checkOperationPropertyNotEmpty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null || value.equals("[]")) {
			return new TestResult(propertyName + " is " + value);
		} else {
			return new TestResult(propertyName + " is not empty", ResultKind.SUCCESS);
		}
	}

	@Check("New test")
	public TestResult getOperationProperty() {
		userVariables.clear();
		return new TestResult();
	}

	@Display("Show (\\w+).([.|\\w]+)")
	public TestResult displayOperationProperty(String componentName, String propertyName) {
		String value = getProperty(componentName, propertyName);
		if (value == null) {
			return new TestResult(propertyName + " is null", ResultKind.ERROR);
		} else {
			TestResult testResult = new TestResult();
			testResult.setMessage(value);
			testResult.setResultKind(ResultKind.INFO);
			return testResult;
		}
	}

	@Display("Select ([\\w| ]+) : (\\w+) as (\\w+)")
	public TestResult getComponent(String componentName, String idValue, String variableName) {
		IComponent component = findComponent(componentName, idValue, variableName);
		return component != null ? new TestResult() : new TestResult("Object not found");
	}
}
