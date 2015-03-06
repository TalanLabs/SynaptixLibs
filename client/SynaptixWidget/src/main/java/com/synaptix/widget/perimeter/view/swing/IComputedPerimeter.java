package com.synaptix.widget.perimeter.view.swing;

/**
 * Interface for a perimeter which means the perimeter doesn't return the final value in the {@link IPerimeterWidget#getValue()} method and can provide with a computed final value.<br/>
 * 
 * @author Nicolas P
 * 
 */
public interface IComputedPerimeter {

	public Object getFinalValue();

}
