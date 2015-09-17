package com.synaptix.mybatis.helper;

import java.util.Map;

import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.service.filter.RootNode;

/**
 * A hint process which builds an hint for pagination requests
 *
 * @author NicolasP
 */
public interface HintProcess {

	public String buildHint(ComponentDescriptor componentDescriptor, Map<String, Object> valueFilterMap);

	public String buildHint(ComponentDescriptor componentDescriptor, RootNode rootNode);

}
