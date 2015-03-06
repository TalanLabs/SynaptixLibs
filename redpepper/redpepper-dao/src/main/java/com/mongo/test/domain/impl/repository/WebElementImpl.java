package com.mongo.test.domain.impl.repository;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.mongo.test.domain.impl.common.BasicEntityBean;
import com.synaptix.redpepper.web.annotations.gwt.shared.AutoWebType;

@Entity(value = "reprository.elements", noClassnameStored = true)
public class WebElementImpl extends BasicEntityBean {
	AutoWebType type;
}
