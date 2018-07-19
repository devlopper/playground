package org.cyk.utility.server.business;

import java.io.Serializable;
import java.util.Collection;

import javax.transaction.Transactional;

import org.cyk.utility.__kernel__.function.Function;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.system.action.SystemAction;

public abstract class AbstractBusinessFunctionTransactionImpl extends AbstractBusinessFunctionImpl implements BusinessFunctionTransaction, Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override @Transactional
	public Function<Properties, Void> execute() {
		return super.execute();
	}
	
	@Override
	public BusinessFunctionTransaction setAction(SystemAction action) {
		return (BusinessFunctionTransaction) super.setAction(action);
	}
	
	@Override
	public BusinessFunctionTransaction setEntity(Object entity) {
		return (BusinessFunctionTransaction) super.setEntity(entity);
	}
	
	@Override
	public BusinessFunctionTransaction setEntities(Collection<?> entities) {
		return (BusinessFunctionTransaction) super.setEntities(entities);
	}
}
