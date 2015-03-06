package com.synaptix.widget.perimeter.view.swing;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;

@SynaptixComponent
public interface IPerimeterWidgetDescriptor extends IComponent {

	public String getId();

	public void setId(String id);

	public IPerimeterWidget getPerimeterWidget();

	public void setPerimeterWidget(IPerimeterWidget perimeterWidget);

}
