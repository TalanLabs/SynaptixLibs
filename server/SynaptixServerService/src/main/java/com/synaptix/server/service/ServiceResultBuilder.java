package com.synaptix.server.service;

import java.io.Serializable;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.model.ErrorEnum;
import com.synaptix.component.model.IError;

/**
 * Service Result Builder.<br>
 * Can add errors using {@link #addError(ErrorEnum)} and {@link #addError(ErrorEnum, String, String)}<br>
 * Can create a IServiceResult using {@link ServiceResultContainer#compileResult(Object)}
 *
 * @author Nicolas P
 *
 * @param <E>
 */
public class ServiceResultBuilder<E extends ErrorEnum> extends ServiceResultContainer {

	public ServiceResultBuilder() {
		super();
	}

	public ServiceResultBuilder(String name) {
		super(name);
	}

	/**
	 * Add an error to the error list
	 *
	 * @param errorCode
	 * @return
	 */
	public final ServiceResultBuilder<E> addError(E errorCode) {
		return addError(errorCode, null, null);
	}

	/**
	 * Add an error to the error list with given object id
	 *
	 * @param errorCode
	 * @param idObject
	 * @return
	 */
	public final ServiceResultBuilder<E> addError(E errorCode, Serializable idObject) {
		return addError(errorCode, idObject, null);
	}

	/**
	 * Add an error to the error list with given attribute
	 *
	 * @param errorCode
	 * @param attribute
	 * @return
	 */
	public final ServiceResultBuilder<E> addError(E errorCode, String attribute) {
		return addError(errorCode, attribute, null);
	}

	/**
	 * Add an error to the error list with given object id and value
	 *
	 * @param errorCode
	 * @param idObject
	 * @param value
	 * @return
	 */
	public final ServiceResultBuilder<E> addError(E errorCode, Serializable idObject, String value) {
		return addError(errorCode, String.valueOf(idObject), value);
	}

	/**
	 * Add an error to the error list with given attribute and value
	 *
	 * @param errorCode
	 * @param attribute
	 * @param value
	 *            Maximum 50 characters
	 * @return
	 */
	public final ServiceResultBuilder<E> addError(E errorCode, String attribute, String value) {
		IError error = ComponentFactory.getInstance().createInstance(IError.class);
		error.setErrorCode(errorCode);
		error.setAttribute(attribute);
		error.setValue(value);
		addError(error);
		return this;
	}
}
