package com.mongo.test.domain.impl.common;

import java.util.List;

import com.github.jmkgreen.morphia.annotations.Reference;
import com.mongo.test.domain.def.ITaggable;

public abstract class BasicTaggableMongoBean extends BasicMongoBean implements ITaggable {

	@Reference
	List<TagImpl> tags;

	public List<TagImpl> getTags() {
		return tags;
	}

	public void setTags(List<TagImpl> tags) {
		this.tags = tags;
	}
}
