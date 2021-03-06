/**
 * 
 */
package com.mongo.test.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.mongo.test.domain.def.test.IBlock;
import com.mongo.test.domain.impl.test.TestResult;
import com.mongo.test.domain.impl.test.WebPageConfigLine;

/**
 * A web page block.
 * 
 */
@Embedded
public class WebPageBlock implements IBlock {

	private List<WebPageConfigLine> blockLines;

	private BlockLine columns;

	private String fixtureName;

	private TestResult testResult;

	/**
	 * 
	 */
	public WebPageBlock() {
		blockLines = new ArrayList<WebPageConfigLine>();
	}

	public List<WebPageConfigLine> getBlockLines() {
		return blockLines;
	}

	public void setBlockLines(List<WebPageConfigLine> blockLines) {
		this.blockLines = blockLines;
	}

	public BlockLine getColumns() {
		return columns;
	}

	public void setColumns(BlockLine columns) {
		this.columns = columns;
	}

	public String getFixtureName() {
		return fixtureName;
	}

	public void setFixtureName(String fixtureName) {
		this.fixtureName = fixtureName;
	}

	public void addLine(WebPageConfigLine line) {
		blockLines.add(line);
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}
}
