package com.synaptix.client.common.message;

import com.synaptix.auth.AuthsBundle;

public interface CommonAuthsBundle extends AuthsBundle {

	@Key(object = "nlsMessages", action = "write")
	@Description("Import and export nls messages")
	public boolean hasWriteNlsMessages();

	@Key(object = "debugActions", action = "write")
	@Description("Show debug and test buttons")
	public boolean hasDebugActions();
}
