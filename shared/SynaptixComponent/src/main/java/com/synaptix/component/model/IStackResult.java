package com.synaptix.component.model;

import java.util.Date;
import java.util.List;

import com.synaptix.component.IComponent;

/**
 * Any use of ServiceResultContainer uses a stack result
 *
 * @author Nicolas P
 */
public interface IStackResult extends IComponent {

	String getClassName();

	void setClassName(String className);

	String getResultCode();

	void setResultCode(String resultCode);

	String getResultText();

	void setResultText(String resultText);

	Date getResultDateTime();

	void setResultDateTime(Date resultDateTime);

	List<IStackResult> getStackResultList();

	void setStackResultList(List<IStackResult> stackResultList);

}
