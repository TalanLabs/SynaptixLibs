/**
 * 
 */
package com.synpatix.redpepper.backend.core.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.CaseFormat;
import com.mongo.test.domain.def.test.IBlock;
import com.mongo.test.domain.impl.test.ComponentConfigLine;
import com.mongo.test.domain.impl.test.TestLine;
import com.mongo.test.domain.impl.test.TestPage;
import com.mongo.test.domain.impl.test.TestResult;
import com.mongo.test.domain.impl.test.TestResult.ResultKind;
import com.mongo.test.domain.impl.test.WebPageConfigLine;
import com.mongo.test.domain.impl.test.block.BlockLine;
import com.mongo.test.domain.impl.test.block.CommentBlock;
import com.mongo.test.domain.impl.test.block.ConfigBlock;
import com.mongo.test.domain.impl.test.block.InsertBlock;
import com.mongo.test.domain.impl.test.block.SetupBlock;
import com.mongo.test.domain.impl.test.block.TestBlock;
import com.mongo.test.domain.impl.test.block.WebPageBlock;
import com.synaptix.redpepper.automation.elements.impl.DefaultWebPage;
import com.synaptix.redpepper.commons.init.Check;
import com.synaptix.redpepper.commons.init.Display;
import com.synaptix.redpepper.commons.init.ITestManager;

/**
 * @author E413544
 * 
 */
public class RedPepperTestRunner {

	private final ITestManager testManager;
	private final RepositorySetup repoSetup;

	public RedPepperTestRunner(ITestManager testManager) {
		this.testManager = testManager;
		this.repoSetup = testManager.getClassInstance(RepositorySetup.class);
		// this.repoSetup.clean(); // FIXME NicoSau part en vacances et casse la compil ... rha la la
	}

	/**
	 * @param result
	 * @return
	 * @throws IllegalAccessException
	 */
	public TestPage run(TestPage testPage) throws IllegalAccessException {
		testPage.startExecution();
		for (IBlock block : testPage.getBlocks()) {
			if (block instanceof CommentBlock) {
				// runCommentBlock((CommentBlock) block, report);
			} else if (block instanceof WebPageBlock) {
				runWebPageBlock((WebPageBlock) block, testPage);
			} else if (block instanceof TestBlock) {
				runTestBlock((TestBlock) block, testPage);
			} else if (block instanceof TestPage) {
				runTestPage((TestPage) block, testPage);
			} else if (block instanceof SetupBlock) {
				runSetupBlock((SetupBlock) block, testPage);
			} else if (block instanceof ConfigBlock) {
				runConfigBlock((ConfigBlock) block, testPage);
			} else if (block instanceof InsertBlock) {
				runInsertBlock((InsertBlock) block, testPage);
			}
		}
		testPage.stopExecution();
		return testPage;
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runWebPageBlock(WebPageBlock block, TestPage testPage) {
		repoSetup.addPage(block.getFixtureName());
		DefaultWebPage page = repoSetup.getPage(block.getFixtureName());
		for (WebPageConfigLine line : block.getBlockLines()) {
			page.addElement(line.getElementName(), line.getType(), line.getMethod(), line.getLocator(), line.getPosition());
		}
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runInsertBlock(InsertBlock block, TestPage testPage) {
		String entityName = block.getComponentName();

		// Get row values
		for (BlockLine line : block.getBlockLines()) {
			Map<String, String> values = new HashMap<String, String>();
			for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
				String cell = line.getCellAt(cellIndex);
				values.put(block.getColumns().getCellAt(cellIndex), cell);
			}

			TestResult result = insertEntity(entityName, values);
			line.setTestResult(result);
			testPage.addResult(result);
		}
	}

	private TestResult insertEntity(String entityName2, Map<String, String> values2) {
		System.out.println("Insert entity: " + entityName2 + " [" + values2 + "]");
		TestResult result;
		try {
			result = repoSetup.insertComponent(entityName2, values2);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
		}
		return result;
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runConfigBlock(ConfigBlock block, TestPage testPage) {
		for (ComponentConfigLine line : block.getLines()) {
			TestResult result = repoSetup.addProperty(block.getComponentName(), line.getTestName(), line.getSystemName(), line.getComponentAssociation());
			line.setResult(result);
			testPage.addResult(result);
		}
	}

	/**
	 * @param block
	 * @param testPage
	 */
	private void runSetupBlock(SetupBlock block, TestPage testPage) {
		// Get fixture name
		String fixtureName = block.getFixtureName();

		// Get columns names
		List<String> columnsList = new ArrayList<String>();
		for (String cell : block.getColumns().getCells()) {
			if (cell.contains(" ")) {
				cell = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, cell.replace(" ", "_"));
			}
			columnsList.add(cell);
		}

		// Get values
		for (BlockLine row : block.getBlockLines()) {

			TestResult result;
			if (fixtureName.equals("domain")) {
				result = addDomain(row.getCellAt(0), row.getCellAt(1), row.getCellAt(2));
				row.setTestResult(result);
				testPage.addResult(result);
			} else if (fixtureName.equals("entity")) {
				result = addEntity(row.getCellAt(1), row.getCellAt(0), row.getCellAt(2));
				row.setTestResult(result);
				testPage.addResult(result);
			} else if (fixtureName.equals("service")) {
				result = addService(row.getCellAt(0), row.getCellAt(1));
				row.setTestResult(result);
				testPage.addResult(result);
			} else {
				Class<?> fixtureClass = repoSetup.getService(fixtureName);
				if (fixtureClass == null) {
					block.setTestResult(new TestResult("Fixture " + fixtureName + " not found", ResultKind.ERROR));
					return;
				}
				Object instance = testManager.getClassInstance(fixtureClass);
				for (int cellIndex = 0; cellIndex < row.getCells().size(); cellIndex++) {
					String cell = row.getCellAt(cellIndex);
					try {
						fixtureClass.getField(columnsList.get(cellIndex)).set(instance, cell);
					} catch (Exception e) {
						e.printStackTrace();
						row.setTestResult(new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR));
						return;
					}
				}
				try {
					fixtureClass.getMethod("enterRow").invoke(instance);
					row.setTestResult(new TestResult("Done", ResultKind.INFO));
				} catch (Exception e) {
					e.printStackTrace();
					row.setTestResult(new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR));
				}
			}
		}
		for (BlockLine row : block.getBlockLines()) {
			testPage.addResult(row.getTestResult());
		}
		return;
	}

	private TestResult addDomain(String domainTestName, String domainClassName, String tableName) {
		System.out.println("Add domain: [" + domainTestName + "] [" + domainClassName + "] [" + tableName + "]");
		return repoSetup.addDomain(domainClassName, domainTestName, tableName);
	}

	private TestResult addEntity(String className, String testName, String searchBy) {
		return repoSetup.addClass(className, testName, searchBy);
	}

	private TestResult addService(String testName, String className) {
		return repoSetup.addService(testName, className);
	}

	/**
	 * @param block
	 * @param testPage
	 * @throws IllegalAccessException
	 */
	private void runTestPage(TestPage block, TestPage testPage) throws IllegalAccessException {
		this.run(block);
	}

	/**
	 * @param block
	 * @param testPage
	 * @throws IllegalAccessException
	 */
	private void runTestBlock(TestBlock block, TestPage testPage) throws IllegalAccessException {
		String scenarioService = block.getFixtureName();

		for (TestLine line : block.getBlockLines()) {
			line.startExecution();
			TestResult result = parseServiceCall(line.getTest(), scenarioService);
			line.stopExecution();
			if ("KO".equals(line.getExpected()) && ResultKind.FAILURE.equals(result.getResultKind())) {
				result.setResultKind(ResultKind.SUCCESS);
			}
			line.setTestResult(result);
			testPage.addResult(result);
		}

	}

	private TestResult parseServiceCall(String string, String scenarioService) throws IllegalAccessException {
		string = string.replace("*", "");
		System.out.println("Try: " + string);
		Class<?> serviceClass = repoSetup.getService(scenarioService);
		if (serviceClass == null) {
			throw new IllegalAccessException("Service " + scenarioService + " not found");
			// return new TestResult("Service " + scenarioService + " not found", ResultKind.ERROR);
		}
		Object instance = testManager.getClassInstance(serviceClass);
		FixtureService methodAndMatcher = findMethodInClass(string, serviceClass);
		if (methodAndMatcher == null) {
			methodAndMatcher = findMethodInClass(string, serviceClass.getSuperclass());
		}
		if (methodAndMatcher == null) {
			methodAndMatcher = findMethodInClass(string, RedPepperBackendFixture.class);
			instance = testManager.getClassInstance(RedPepperBackendFixture.class);
		}
		if (methodAndMatcher != null) {
			Matcher matcher = methodAndMatcher.matcher;
			matcher.matches();
			int groupCount = matcher.groupCount();
			Object[] args = new Object[groupCount];
			for (int i = 0; i < groupCount; i++) {
				args[i] = matcher.group(i + 1);
			}

			try {
				return (TestResult) methodAndMatcher.method.invoke(instance, args);
			} catch (Exception e) {
				e.printStackTrace();
				return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
			}
		} else {
			return new TestResult("Method not found");
		}
	}

	private FixtureService findMethodInClass(String string, Class<?> serviceClass) {
		Method[] methods = serviceClass.getMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				String methodRegex = null;
				if (annotation.annotationType().equals(Check.class)) {
					methodRegex = ((Check) annotation).value();
				}
				if (annotation.annotationType().equals(Display.class)) {
					methodRegex = ((Display) annotation).value();
				}
				if (methodRegex != null) {
					Pattern regexPattern = Pattern.compile(methodRegex);
					Matcher matcher = regexPattern.matcher(string);
					boolean matches = matcher.matches();
					if (matches) {
						return new FixtureService(method, matcher);
					}
				}
			}
		}
		return null;
	}

	public class FixtureService {
		Method method;
		Matcher matcher;

		public FixtureService(Method method, Matcher matcher) {
			this.method = method;
			this.matcher = matcher;
		}
	}

}
