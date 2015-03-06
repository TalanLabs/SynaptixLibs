package toto;

import java.util.ArrayList;
import java.util.List;

public class Builder {

	private List<String> propertyNames;

	public Builder() {
		super();
		propertyNames = new ArrayList<String>();
	}

	public Builder addProperty(String propertyName) {
		propertyNames.add(propertyName);
		return this;
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String propertyName : propertyNames) {
			if (first) {
				first = false;
			} else {
				sb.append(".");
			}
			sb.append(propertyName);
		}
		return sb.toString();
	}
}
