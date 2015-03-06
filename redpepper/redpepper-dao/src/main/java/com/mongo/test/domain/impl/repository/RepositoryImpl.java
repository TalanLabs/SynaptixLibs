package com.mongo.test.domain.impl.repository;

import java.util.List;

import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.mongo.test.domain.impl.common.BasicEntityBean;

@Entity(value = "repositories", noClassnameStored = true)
public class RepositoryImpl extends BasicEntityBean {

	String type;

	Key<RepositoryImpl> parent;

	@Reference
	List<RepositoryImpl> subContainers;

	@Reference
	List<WebElementImpl> webElements;
}
