package auth;

import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.annotation.SynaptixAuth;

@SynaptixAuth
public interface MyAuthsBundle extends AuthsBundle {

	@Key(object = "Test", action = "READABLE")
	public boolean hasTestRead();

}
