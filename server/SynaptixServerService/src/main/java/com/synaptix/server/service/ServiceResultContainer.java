package com.synaptix.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.model.ErrorEnum;
import com.synaptix.component.model.IError;
import com.synaptix.component.model.IServiceResult;
import com.synaptix.component.model.IServiceResultComponent;
import com.synaptix.component.model.IServiceWithErrorResult;
import com.synaptix.component.model.IStackResult;

/**
 * Service Result Container. Can create a IServiceResult using {@link #compileResult(Object)}
 *
 * @author Nicolas P
 */
public class ServiceResultContainer {

	private final Set<IError> errorSet;

	private final IStackResult stackResult;

	private boolean newErrors;

	public ServiceResultContainer() {
		this((String) null);
	}

	public ServiceResultContainer(String name) {
		this.stackResult = ComponentFactory.getInstance().createInstance(IStackResult.class);
		this.stackResult.setStackResultList(new ArrayList<IStackResult>());
		this.errorSet = new HashSet<IError>();

		this.stackResult.setClassName(name);
	}

	public <O> ServiceResultContainer(IServiceResult<O> serviceResult) {
		this();

		ingest(serviceResult);
	}

	/**
	 * Create a new service result container initialized with a list of errors
	 *
	 * @param errorSet
	 */
	public ServiceResultContainer(Set<IError> errorSet) {
		this(errorSet, null);
	}

	public ServiceResultContainer(Set<IError> errorSet, String name) {
		super();

		this.stackResult = ComponentFactory.getInstance().createInstance(IStackResult.class);
		this.stackResult.setStackResultList(new ArrayList<IStackResult>());
		this.errorSet = new HashSet<IError>(errorSet);

		this.stackResult.setClassName(name);
	}

	public static final <O> IServiceResult<O> compile(O object) {
		return compile(object, null, null);
	}

	public static final <O> IServiceResult<O> compile(O object, String resultCode, String resultText) {
		return new ServiceResultContainer().compileResult(object, resultCode, resultText);
	}

	public static final <O> ServiceResultContainer build(IServiceResult<O> serviceResult) {
		ServiceResultContainer serviceResultContainer = new ServiceResultContainer();
		serviceResultContainer.ingest(serviceResult);
		return serviceResultContainer;
	}

	protected final void addError(IError error) {
		errorSet.add(error);
		newErrors = true;
	}

	/**
	 * Get the unmodifiable set of errors
	 *
	 * @return
	 */
	public final Set<IError> getErrorSet() {
		return Collections.unmodifiableSet(errorSet);
	}

	/**
	 * Construct a new IServiceResult with given object.<br>
	 * This object contains the error list (hidden)
	 */
	public final <O> IServiceResult<O> compileResult(O object) {
		return compileResult(object, null, null);
	}

	/**
	 * Construct a new IServiceResult with given object and result information.<br>
	 * This object contains the error list (hidden)
	 */
	public final <O> IServiceResult<O> compileResult(O object, String resultCode) {
		return compileResult(object, resultCode, null);
	}

	/**
	 * Builds a IServiceResult with given object. Also builds a stack with resultCode and resultText, stored in getStackResult()<br/>
	 * If result text or the name of the service result is null, it shouldn't represent a level when unstacking
	 */
	public final <O> IServiceResult<O> compileResult(O object, String resultCode, String resultText) {
		@SuppressWarnings("unchecked")
		IServiceResultComponent<O> serviceResultComponent = ComponentFactory.getInstance().createInstance(IServiceResultComponent.class);
		serviceResultComponent.setErrorSet(errorSet);
		serviceResultComponent.setError(hasError());
		serviceResultComponent.setObject(object);
		serviceResultComponent.setStackResult(stackResult);
		serviceResultComponent.setResultCode(resultCode);
		serviceResultComponent.setResultText(resultText);
		stackResult.setResultCode(resultCode);
		stackResult.setResultText(resultText);
		stackResult.setResultDateTime(new Date());
		return serviceResultComponent;
	}

	/**
	 * Returns true if container has at least one error
	 *
	 * @return
	 */
	public boolean hasError() {
		return !errorSet.isEmpty();
	}

	/**
	 * Returns true if current process has the specified error
	 *
	 * @param errorEnum
	 * @return
	 */
	public boolean hasError(ErrorEnum errorEnum) {
		boolean containsError = false;
		for (IError error : errorSet) {
			if (error.getErrorCode().equals(errorEnum)) {
				containsError = true;
			}
		}
		return containsError;
	}

	/**
	 * Returns the number of errors
	 *
	 * @return
	 */
	public int getNbError() {
		return errorSet.size();
	}

	/**
	 * Ingest service result:<br>
	 * - Add errors to its own errors<br>
	 * - Return object
	 *
	 * @param serviceResult
	 * @return
	 */
	public final <O> O ingest(IServiceResult<O> serviceResult) {
		if (serviceResult != null) {
			if (IServiceWithErrorResult.class.isAssignableFrom(serviceResult.getClass())) {
				IServiceWithErrorResult<O> serviceWithErrorResult = (IServiceWithErrorResult<O>) serviceResult;
				if (CollectionUtils.isNotEmpty(serviceWithErrorResult.getErrorSet())) {
					serviceWithErrorResult.setError(true);
					for (IError error : serviceWithErrorResult.getErrorSet()) {
						addError(error);
					}
				}
				if (serviceWithErrorResult.getStackResult() != null) {
					if (stackResult.getStackResultList() == null) {
						stackResult.setStackResultList(new ArrayList<IStackResult>());
					}
					stackResult.getStackResultList().add(serviceWithErrorResult.getStackResult());
				}
			}
			return serviceResult.getObject();
		}
		return null;
	}

	/**
	 * Acquit errors to use hasNewErrors later
	 */
	public void acquitErrors() {
		newErrors = false;
	}

	/**
	 * Returns true if has new errors. Use acquitErrors to acquit old errors
	 */
	public boolean hasNewErrors() {
		return newErrors;
	}

	/**
	 * Return current error code set (unmodifiable set)
	 *
	 * @return
	 */
	public Set<ErrorEnum> getErrorCodeSet() {
		Set<ErrorEnum> set = new HashSet<ErrorEnum>();
		if (hasError()) {
			for (IError error : errorSet) {
				set.add(error.getErrorCode());
			}
			set = Collections.unmodifiableSet(set);
		}
		return set;
	}
}
