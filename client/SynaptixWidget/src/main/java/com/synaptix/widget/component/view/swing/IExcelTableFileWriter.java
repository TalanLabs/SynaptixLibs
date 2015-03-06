package com.synaptix.widget.component.view.swing;

import java.io.File;
import java.util.List;

import com.synaptix.component.IComponent;

public interface IExcelTableFileWriter<E extends IComponent> {

	/**
	 * Create excel from table
	 * 
	 * @param file
	 * @param components
	 */
	public void createExcelFromTable(File file, List<E> components) throws Exception;

	/**
	 * Get the component at given row index
	 * 
	 * @param rowIndex
	 * @return
	 */
	public E getComponent(int rowIndex);

}
