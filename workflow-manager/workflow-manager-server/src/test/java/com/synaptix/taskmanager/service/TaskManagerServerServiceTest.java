package com.synaptix.taskmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.StatusGraphBuilder;

public class TaskManagerServerServiceTest {

	@Test
	public void testGetStatusesPath() throws Exception {
		List<IStatusGraph> statusGraphs = new ArrayList<IStatusGraph>();
		statusGraphs.add(new StatusGraphBuilder().currentStatus("VAL").nextStatus("TIC").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("TIC").nextStatus("FAI").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("TIC").nextStatus("CUR").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("CUR").nextStatus("FAI").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("CUR").nextStatus("EXE").build());
		statusGraphs.add(new StatusGraphBuilder().currentStatus("EXE").nextStatus("CLO").build());

		TaskManagerServerService taskManagerServerService = new TaskManagerServerService();
		String result = taskManagerServerService.getStatusesPaths(statusGraphs, "TIC", "FAI");
		Assert.assertEquals("FAI",result);

		result = taskManagerServerService.getStatusesPaths(statusGraphs, "TIC", "CLO");
		Assert.assertEquals("CUR EXE CLO",result);

		result = taskManagerServerService.getStatusesPaths(statusGraphs, "VAL", "CLO");
		Assert.assertEquals("TIC CUR EXE CLO",result);

		result = taskManagerServerService.getStatusesPaths(statusGraphs, "VAL", "FAI");
		Assert.assertEquals("TIC FAI",result);

		result = taskManagerServerService.getStatusesPaths(statusGraphs, "VAL", "EXE");
		Assert.assertEquals("TIC CUR EXE",result);

		result = taskManagerServerService.getStatusesPaths(statusGraphs, "CLO", "EXE");
		Assert.assertEquals("",result);

		result = taskManagerServerService.getStatusesPaths(statusGraphs, "FAI", "FAI");
		Assert.assertEquals("",result);
	}
}