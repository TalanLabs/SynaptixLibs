package com.synaptix.auth.helper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.synaptix.auth.AuthsBundleManager;

public class DefaultAndAuth implements IAuth {

	private final List<IAuth> auths;

	public DefaultAndAuth(IAuth... auths) {
		super();
		this.auths = Arrays.asList(auths);
	}

	@Override
	public boolean hasAuth(AuthsBundleManager authsBundleManager) {
		if (auths != null && !auths.isEmpty()) {
			boolean res = true;
			Iterator<IAuth> it = auths.iterator();
			while (it.hasNext() && res) {
				IAuth auth = it.next();
				res = auth.hasAuth(authsBundleManager);
			}
			return res;
		}
		return false;
	}

}
