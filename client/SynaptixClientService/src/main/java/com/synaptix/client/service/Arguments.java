package com.synaptix.client.service;

public final class Arguments {

	private Object[] args;
	
	public Arguments() {
		
	}
	
	public Arguments(final Object[] args) { 
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(final Object[] args) {
		this.args = args;
	}
	
	private boolean equalsNull(
			final Object one,
			final Object two
	) {
		if(one != null) {
			if(two != null) {
				return one.equals(two);
			}
			return false;
		}
		else {
			if(two != null) {
				return false;
			}
			return true;
		}
	}
	
	public boolean equals(final Object obj) {
		if(obj instanceof Arguments) {
			final Arguments argument = (Arguments) obj;
			if(getArgs() != null) {
				if(argument.getArgs() != null) {
					if(getArgs().length == argument.getArgs().length) {
						final int length = getArgs().length;
						for(int index = 0; index < length ; ++index) {
							if(!equalsNull(getArgs()[index], argument.getArgs()[index])) {
								return false;
							}
						}
						return true;
					}
				}
				return false;
			}
			else {
				if(argument.getArgs() != null) {
					return false;
				}
				return true;
			}
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		int ret = 0;
		if(getArgs() != null) {
			for(Object obj : getArgs()) {
				if(obj != null) {
					ret += obj.hashCode();
				}
			}
		}
		return ret;
	}
}
