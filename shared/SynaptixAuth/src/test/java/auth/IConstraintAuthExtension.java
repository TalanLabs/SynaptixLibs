package auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IConstraintAuthExtension {

	/**
	 * Get a auth constraint for object, action and idConstraint
	 * 
	 * @param object
	 * @param action
	 * @param idConstraint
	 * @return
	 */
	public Object getAuthConstraint(String object, String action, String idConstraint);

	/**
	 * Add one constraint for authorization
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Constraint {

		String id();

		Class<? extends IConstraint> type();

	}

	/**
	 * Add multi constraints for authorization
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Constraints {

		Constraint[] value();

	}

}
