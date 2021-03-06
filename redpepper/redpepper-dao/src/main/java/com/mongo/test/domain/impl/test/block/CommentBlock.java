/**
 * 
 */
package com.mongo.test.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.mongo.test.domain.def.test.IBlock;

/**
 * A comment block.
 * 
 * @author E413544
 * 
 */
// @Entity(value = "blocks")
@Embedded
public class CommentBlock implements IBlock {

	private List<String> lines;

	public CommentBlock() {
		setLines(new ArrayList<String>());
	}

	/**
	 * Add a test line
	 * 
	 * @param cellsContent
	 * @param comment
	 */
	public void addLine(String line) {
		this.getLines().add(line);
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}
}
