package org.cyk.utility.server.persistence.test.arquillian;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.clazz.ClassHelper;
import org.cyk.utility.field.FieldName;
import org.cyk.utility.field.FieldValueGetter;
import org.cyk.utility.server.persistence.Persistence;
import org.cyk.utility.server.persistence.PersistenceEntity;
import org.cyk.utility.server.persistence.PersistenceServiceProvider;
import org.cyk.utility.test.arquillian.AbstractArquillianTest;
import org.cyk.utility.throwable.ThrowableHelper;
import org.cyk.utility.value.ValueUsageType;

public abstract class AbstractArquillianIntegrationTest extends AbstractArquillianTest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject protected UserTransaction userTransaction;
	
	public <OBJECT> void create(PersistenceEntity<OBJECT> persistence,OBJECT object) throws Exception{
		userTransaction.begin();
		persistence.create(object);
		userTransaction.commit();
		assertThat(__inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.SYSTEM).getOutput()).isNotNull();
		assertionHelper.assertStartsWithLastLogEventMessage("Server Persistence Create "+object.getClass().getSimpleName())
			.assertContainsLastLogEventMessage("code="+__inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.BUSINESS).getOutput());
	}
	
	public <OBJECT> OBJECT read(PersistenceEntity<OBJECT> persistence,Object identifier,ValueUsageType valueUsageType,Properties expectedFieldValues) throws Exception{
		OBJECT object = persistence.readOne(identifier,valueUsageType);
		assertThat(object).isNotNull();
		assertionHelper.assertEqualsByFieldValue(expectedFieldValues, object);
		Object businessIdentifier = ValueUsageType.BUSINESS.equals(valueUsageType) ? identifier : __inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.BUSINESS).getOutput();
		assertionHelper.assertStartsWithLastLogEventMessage("Server Persistence Read "+object.getClass().getSimpleName())
		.assertContainsLastLogEventMessage("code="+businessIdentifier);
		return object;
	}
	
	public <OBJECT> OBJECT read(PersistenceEntity<OBJECT> persistence,Object identifier,ValueUsageType valueUsageType) throws Exception{
		return read(persistence, identifier,valueUsageType, null);
	}
	
	public <OBJECT> void update(PersistenceEntity<OBJECT> persistence,OBJECT object) throws Exception{
		userTransaction.begin();
		persistence.update(object);
		userTransaction.commit();
		assertThat(__inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.SYSTEM).getOutput()).isNotNull();
		assertionHelper.assertStartsWithLastLogEventMessage("Server Persistence Update "+object.getClass().getSimpleName())
			.assertContainsLastLogEventMessage("code="+__inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.BUSINESS).getOutput());
	}
	
	public <OBJECT> void delete(PersistenceEntity<OBJECT> persistence,OBJECT object) throws Exception{
		userTransaction.begin();
		persistence.delete(object);
		userTransaction.commit();
		Object systemIdentifier = __inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.SYSTEM).getOutput();
		assertionHelper.assertStartsWithLastLogEventMessage("Server Persistence Delete "+object.getClass().getSimpleName()).assertContainsLastLogEventMessage(
				"code="+__inject__(FieldValueGetter.class).execute(object,FieldName.IDENTIFIER,ValueUsageType.BUSINESS).getOutput());
		object = persistence.readOne(systemIdentifier);
		assertThat(object).isNull();
	}
	
	/*
	public void delete() throws Exception{
		NestedSet entity = new NestedSet().setCode("mc001");
		userTransaction.begin();
		persistence.create(entity);
		userTransaction.commit();
		entity = persistence.readOne(entity.getIdentifier());
		assertThat(entity).isNotNull();
		userTransaction.begin();
		persistence.delete(entity);
		userTransaction.commit();
		assertionHelper.assertStartsWithLastLogEventMessage("Server Persistence Delete NestedSet").assertContainsLastLogEventMessage("code=mc001");
		entity = persistence.readOne(entity.getIdentifier());
		assertThat(entity).isNull();
	}
	*/
	
	/**/
	
	
}
