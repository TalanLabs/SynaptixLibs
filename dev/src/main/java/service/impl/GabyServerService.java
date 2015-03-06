package service.impl;

import service.IGabyService;
import auth.MyAuthsBundleMethods;

public class GabyServerService implements IGabyService {

	@Override
	@Precondition(AuthPrecondition.class)
	@AuthPrecondition.Auth(MyAuthsBundleMethods.hasTestReadString)
	public String whatYourName() {
		return "Gaby";
	}
}
