package auth;

import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.extension.IRootAuthExtension;
import com.synaptix.auth.extension.IWithLookupExtension;

public interface MyAuthsBundle extends AuthsBundle, IConstraintAuthExtension, IRootAuthExtension, IWithLookupExtension {

	@Key(object = "TEST", action = "READ")
	public boolean hasReadTest();

	@Key(object = "TEST", action = "WRITE")
	@Description("Permet d'ecrire un test")
	public boolean hasWriteTest();

	@Key(object = "TEST2", action = "READ")
	@Constraint(id = "toto", type = StringConstraint.class)
	public boolean hasReadTest2();

	@Key(object = "TEST3", action = "READ")
	@Constraints(@Constraint(id = "toto", type = StringConstraint.class))
	public boolean hasReadTest3();

}
