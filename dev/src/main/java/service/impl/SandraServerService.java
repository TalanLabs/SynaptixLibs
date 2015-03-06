package service.impl;

import service.ISandraService;

public class SandraServerService implements ISandraService {

	@Override
	public String whatYourName() {
		return "Sandra";
	}

	@Override
	public String laureline() {
		return "Laureline";
	}
}
