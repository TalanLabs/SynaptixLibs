package service.impl;

import auth.MyAuthsBundleMethods;
import service.IGabyService;

public class GabyServerService implements IGabyService {

	@Override
	@Precondition(AuthPrecondition.class)
	@AuthPrecondition.Auth(MyAuthsBundleMethods.hasTestReadString)
	public String whatYourName() {
		return "Gaby";
	}
}
