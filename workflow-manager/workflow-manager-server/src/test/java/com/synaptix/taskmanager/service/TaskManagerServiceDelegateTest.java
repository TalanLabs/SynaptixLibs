package com.synaptix.taskmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.delegate.TaskManagerServiceDelegate;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.StatusGraphBuilder;

public class TaskManagerServiceDelegateTest {

	@Test
	public void testGetStatusesPath() throws Exception {
		List<IStatusGraph> statusGraphs = new ArrayList<IStatusGraph>();
		statusGraphs.add(new StatusGraphBuilder().currentStatus("VAL").nextStatus("TIC").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("TIC").nextStatus("FAI").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("TIC").nextStatus("CUR").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("CUR").nextStatus("FAI").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("CUR").nextStatus("EXE").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("EXE").nextStatus("CLO").build());

		TaskManagerServiceDelegate taskManagerServiceDelegate = new TaskManagerServiceDelegate();
		String result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "TIC", "FAI");
		Assert.assertEquals("TIC FAI",result);

		result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "TIC", "CLO");
		Assert.assertEquals("TIC CUR EXE CLO",result);

		result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "VAL", "CLO");
		Assert.assertEquals("VAL TIC CUR EXE CLO",result);

		result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "VAL", "FAI");
		Assert.assertEquals("VAL TIC FAI",result);

		result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "VAL", "EXE");
		Assert.assertEquals("VAL TIC CUR EXE",result);

		result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "CLO", "EXE");
		Assert.assertEquals("",result);

		result = taskManagerServiceDelegate.getStatusesPaths(statusGraphs, "FAI", "FAI");
		Assert.assertEquals("FAI",result);

	}
}