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

	public String getClassName();

	public void setClassName(String className);

	public String getResultCode();

	public void setResultCode(String resultCode);

	public String getResultText();

	public void setResultText(String resultText);

	public Date getResultDateTime();

	public void setResultDateTime(Date resultDateTime);

	public List<IStackResult> getStackResultList();

	public void setStackResultList(List<IStackResult> stackResultList);

}
