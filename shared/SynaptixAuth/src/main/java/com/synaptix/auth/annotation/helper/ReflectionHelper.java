/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.synaptix.auth.annotation.helper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * {@link ReflectionHelper} is an internal class that provides common routines
 * only used by the annotation processors.
 * 
 * @author Brendan Doherty
 * @author Florian Sauter
 * @author Stephen Haberman (concept)
 */
@SuppressWarnings("unchecked")
public class ReflectionHelper {

	private TypeElement classRepresenter;
	private ProcessingEnvironment environment;

	public ReflectionHelper(ProcessingEnvironment environment, TypeElement classRepresenter) {
		this.classRepresenter = classRepresenter;
		this.environment = environment;
	}

	public Collection<VariableElement> filterConstantFields(Collection<VariableElement> fieldElements) {
		return filterFields(fieldElements, Modifier.STATIC, Modifier.FINAL);
	}

	/**
	 * Returns only fields which are not annotated with one of the passed
	 * annotation.
	 */
	public Collection<VariableElement> filterFields(Collection<VariableElement> fieldElements, Class<? extends Annotation>... annotations) {
		Collection<VariableElement> filteredFields = new ArrayList<VariableElement>();
		filteredFields.addAll(fieldElements);
		for (VariableElement fieldElement : fieldElements) {
			for (Class<? extends Annotation> passedAnnotation : annotations) {
				Annotation fieldAnnotation = fieldElement.getAnnotation(passedAnnotation);
				if (fieldAnnotation != null) {
					filteredFields.remove(fieldElement);
					break;
				}
			}
		}
		return filteredFields;
	}

	/**
	 * Returns only fields which do not contain one of the passed modifiers.
	 */
	public Collection<VariableElement> filterFields(Collection<VariableElement> fieldElements, Modifier... modifiers) {
		Collection<VariableElement> filteredFields = new ArrayList<VariableElement>();
		filteredFields.addAll(fieldElements);
		for (VariableElement fieldElement : fieldElements) {
			for (Modifier modifier : modifiers) {
				if (fieldElement.getModifiers().contains(modifier)) {
					filteredFields.remove(fieldElement);
					break;
				}
			}
		}
		return filteredFields;
	}

	/**
	 * Returns only fields which simple names do not equal the passed field
	 * names.
	 */
	public Collection<VariableElement> filterFields(Collection<VariableElement> fieldElements, String... simpleFieldNames) {
		Collection<VariableElement> filteredFields = new ArrayList<VariableElement>();
		filteredFields.addAll(fieldElements);
		for (VariableElement fieldElement : fieldElements) {
			for (String simpleFieldName : simpleFieldNames) {
				if (fieldElement.getSimpleName().toString().equals(simpleFieldName)) {
					filteredFields.remove(fieldElement);
					break;
				}
			}
		}
		return filteredFields;
	}

	/**
	 * Returns all fields annotated with the passed annotation classes.
	 */
	public Collection<VariableElement> getAnnotatedFields(Class<? extends Annotation>... annotations) {
		Collection<VariableElement> fieldsCopy = getFields();
		for (Class<? extends Annotation> annotation : annotations) {
			Collection<VariableElement> nonAnnotatedFields = filterFields(getFields(), annotation);
			fieldsCopy.removeAll(nonAnnotatedFields);
		}
		return fieldsCopy;
	}

	/**
	 * Returns the class name.
	 * <p>
	 * For example:<br>
	 * {@code com.gwtplatform.dispatch.annotation.Foo}
	 * </p>
	 * 
	 * @return the class name.
	 */
	public String getClassName() {
		String packageName = getPackageName();
		return packageName != null ? packageName + '.' + getSimpleClassName() : getSimpleClassName();
	}

	public TypeElement getClassRepresenter() {
		return classRepresenter;
	}

	/**
	 * Returns all fields ordered that are {@link Modifier.FINAL} or
	 * {@link Modifier.STATIC}.
	 */
	public Collection<VariableElement> getConstantFields() {
		return getModifierFields(Modifier.FINAL, Modifier.STATIC);
	}

	/**
	 * Returns all fields.
	 * <p>
	 * <b>Important:</b> Fields are not sorted according to @{@link Order}!
	 * </p>
	 * To get these sorted use {@link ReflectionHelper#getOrderedFields()}.
	 */
	public Collection<VariableElement> getFields() {
		List<? extends Element> members = getElementUtils().getAllMembers(classRepresenter);
		return ElementFilter.fieldsIn(members);
	}

	/**
	 * Returns all fields with the passed modifier.
	 */
	public Collection<VariableElement> getModifierFields(Modifier... modifiers) {
		Collection<VariableElement> modifierFields = new ArrayList<VariableElement>();
		modifierFields.addAll(getFields());
		for (Modifier modifier : modifiers) {
			Collection<VariableElement> nonModifierFields = filterFields(getFields(), modifier);
			modifierFields.removeAll(nonModifierFields);
		}
		return modifierFields;
	}

	public String getPackageName() {
		String res = null;
		PackageElement packageElement = getElementUtils().getPackageOf(classRepresenter);
		if (packageElement != null && packageElement.getQualifiedName() != null) {
			res = packageElement.getQualifiedName().toString();
		}
		return res;
	}

	public ProcessingEnvironment getProcessingEnvironment() {
		return environment;
	}

	public String getSimpleClassName() {
		return classRepresenter.getSimpleName().toString();
	}

	/**
	 * Utility method.
	 */
	protected Elements getElementUtils() {
		return environment.getElementUtils();
	}

	public List<ExecutableElement> getMethods() {
		List<? extends Element> members = getElementUtils().getAllMembers(classRepresenter);
		return ElementFilter.methodsIn(members);
	}
}