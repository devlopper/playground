package org.cyk.utility.system.action;

import org.cyk.utility.__kernel__.object.dynamic.Objectable;
import org.cyk.utility.object.Objects;
import org.cyk.utility.value.ValueUsageType;

public interface SystemAction extends Objectable  {

	Objects getEntities();
	Objects getEntities(Boolean injectIfNull);
	SystemAction setEntities(Objects entities);
	SystemAction setEntityClass(Class<?> entityClass);
	Class<?> getEntityClass();
	
	Objects getEntitiesIdentifiers();
	Objects getEntitiesIdentifiers(Boolean injectIfNull);
	SystemAction setEntitiesIdentifiers(Objects entitiesIdentifiers);
	SystemAction setEntityIdentifierClass(Class<?> entityIdentifierClass);
	Class<?> getEntityIdentifierClass();
	
	ValueUsageType getEntityIdentifierValueUsageType();
	SystemAction setEntityIdentifierValueUsageType(ValueUsageType entityIdentifierValueUsageType);
	
	Boolean getIsBatchProcessing();
	SystemAction setIsBatchProcessing(Boolean isBatchProcessing);
	/*
	Object getSubActionIdentifier();
	SystemAction setSubActionIdentifier(Object subActionIdentifier);
	*/
}
