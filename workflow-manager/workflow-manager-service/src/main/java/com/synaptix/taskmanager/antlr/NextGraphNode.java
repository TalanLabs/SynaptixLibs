package com.synaptix.taskmanager.antlr;

public class NextGraphNode extends AbstractGraphNode {

	private final AbstractGraphNode firstNode;

	private final AbstractGraphNode nextNode;

	public NextGraphNode(AbstractGraphNode firstNode, AbstractGraphNode nextNode) {
		super();
		this.firstNode = firstNode;
		this.nextNode = nextNode;
	}

	public AbstractGraphNode getFirstNode() {
		return firstNode;
	}

	public AbstractGraphNode getNextNode() {
		return nextNode;
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(firstNode).append("=>").append(nextNode).append(")").toString();
	}
}
