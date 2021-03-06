/**
 * 
 */
package com.mongo.test.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.mongo.test.domain.def.test.IBlock;
import com.mongo.test.domain.impl.test.TestLine;

/**
 * A test block.
 * 
 * @author E413544
 * 
 */
// @Entity(value = "blocks")
@Embedded
public class TestBlock implements IBlock {

	private List<TestLine> blockLines;
	private String fixtureName;

	public TestBlock() {
		blockLines = new ArrayList<TestLine>();
	}

	public List<TestLine> getBlockLines() {
		return blockLines;
	}

	public void setBlockLines(List<TestLine> blockLines) {
		this.blockLines = blockLines;
	}

	/**
	 * Add a test line
	 * 
	 * @param cellsContent
	 * @param comment
	 */
	public void addLine(String test, String expected, String comment) {
		TestLine blockLine = new TestLine(test, expected, comment);
		blockLine.setTestCommentString(comment);
		this.blockLines.add(blockLine);
	}

	public String getFixtureName() {
		return fixtureName;
	}

	public void setFixtureName(String fixtureName) {
		this.fixtureName = fixtureName;
	}
}
