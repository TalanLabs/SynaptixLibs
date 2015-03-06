package service.impl;

import java.lang.reflect.Method;

public interface IPrecondition {

	public boolean process(Method method, Object[] args) throws Throwable;

}
