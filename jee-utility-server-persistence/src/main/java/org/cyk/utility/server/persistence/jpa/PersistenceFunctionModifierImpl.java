package org.cyk.utility.server.persistence.jpa;

import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.server.persistence.AbstractPersistenceFunctionModifierImpl;
import org.cyk.utility.server.persistence.PersistenceFunctionModifier;
import org.cyk.utility.server.persistence.query.PersistenceQuery;
import org.cyk.utility.system.action.SystemAction;

@Dependent
public class PersistenceFunctionModifierImpl extends AbstractPersistenceFunctionModifierImpl implements PersistenceFunctionModifier {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void __executeQuery__(SystemAction action) {
		__inject__(EntityManager.class).merge(getEntity());		
	}
	
	@Override
	protected void __execute__(Collection<Object> entities,Integer batchSize) {
		EntityManager entityManager = __getEntityManager__();
		Integer count = 0;
		for(Object index : entities) {
			entityManager.merge(index);
			if(batchSize != null) {
				count++;
				if(count % batchSize == 0) {
					entityManager.flush();
					entityManager.clear();
				}
			}
		}
	}
	
	@Override
	protected void __executeQuery__(SystemAction action, PersistenceQuery persistenceQuery) {
		EntityManager entityManager = __inject__(EntityManager.class);
		//Instantiate query
		Query query = entityManager.createNamedQuery(persistenceQuery.getIdentifier().toString());
		
		//Set query parameters
		Properties parameters = getQueryParameters();
		if(parameters != null && parameters.__getMap__()!=null)
			for(Map.Entry<Object, Object> entry : parameters.__getMap__().entrySet())
				query.setParameter(entry.getKey().toString(), entry.getValue());
		
		//Execute query statement
		Integer count = query.executeUpdate();
		
		getProperties().setCount(count);
		
		//This is required when doing batch processing
		entityManager.clear();
	}
	
	private EntityManager __getEntityManager__() {
		EntityManager entityManager = (EntityManager) getProperty(Properties.ENTITY_MANAGER);
		if(entityManager == null)
			entityManager = __inject__(EntityManager.class);
		return entityManager;
	}
}
