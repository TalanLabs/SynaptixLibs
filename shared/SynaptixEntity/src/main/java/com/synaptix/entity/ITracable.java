package com.synaptix.entity;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.entity.extension.JdbcTypesEnum;

@SynaptixComponent
public interface ITracable extends IComponent, IDatabaseComponentExtension {

	Comparator<ITracable> CREATED_DATE_COMPARATOR = new Comparator<ITracable>() {

		@Override
		public int compare(ITracable o1, ITracable o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}
			return o1.getCreatedDate().compareTo(o2.getCreatedDate());
		}
	};

	@Column(name = "CREATED_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	Date getCreatedDate();

	void setCreatedDate(Date createdDate);

	@Column(name = "CREATED_BY", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getCreatedBy();

	void setCreatedBy(String createdBy);

	@Column(name = "UPDATED_DATE")
	@JdbcType(JdbcTypesEnum.TIMESTAMP)
	Date getUpdatedDate();

	void setUpdatedDate(Date updatedDate);

	@Column(name = "UPDATED_BY", length = 240)
	@JdbcType(JdbcTypesEnum.VARCHAR)
	String getUpdatedBy();

	void setUpdatedBy(String updatedBy);

}
