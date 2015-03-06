package com.synaptix.deployer.instructions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.synaptix.deployer.environment.IEnvironmentInstance;

public final class SynaptixInstructions {

	public Set<IEnvironmentInstance> stopInstanceSet;

	public Set<IEnvironmentInstance> renameInstanceSet;

	public Set<IEnvironmentInstance> downloadInstanceSet;

	public Set<IEnvironmentInstance> launchInstanceSet;

	private boolean playScripts;

	public SynaptixInstructions() {
		super();

		stopInstanceSet = new HashSet<IEnvironmentInstance>();
		renameInstanceSet = new HashSet<IEnvironmentInstance>();
		downloadInstanceSet = new HashSet<IEnvironmentInstance>();
		launchInstanceSet = new HashSet<IEnvironmentInstance>();
	}

	public void addStopInstances(Set<IEnvironmentInstance> instances) {
		stopInstanceSet.addAll(instances);
	}

	public void addRenameInstances(Set<IEnvironmentInstance> instances) {
		renameInstanceSet.addAll(instances);
	}

	public void addDownloadInstances(Set<IEnvironmentInstance> instances) {
		downloadInstanceSet.addAll(instances);
	}

	public void addLaunchInstances(Set<IEnvironmentInstance> instances) {
		launchInstanceSet.addAll(instances);
	}

	public Set<IEnvironmentInstance> getStopInstanceSet() {
		return Collections.unmodifiableSet(stopInstanceSet);
	}

	public Set<IEnvironmentInstance> getRenameInstanceSet() {
		return Collections.unmodifiableSet(renameInstanceSet);
	}

	public Set<IEnvironmentInstance> getDownloadInstanceSet() {
		return Collections.unmodifiableSet(downloadInstanceSet);
	}

	public Set<IEnvironmentInstance> getLaunchInstanceSet() {
		return Collections.unmodifiableSet(launchInstanceSet);
	}

	public void playScripts(boolean playScripts) {
		this.playScripts = playScripts;
	}

	public boolean isPlayScripts() {
		return playScripts;
	}
}
