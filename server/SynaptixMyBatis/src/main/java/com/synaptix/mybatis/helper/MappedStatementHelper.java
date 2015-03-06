package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MappedStatementHelper {

	public static String buildMappedStatementKey(Class<?> clazz, Set<String> columns, String prefix) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null && !prefix.isEmpty()) {
			sb.append(prefix).append("-");
		}
		if (clazz != null) {
			sb.append(clazz.getName());
		}
		if (columns != null && !columns.isEmpty()) {
			List<String> cs = new ArrayList<String>(columns);
			Collections.sort(cs);
			sb.append("?");
			for (String column : columns) {
				sb.append(column).append("&");
			}
		}
		sb.append("-MappedStatement");
		return sb.toString();
	}

}
