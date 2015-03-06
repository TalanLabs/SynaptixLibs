package com.synaptix.deployer.client.view.swing.helper;

import java.util.List;

import javax.swing.JComboBox;

import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.swing.widget.TypeGenericCellRenderer;

public class DBHelper {

	private static final GenericObjectToString<ISynaptixDatabaseSchema> os = new GenericObjectToString<ISynaptixDatabaseSchema>() {

		@Override
		public String getString(ISynaptixDatabaseSchema t) {
			return String.format("%s [%s]", t.getTablespaceName(), t.getEnvironment());
		}
	};

	public static JComboBox buildDBComboBox(List<ISynaptixDatabaseSchema> dbList) {
		TypeGenericCellRenderer<ISynaptixDatabaseSchema> renderer = new TypeGenericCellRenderer<ISynaptixDatabaseSchema>(os);
		ISynaptixDatabaseSchema[] dbs = dbList.toArray(new ISynaptixDatabaseSchema[dbList.size()]);
		JComboBox databaseCombo = new JComboBox(dbs);
		databaseCombo.setRenderer(renderer);
		return databaseCombo;
	}
}
