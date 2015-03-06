package com.synaptix.deployer.client.model;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.deployer.model.ITableColumn;

@SynaptixComponent
public interface IAnalyzedTableColumn extends ITableColumn {

	public enum MovementType {

		/**
		 * 
		 */
		CREATION,
		/**
		 * 
		 */
		SUPPRESSION,
		/**
		 * 
		 */
		EDIT;

	}

	public MovementType getMovementType();

	public void setMovementType(MovementType movementType);

}
