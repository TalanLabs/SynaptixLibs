package com.synaptix.auth.helper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.synaptix.auth.AuthsBundleManager;

public class DefaultOrAuth implements IAuth {

	private final List<IAuth> auths;

	public DefaultOrAuth(IAuth... auths) {
		super();
		this.auths = Arrays.asList(auths);
	}

	@Override
	public boolean hasAuth(AuthsBundleManager authsBundleManager) {
		if (auths != null && !auths.isEmpty()) {
			boolean res = false;
			Iterator<IAuth> it = auths.iterator();
			while (it.hasNext() && !res) {
				IAuth auth = it.next();
				res = auth.hasAuth(authsBundleManager);
			}
			return res;
		}
		return true;
	}
}
