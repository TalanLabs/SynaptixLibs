/**
 * 
 */
package com.mongo.test.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.mongo.test.domain.def.test.IBlock;
import com.mongo.test.domain.impl.test.ComponentConfigLine;

/**
 * A component configuration block.
 * 
 * @author E413544
 * 
 */
// @Entity(value = "blocks")
@Embedded
public class ConfigBlock implements IBlock {

	@Embedded
	private List<ComponentConfigLine> lines;

	public ConfigBlock() {
		lines = new ArrayList<ComponentConfigLine>();
	}

	public List<ComponentConfigLine> getLines() {
		return lines;
	}

	public void setLines(List<ComponentConfigLine> lines) {
		this.lines = lines;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	private String componentName;

	public void addLine(String testName, String systemName, String componentAssociation) {
		lines.add(new ComponentConfigLine(testName, systemName, componentAssociation));
	}

}
