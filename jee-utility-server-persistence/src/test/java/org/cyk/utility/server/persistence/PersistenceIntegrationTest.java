package org.cyk.utility.server.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.computation.ArithmeticOperator;
import org.cyk.utility.__kernel__.computation.SortOrder;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.field.FieldHelper;
import org.cyk.utility.field.FieldInstancesRuntime;
import org.cyk.utility.mapping.MappingHelper;
import org.cyk.utility.server.persistence.api.MyEntityPersistence;
import org.cyk.utility.server.persistence.api.NodeHierarchyPersistence;
import org.cyk.utility.server.persistence.api.NodePersistence;
import org.cyk.utility.server.persistence.entities.MyEntity;
import org.cyk.utility.server.persistence.entities.Node;
import org.cyk.utility.server.persistence.entities.NodeHierarchy;
import org.cyk.utility.server.persistence.query.PersistenceQuery;
import org.cyk.utility.server.persistence.query.PersistenceQueryRepository;
import org.cyk.utility.server.persistence.query.filter.Field;
import org.cyk.utility.server.persistence.query.filter.FieldDto;
import org.cyk.utility.server.persistence.query.filter.Filter;
import org.cyk.utility.server.persistence.query.filter.FilterDto;
import org.cyk.utility.server.persistence.test.TestPersistenceCreate;
import org.cyk.utility.server.persistence.test.arquillian.AbstractPersistenceArquillianIntegrationTestWithDefaultDeployment;
import org.cyk.utility.sql.builder.Attribute;
import org.cyk.utility.sql.builder.Tuple;
import org.cyk.utility.throwable.ThrowableHelper;
import org.cyk.utility.value.ValueDto;
import org.cyk.utility.value.ValueUsageType;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

public class PersistenceIntegrationTest extends AbstractPersistenceArquillianIntegrationTestWithDefaultDeployment {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __listenBefore__() {
		super.__listenBefore__();
		DependencyInjection.setQualifierClassTo(org.cyk.utility.__kernel__.annotation.Test.Class.class, PersistableClassesGetter.class);
		__inject__(ApplicationScopeLifeCycleListenerEntities.class).initialize(null);
	}
	
	@Override
	protected void __listenBeforeCallCountIsZero__() throws Exception {
		super.__listenBeforeCallCountIsZero__();
		__inject__(PersistenceQueryRepository.class).add(new PersistenceQuery().setIdentifier("MyEntity.readAll").setValue("SELECT r FROM MyEntity r")
				.setResultClass(MyEntity.class));
		__inject__(MyEntityPersistence.class).read();//to trigger initialisation
	}
	
	@Test
	public void buildQueryIdentifierStringFromName(){
		Assert.assertEquals("MyEntity.readByValue", __inject__(PersistenceQueryIdentifierStringBuilder.class)
				.setClassSimpleName("MyEntity").setName("readByValue").execute().getOutput());
	}
	
	@Test
	public void buildQueryIdentifierStringFromDerivedFromQueryIdentifier(){
		Assert.assertEquals("MyEntity.countByValue", __inject__(PersistenceQueryIdentifierStringBuilder.class)
				.setIsDerivedFromQueryIdentifier(Boolean.TRUE).setDerivedFromQueryIdentifier("MyEntity.readByValue")
				.setIsCountInstances(Boolean.TRUE).execute().getOutput());
	}
	
	@Test
	public void executeQueryReadMyEntityAllFromRepository() throws Exception{		
		userTransaction.begin();
		__inject__(PersistenceFunctionCreator.class).setEntity(new MyEntity().setCode("mc001")).execute();
		__inject__(PersistenceFunctionCreator.class).setEntity(new MyEntity().setCode("mc002")).execute();
		__inject__(PersistenceFunctionCreator.class).setEntity(new MyEntity().setCode("mc003")).execute();
		userTransaction.commit();
		
		@SuppressWarnings("unchecked")
		List<MyEntity> results = (List<MyEntity>) __inject__(PersistenceFunctionReader.class).setEntityClass(MyEntity.class).setQueryIdentifier("MyEntity.read")
				.execute().getProperties().getEntities();
		
		Assert.assertEquals(3, results.size());
		//System.out.println(results);
		/*
		myEntity = (MyEntity) __inject__(PersistenceFunctionReader.class).setEntityClass(MyEntity.class)
				.setEntityIdentifier(myEntity.getIdentifier()).execute().getProperties().getEntity();
		
		assertThat(myEntity).isNotNull();
		assertThat(myEntity.getIdentifier()).isNotNull();
		assertThat(myEntity.getCode()).isEqualTo("mc001");
		assertionHelper.assertStartsWithLastLogEventMessage("Server Persistence Read MyEntity")
			.assertContainsLastLogEventMessage("identifier="+myEntity.getIdentifier()).assertContainsLastLogEventMessage("code=mc001");
		*/
	}
	
	/* Create */
	
	@Test
	public void create_myEntity_one() throws Exception{
		String code1 = __getRandomCode__();
		MyEntity myEntity = new MyEntity().setCode(code1);
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		myEntity = __inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS);
		assertionHelper.assertNotNull(myEntity);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByIdentifier(code1, ValueUsageType.BUSINESS);
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		myEntity = __inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS);
		assertionHelper.assertNull(myEntity);
	}
	
	@Test
	public void create_myEntity_many_sequential() throws Exception{
		String code1 = __getRandomCode__();
		String code2 = __getRandomCode__();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(new MyEntity().setCode(code1));
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(new MyEntity().setCode(code2));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code2, ValueUsageType.BUSINESS));
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByIdentifier(code1, ValueUsageType.BUSINESS);
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code2, ValueUsageType.BUSINESS));
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByIdentifier(code2, ValueUsageType.BUSINESS);
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(code2, ValueUsageType.BUSINESS));
	}
	
	@Test
	public void create_myEntity_many_simultanous() throws Exception{
		String code1 = __getRandomCode__();
		String code2 = __getRandomCode__();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(new MyEntity().setCode(code1),new MyEntity().setCode(code2)));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code2, ValueUsageType.BUSINESS));
		
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByIdentifier(code1, ValueUsageType.BUSINESS);
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(code2, ValueUsageType.BUSINESS));
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByIdentifier(code2, ValueUsageType.BUSINESS);
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(code1, ValueUsageType.BUSINESS));
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(code2, ValueUsageType.BUSINESS));
	}
	
	/* Read */
	
	@Test
	public void read_myEntity_one_by_identifier_system() throws Exception{
		MyEntity myEntity = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(myEntity.getIdentifier(), ValueUsageType.SYSTEM));
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(myEntity.getIdentifier(), ValueUsageType.BUSINESS));
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).delete(myEntity);
		userTransaction.commit();
	}
	
	
	@Test
	public void read_myEntity_one_by_identifier_business() throws Exception{
		String code = __getRandomCode__();
		MyEntity myEntity = new MyEntity().setCode(code);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertNull(__inject__(MyEntityPersistence.class).readByIdentifier(myEntity.getCode() ,ValueUsageType.SYSTEM));
		assertionHelper.assertNotNull(__inject__(MyEntityPersistence.class).readByIdentifier(myEntity.getCode(), ValueUsageType.BUSINESS));
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).delete(myEntity);
		userTransaction.commit();
	}
	
	
	@Test
	public void read_myEntity_many_by_identifier_system() throws Exception{
		String code1 = __getRandomCode__(); 
		String code2 = __getRandomCode__();
		String code3 = __getRandomCode__();
		String id1 = __getRandomIdentifier__(); 
		String id2 = __getRandomIdentifier__();
		String id3 = __getRandomIdentifier__();
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(new MyEntity().setIdentifier(id1).setCode(code1),new MyEntity().setIdentifier(id2).setCode(code2)
				,new MyEntity().setIdentifier(id3).setCode(code3)));
		userTransaction.commit();
		Collection<Object> identifiers = __inject__(MyEntityPersistence.class).readSystemIdentifiers();
		assertThat(identifiers).containsOnly(id1,id2,id3);
		assertThat(__inject__(MyEntityPersistence.class).readBySystemIdentifiers(identifiers).stream().map(MyEntity::getCode).collect(Collectors.toList()))
			.containsOnly(code1,code2,code3);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteBySystemIdentifiers(Arrays.asList(id1,id2,id3));
		userTransaction.commit();
	}
	
	@Test
	public void read_myEntity_many_by_identifier_business() throws Exception{
		String code1 = __getRandomCode__(); 
		String code2 = __getRandomCode__();
		String code3 = __getRandomCode__();
		String id1 = __getRandomIdentifier__(); 
		String id2 = __getRandomIdentifier__();
		String id3 = __getRandomIdentifier__();
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(new MyEntity().setIdentifier(id1).setCode(code1),new MyEntity().setIdentifier(id2).setCode(code2)
				,new MyEntity().setIdentifier(id3).setCode(code3)));
		userTransaction.commit();
		Collection<Object> identifiers = __inject__(MyEntityPersistence.class).readBusinessIdentifiers();
		assertThat(identifiers).containsOnly(code1,code2,code3);
		assertThat(__inject__(MyEntityPersistence.class).readByBusinessIdentifiers(identifiers).stream().map(MyEntity::getIdentifier).collect(Collectors.toList()))
			.containsOnly(id1,id2,id3);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByBusinessIdentifiers(Arrays.asList(code1,code2,code3));
		userTransaction.commit();
	}
	
	@Test
	public void read_myEntity_many_by_identifier_system_filter() throws Exception{
		String code1 = __getRandomCode__(); 
		String code2 = __getRandomCode__();
		String code3 = __getRandomCode__();
		String id1 = __getRandomIdentifier__(); 
		String id2 = __getRandomIdentifier__();
		String id3 = __getRandomIdentifier__();
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(new MyEntity().setIdentifier(id1).setCode(code1),new MyEntity().setIdentifier(id2).setCode(code2)
				,new MyEntity().setIdentifier(id3).setCode(code3)));
		userTransaction.commit();
		Collection<Object> identifiers = __inject__(MyEntityPersistence.class).read().stream().map(MyEntity::getIdentifier).collect(Collectors.toList());
		assertThat(identifiers).containsOnly(id1,id2,id3);
		Filter filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER,Arrays.asList(id1));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(id1);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, Arrays.asList(id2));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(id2);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, Arrays.asList(id3));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(id3);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, Arrays.asList(id1,id3));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(id1,id3);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, Arrays.asList());
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).isEmpty();
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, null);
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(id1,id2,id3);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getIdentifier).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(id1,id2,id3);
		
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteBySystemIdentifiers(Arrays.asList(id1,id2,id3));
		userTransaction.commit();
	}
	
	@Test
	public void read_myEntity_many_by_identifier_business_filter() throws Exception{
		String code1 = __getRandomCode__(); 
		String code2 = __getRandomCode__();
		String code3 = __getRandomCode__();
		String id1 = __getRandomIdentifier__(); 
		String id2 = __getRandomIdentifier__();
		String id3 = __getRandomIdentifier__();
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(new MyEntity().setIdentifier(id1).setCode(code1),new MyEntity().setIdentifier(id2).setCode(code2)
				,new MyEntity().setIdentifier(id3).setCode(code3)));
		userTransaction.commit();
		Collection<Object> identifiers = __inject__(MyEntityPersistence.class).read().stream().map(MyEntity::getCode).collect(Collectors.toList());
		assertThat(identifiers).containsOnly(code1,code2,code3);
		Filter filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, Arrays.asList(code1));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(code1);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, Arrays.asList(code2));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(code2);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, Arrays.asList(code3));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(code3);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, Arrays.asList(code1,code3));
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(code1,code3);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, Arrays.asList());
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).isEmpty();
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, null);
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(code1,code2,code3);
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		identifiers = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters)).stream().map(MyEntity::getCode).collect(Collectors.toList());		
		assertThat(identifiers).containsOnly(code1,code2,code3);
		
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByBusinessIdentifiers(Arrays.asList(code1,code2,code3));
		userTransaction.commit();
	}
	
	@Test
	public void read_myEntity_by_identifier_system_contains() throws Exception{
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(
				new MyEntity().setIdentifier("123").setCode(__getRandomCode__()).setIntegerValue(1)
				,new MyEntity().setIdentifier("133").setCode(__getRandomCode__()).setIntegerValue(2)
				,new MyEntity().setIdentifier("144").setCode(__getRandomCode__()).setIntegerValue(1)
				,new MyEntity().setIdentifier("150").setCode(__getRandomCode__()).setIntegerValue(2)
				,new MyEntity().setIdentifier("623").setCode(__getRandomCode__()).setIntegerValue(2)
				));
		userTransaction.commit();
		
		Filter filters = null;
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, "123");
		Collection<MyEntity> entities = null;
		try {
			entities = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		org.assertj.core.api.Assertions.assertThat(entities).isNotEmpty();
		org.assertj.core.api.Assertions.assertThat(entities.stream().map(MyEntity::getIdentifier)).containsExactly("123");
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, "23");
		entities = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters));
		org.assertj.core.api.Assertions.assertThat(entities).isNotEmpty();
		org.assertj.core.api.Assertions.assertThat(entities.stream().map(MyEntity::getIdentifier)).containsExactly("123","623");
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_IDENTIFIER, "3");
		entities = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters));
		org.assertj.core.api.Assertions.assertThat(entities).isNotEmpty();
		org.assertj.core.api.Assertions.assertThat(entities.stream().map(MyEntity::getIdentifier)).containsExactly("123","133","623");
	}
	
	@Test
	public void read_myEntity_by_identifier_business_contains() throws Exception{
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(
				new MyEntity().setCode("123").setIntegerValue(1)
				,new MyEntity().setCode("133").setIntegerValue(2)
				,new MyEntity().setCode("144").setIntegerValue(1)
				,new MyEntity().setCode("150").setIntegerValue(2)
				,new MyEntity().setCode("623").setIntegerValue(2)
				));
		userTransaction.commit();
		
		Filter filters = null;
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, "123");
		Collection<MyEntity> entities = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters));
		org.assertj.core.api.Assertions.assertThat(entities).isNotEmpty();
		org.assertj.core.api.Assertions.assertThat(entities.stream().map(MyEntity::getCode)).containsExactly("123");
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, "23");
		entities = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters));
		org.assertj.core.api.Assertions.assertThat(entities).isNotEmpty();
		org.assertj.core.api.Assertions.assertThat(entities.stream().map(MyEntity::getCode)).containsExactly("123","623");
		
		filters = __inject__(Filter.class).setKlass(MyEntity.class);
		filters.addField(MyEntity.FIELD_CODE, "3");
		entities = __inject__(MyEntityPersistence.class).read(new Properties().setQueryFilters(filters));
		org.assertj.core.api.Assertions.assertThat(entities).isNotEmpty();
		org.assertj.core.api.Assertions.assertThat(entities.stream().map(MyEntity::getCode)).containsExactly("123","133","623");
	}
	
	/* Update */
	
	@Test
	public void update_myEntity_one() throws Exception{
		String code = __getRandomCode__();
		MyEntity myEntity = new MyEntity().setCode(code).setIntegerValue(123);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(123, __inject__(MyEntityPersistence.class).readByBusinessIdentifier(code).getIntegerValue());
		myEntity = __inject__(MyEntityPersistence.class).readByIdentifier(code, ValueUsageType.BUSINESS);
		myEntity.setIntegerValue(789);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).update(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(789, __inject__(MyEntityPersistence.class).readByBusinessIdentifier(code).getIntegerValue());		
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).delete(myEntity);
		userTransaction.commit();
	}
	
	@Test
	public void update_myEntity_many() throws Exception{
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__()).setIntegerValue(123);
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__()).setIntegerValue(456);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(123, __inject__(MyEntityPersistence.class).readByBusinessIdentifier(myEntity01.getCode()).getIntegerValue());
		assertionHelper.assertEquals(456, __inject__(MyEntityPersistence.class).readByBusinessIdentifier(myEntity02.getCode()).getIntegerValue());
		myEntity01 = __inject__(MyEntityPersistence.class).readByIdentifier(myEntity01.getCode(), ValueUsageType.BUSINESS);
		myEntity02 = __inject__(MyEntityPersistence.class).readByIdentifier(myEntity02.getCode(), ValueUsageType.BUSINESS);
		myEntity01.setIntegerValue(26);
		myEntity02.setIntegerValue(48);
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).updateMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(26, __inject__(MyEntityPersistence.class).readByBusinessIdentifier(myEntity01.getCode()).getIntegerValue());
		assertionHelper.assertEquals(48, __inject__(MyEntityPersistence.class).readByBusinessIdentifier(myEntity02.getCode()).getIntegerValue());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
	}
	
	/* Delete */
	
	@Test
	public void delete_myEntity_one() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).delete(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_many() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__());
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_one_by_identifier_system() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteBySystemIdentifier(myEntity.getIdentifier());
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_many_by_identifier_system() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__());
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteBySystemIdentifiers(Arrays.asList(myEntity01.getIdentifier(),myEntity02.getIdentifier()));
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_one_by_identifier_business() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).create(myEntity);
		userTransaction.commit();
		assertionHelper.assertEquals(1l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByBusinessIdentifier(myEntity.getCode());
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_many_by_identifier_business() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__());
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteByBusinessIdentifiers(Arrays.asList(myEntity01.getCode(),myEntity02.getCode()));
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_all_specific() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__());
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).deleteAll();
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_all_generic_by_class() throws Exception{
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__());
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(Persistence.class).deleteByEntityClass(MyEntity.class);
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	@Test
	public void delete_myEntity_all_generic() throws Exception{
		//DependencyInjection.setQualifierClass(EntityClassesGetter.class, org.cyk.utility.__kernel__.annotation.Test.Class.class);
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
		MyEntity myEntity01 = new MyEntity().setCode(__getRandomCode__());
		MyEntity myEntity02 = new MyEntity().setCode(__getRandomCode__());
		userTransaction.begin();
		__inject__(MyEntityPersistence.class).createMany(Arrays.asList(myEntity01,myEntity02));
		userTransaction.commit();
		assertionHelper.assertEquals(2l, __inject__(MyEntityPersistence.class).count());
		userTransaction.begin();
		__inject__(Persistence.class).deleteAll();
		userTransaction.commit();
		assertionHelper.assertEquals(0l, __inject__(MyEntityPersistence.class).count());
	}
	
	/* page */

	@Test
	public void readManyByPage() throws Exception{
		userTransaction.begin();
		for(Integer index = 0 ; index < 10 ; index = index + 1)
			__inject__(MyEntityPersistence.class).create(new MyEntity().setIdentifier(index.toString()).setCode(index.toString()));
		userTransaction.commit();
		
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read()))
			.containsExactly("0","1","2","3","4","5","6","7","8","9");
		
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(null)))
		.containsExactly("0","1","2","3","4","5","6","7","8","9");
		
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read()))
		.containsExactly("0","1","2","3","4","5","6","7","8","9");
		
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(null)))
		.containsExactly("0","1","2","3","4","5","6","7","8","9");
		
		Properties properties = new Properties();
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(properties)))
			.containsExactly("0","1","2","3","4","5","6","7","8","9");
		
		properties = new Properties();
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(properties)))
			.containsExactly("0","1","2","3","4","5","6","7","8","9");
		
		properties = new Properties();
		properties.setQueryFirstTupleIndex(0);
		properties.setQueryNumberOfTuple(1);
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(properties)))
			.containsExactly("0");
		
		properties = new Properties();
		properties.setQueryFirstTupleIndex(1);
		properties.setQueryNumberOfTuple(1);
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(properties)))
			.containsExactly("1");
		
		properties = new Properties();
		properties.setQueryFirstTupleIndex(0);
		properties.setQueryNumberOfTuple(3);
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(properties)))
			.containsExactly("0","1","2");
		
		properties = new Properties();
		properties.setQueryFirstTupleIndex(4);
		properties.setQueryNumberOfTuple(3);
		assertThat(__inject__(FieldHelper.class).getSystemIdentifiers(String.class, __inject__(MyEntityPersistence.class).read(properties)))
			.containsExactly("4","5","6");
	}
	
	/* query */
	
	@SuppressWarnings("unchecked")
	@Test
	public void readByIntegerValueUsingCustom(){
		__createEntity__(new MyEntity().setCode("ee01").setIntegerValue(1));
		__createEntity__(new MyEntity().setCode("ee02").setIntegerValue(2));
		__createEntity__(new MyEntity().setCode("ee03").setIntegerValue(1));
		__createEntity__(new MyEntity().setCode("ee04").setIntegerValue(2));
		__createEntity__(new MyEntity().setCode("ee05").setIntegerValue(2));
		
		String query = __inject__(MyEntityPersistence.class).instanciateReadByIntegerValueQueryStringBuilder()
				.orderBy("code")
				.execute().getOutput();

		Collection<MyEntity> c1 = (Collection<MyEntity>) __inject__(PersistenceFunctionReader.class).setEntityClass(MyEntity.class).setQueryValue(query)
				.setQueryParameters(new Properties().set("integerValue", 2)).execute().getEntities();
		Assert.assertEquals(3, c1.size());
		
		query = __inject__(MyEntityPersistence.class).instanciateReadByIntegerValueQueryStringBuilder()
				.orderBy(new Attribute().setName("code").setTuple(new Tuple().setName("MyEntity")).setSortOrder(SortOrder.DESCENDING))
				.execute().getOutput();
		Collection<MyEntity> c2 = (Collection<MyEntity>) __inject__(PersistenceFunctionReader.class).setEntityClass(MyEntity.class).setQueryValue(query)
				.setQueryParameters(new Properties().set("integerValue", 2)).execute().getEntities();
		Assert.assertEquals(3, c2.size());
		
		Collection<MyEntity> c3 = (Collection<MyEntity>) __inject__(MyEntityPersistence.class).read();
		Assert.assertEquals(5, c3.size());		
	}
	
	/* count */
	
	@Test
	public void count(){
		__createEntity__(new MyEntity().setIdentifier("123").setCode(__getRandomCode__()).setIntegerValue(1));
		__createEntity__(new MyEntity().setIdentifier("133").setCode(__getRandomCode__()).setIntegerValue(2));
		__createEntity__(new MyEntity().setIdentifier("144").setCode(__getRandomCode__()).setIntegerValue(1));
		__createEntity__(new MyEntity().setIdentifier("150").setCode(__getRandomCode__()).setIntegerValue(2));
		__createEntity__(new MyEntity().setIdentifier("623").setCode(__getRandomCode__()).setIntegerValue(2));
		
		Long count = __inject__(MyEntityPersistence.class).count();
		org.assertj.core.api.Assertions.assertThat(count).isEqualTo(5);
	}
	
	@Test
	public void countWithProperties(){
		for(Integer index = 0 ; index < 16 ; index = index + 1)
			__createEntity__(new MyEntity().setCode(__getRandomCode__()).setIntegerValue(1));
		
		Properties properties = new Properties();
		properties.setFilters(null).setIsQueryResultPaginated(Boolean.TRUE).setQueryFirstTupleIndex(5).setQueryNumberOfTuple(5).setQueryIdentifier(null);
		Long count = __inject__(MyEntityPersistence.class).count(properties);
		org.assertj.core.api.Assertions.assertThat(count).isEqualTo(16);
	}
	
	@Test
	public void readByIntegerValue(){
		__createEntity__(new MyEntity().setCode("e01").setIntegerValue(1));
		__createEntity__(new MyEntity().setCode("e02").setIntegerValue(2));
		__createEntity__(new MyEntity().setCode("e03").setIntegerValue(1));
		__createEntity__(new MyEntity().setCode("e04").setIntegerValue(2));
		__createEntity__(new MyEntity().setCode("e05").setIntegerValue(2));
		
		Collection<MyEntity> collection = ____inject____(MyEntityPersistence.class).readByIntegerValue(2);
		Assert.assertNotNull(collection);
		Assert.assertEquals(3, collection.size());
		Assert.assertEquals(new Long(3), ____inject____(MyEntityPersistence.class).countByIntegerValue(2));
	}
	
	@Test
	public void executeIncrementIntegerValue() throws Exception{
		__createEntity__(new MyEntity().setCode("e01A").setIntegerValue(10));
		__createEntity__(new MyEntity().setCode("e02B").setIntegerValue(20));
		__createEntity__(new MyEntity().setCode("e03C").setIntegerValue(10));
		__createEntity__(new MyEntity().setCode("e04D").setIntegerValue(20));
		__createEntity__(new MyEntity().setCode("e05E").setIntegerValue(20));
		userTransaction.begin();
		____inject____(MyEntityPersistence.class).executeIncrementIntegerValue(7);
		userTransaction.commit();	
		MyEntity myEntity = ____inject____(MyEntityPersistence.class).readByBusinessIdentifier("e02B");
		Assert.assertEquals(new Integer(27), myEntity.getIntegerValue());
	}
	
	/* graph */
	
	//@Test
	public void find_graph_myEntityFieldIntegerValueValueNotLoaded() throws Exception{
		EntityManager entityManager = __inject__(EntityManager.class);
		String identifier = __getRandomIdentifier__();
		MyEntity myEntity = new MyEntity().setIdentifier(identifier).setCode("mc001").setIntegerValue(159).setPhones(Arrays.asList("1","2","3","4","5"));
		userTransaction.begin();
		entityManager.persist(myEntity);
		userTransaction.commit();
		
		myEntity = entityManager.find(MyEntity.class, identifier);
		assertionHelper.assertNotNull("entity is null", myEntity);
		assertionHelper.assertEquals("mc001", myEntity.getCode());
		assertionHelper.assertEquals(159, myEntity.getIntegerValue());
		//assertionHelper.assertEquals(null, myEntity.getPhones());
		
		EntityGraph<MyEntity> entityGraph = entityManager.createEntityGraph(MyEntity.class);
		entityGraph.addAttributeNodes("code");
		entityGraph.addAttributeNodes("integerValue");
		entityGraph.addAttributeNodes("phones");		
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.fetchgraph", entityGraph);
		myEntity = entityManager.find(MyEntity.class, identifier,properties);
		assertionHelper.assertNotNull("entity is null", myEntity);
		assertionHelper.assertEquals("mc001", myEntity.getCode());
		assertionHelper.assertEquals(159, myEntity.getIntegerValue());
		assertThat(myEntity.getPhones()).containsExactly("1","2","3","4","5");
		
		entityGraph = entityManager.createEntityGraph(MyEntity.class);
		entityGraph.addAttributeNodes("code");
		properties = new HashMap<>();
		properties.put("javax.persistence.fetchgraph", entityGraph);
		myEntity = entityManager.find(MyEntity.class, identifier,properties);
		assertionHelper.assertNotNull("entity is null", myEntity);
		assertionHelper.assertEquals("mc001", myEntity.getCode());
		//assertionHelper.assertEquals(null, myEntity.getIntegerValue());
		//assertionHelper.assertEquals(null, myEntity.getPhones());
		
		//assertionHelper.assertNull("field integer value has been loaded", myEntity.getIntegerValue());
	}
	
	/* Rules */
	
	@Test
	public void is_myEntityCodeMustBeUnique() throws Exception{
		TestPersistenceCreate test = __inject__(TestPersistenceCreate.class);
		String code = "a";
		test.addObjects(new MyEntity().setCode(code),new MyEntity().setCode(code)).setName("MyEntity.code unicity").setExpectedThrowableCauseClassIsSqlException();
		test.execute();			
	}
	
	@Test
	public void is_myEntityCodeMustBeNotNull() throws Exception{
		TestPersistenceCreate test = __inject__(TestPersistenceCreate.class);
		test.addObjects(new MyEntity()).setName("MyEntity.code notnull").setExpectedThrowableCauseClassIsConstraintViolationException().execute();
		assertThat(__inject__(ThrowableHelper.class).getInstanceOf(test.getThrowable(), ConstraintViolationException.class).getMessage()).contains("propertyPath=code");
	}
	
	/* Hierarchy */
	
	@Test
	public void create_node() throws Exception{
		userTransaction.begin();
		Node nodeModule = new Node().setCode("module").setName(__getRandomName__());
		Node nodeService = new Node().setCode("service").setName(__getRandomName__()).addParents(nodeModule);
		Node nodeMenu = new Node().setCode("menu").setName(__getRandomName__()).addParents(nodeService);
		Node nodeAction = new Node().setCode("action").setName(__getRandomName__()).addParents(nodeMenu);
		__inject__(NodePersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(nodeModule,nodeService,nodeMenu
				,nodeAction));
		__inject__(NodeHierarchyPersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(
				new NodeHierarchy().setParent(nodeModule).setChild(nodeService)
				,new NodeHierarchy().setParent(nodeService).setChild(nodeMenu)
				,new NodeHierarchy().setParent(nodeMenu).setChild(nodeAction)
				));
		userTransaction.commit();
		Node node;
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("module");
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("module",new Properties().setFields(Node.FIELD_PARENTS));
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("module",new Properties().setFields(Node.FIELD_CHILDREN));
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren().get()).isNotEmpty();
		assertThat(node.getChildren().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("service");
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("module",new Properties().setFields(Node.FIELD_PARENTS+","+Node.FIELD_CHILDREN));
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren().get()).isNotEmpty();
		assertThat(node.getChildren().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("service");
		
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("service");
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("service",new Properties().setFields(Node.FIELD_PARENTS));
		assertThat(node.getParents()).isNotNull();
		assertThat(node.getParents().get()).isNotEmpty();
		assertThat(node.getParents().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("module");
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("service",new Properties().setFields(Node.FIELD_CHILDREN));
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren().get()).isNotEmpty();
		assertThat(node.getChildren().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("menu");
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("service",new Properties().setFields(Node.FIELD_PARENTS+","+Node.FIELD_CHILDREN));
		assertThat(node.getParents()).isNotNull();
		assertThat(node.getParents().get()).isNotEmpty();
		assertThat(node.getParents().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("module");
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren().get()).isNotEmpty();
		assertThat(node.getChildren().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("menu");
		
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("action");
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("action",new Properties().setFields(Node.FIELD_PARENTS));
		assertThat(node.getParents()).isNotNull();
		assertThat(node.getParents().get()).isNotEmpty();
		assertThat(node.getParents().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("menu");
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("action",new Properties().setFields(Node.FIELD_CHILDREN));
		assertThat(node.getParents()).isNull();
		assertThat(node.getChildren()).isNull();
		node = __inject__(NodePersistence.class).readByBusinessIdentifier("action",new Properties().setFields(Node.FIELD_PARENTS+","+Node.FIELD_CHILDREN));
		assertThat(node.getParents()).isNotNull();
		assertThat(node.getParents().get()).isNotEmpty();
		assertThat(node.getParents().get().stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("menu");
		assertThat(node.getChildren()).isNull();
	}
	
	@Test
	public void read_node_filter_byParent_identifier_business() throws Exception{
		userTransaction.begin();
		Node nodeModule = new Node().setIdentifier("MO").setCode("module").setName(__getRandomName__());
		Node nodeService = new Node().setIdentifier("S").setCode("service").setName(__getRandomName__()).addParents(nodeModule);
		Node nodeMenu = new Node().setIdentifier("ME").setCode("menu").setName(__getRandomName__()).addParents(nodeService);
		Node nodeAction = new Node().setIdentifier("A").setCode("action").setName(__getRandomName__()).addParents(nodeMenu);
		__inject__(NodePersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(nodeModule,nodeService,nodeMenu
				,nodeAction));
		__inject__(NodeHierarchyPersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(
				new NodeHierarchy().setParent(nodeModule).setChild(nodeService)
				,new NodeHierarchy().setParent(nodeService).setChild(nodeMenu)
				,new NodeHierarchy().setParent(nodeMenu).setChild(nodeAction)
				));
		userTransaction.commit();
		
		Filter filters = __inject__(Filter.class).setKlass(Node.class);
		filters.addField(Node.FIELD_PARENTS, Arrays.asList("module"));
		Collection<Node> nodes = __inject__(NodePersistence.class).read(new Properties().setQueryFilters(filters));
		assertThat(nodes).isNotEmpty();
		assertThat(nodes.stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("service");
	}
	
	@Test
	public void read_node_filter_byParent_identifier_system() throws Exception{
		userTransaction.begin();
		Node nodeModule = new Node().setIdentifier("MO").setCode("module").setName(__getRandomName__());
		Node nodeService = new Node().setIdentifier("S").setCode("service").setName(__getRandomName__()).addParents(nodeModule);
		Node nodeMenu = new Node().setIdentifier("ME").setCode("menu").setName(__getRandomName__()).addParents(nodeService);
		Node nodeAction = new Node().setIdentifier("A").setCode("action").setName(__getRandomName__()).addParents(nodeMenu);
		__inject__(NodePersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(nodeModule,nodeService,nodeMenu
				,nodeAction));
		__inject__(NodeHierarchyPersistence.class).createMany(__inject__(CollectionHelper.class).instanciate(
				new NodeHierarchy().setParent(nodeModule).setChild(nodeService)
				,new NodeHierarchy().setParent(nodeService).setChild(nodeMenu)
				,new NodeHierarchy().setParent(nodeMenu).setChild(nodeAction)
				));
		userTransaction.commit();
		
		Filter filters = __inject__(Filter.class).setKlass(Node.class);
		filters.addField(Node.FIELD_PARENTS, Arrays.asList("MO"),ValueUsageType.SYSTEM);
		Collection<Node> nodes = __inject__(NodePersistence.class).read(new Properties().setQueryFilters(filters));
		assertThat(nodes).isNotEmpty();
		assertThat(nodes.stream().map(Node::getCode).collect(Collectors.toList())).containsOnly("service");
	}
	
	/* Filter mapping */
	
	@Test
	public void map_filter_field_to_dto_string() throws Exception{
		Field field = __inject__(Field.class).setInstance(__inject__(FieldInstancesRuntime.class).get(Class.class, "f01")).setValue("12")
				.setValueUsageType(ValueUsageType.BUSINESS).setArithmeticOperator(ArithmeticOperator.LIKE);
		FieldDto fieldDto = __inject__(MappingHelper.class).getSource(field, FieldDto.class);
		assertThat(fieldDto).isNotNull();
		assertThat(fieldDto.getArithmeticOperator()).isEqualTo(ArithmeticOperator.LIKE);
		assertThat(fieldDto.getField()).isNotNull();
		assertThat(fieldDto.getField().getKlass()).isEqualTo(Class.class.getSimpleName());
		assertThat(fieldDto.getField().getPath()).isEqualTo("f01");
		assertThat(fieldDto.getField().getType()).isEqualTo(org.cyk.utility.field.FieldDto.Type.STRING);
		assertThat(fieldDto.getValue()).isNotNull();
		assertThat(fieldDto.getValue().getValue()).isEqualTo("12");
		assertThat(fieldDto.getValue().getContainer()).isEqualTo(ValueDto.Container.NONE);
		assertThat(fieldDto.getValue().getType()).isEqualTo(ValueDto.Type.STRING);
		assertThat(fieldDto.getValue().getUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@Test
	public void map_filter_field_dto_to_field_string() throws Exception{
		FieldDto fieldDto = new FieldDto();
		fieldDto.setArithmeticOperator(ArithmeticOperator.LIKE);
		fieldDto.setField(new org.cyk.utility.field.FieldDto().setKlass(Class.class.getName()).setPath("f01").setType(org.cyk.utility.field.FieldDto.Type.STRING));
		fieldDto.setValue(new ValueDto().setContainer(ValueDto.Container.NONE).setType(ValueDto.Type.STRING).setUsageType(ValueUsageType.BUSINESS).setValue("hello"));
		
		Field field = __inject__(MappingHelper.class).getDestination(fieldDto, Field.class);
		assertThat(field).isNotNull();
		assertThat(field.getArithmeticOperator()).isEqualTo(ArithmeticOperator.LIKE);
		assertThat(field.getInstance()).isNotNull();
		assertThat(field.getInstance().getClazz()).isEqualTo(Class.class);
		assertThat(field.getInstance().getPath()).isEqualTo("f01");
		assertThat(field.getInstance().getType()).isEqualTo(String.class);
		assertThat(field.getValue()).isNotNull();
		assertThat(field.getValue().getClass()).isEqualTo(String.class);
		assertThat(field.getValue()).isEqualTo("hello");
		assertThat(field.getValueUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@Test
	public void map_filter_field_to_dto_collection_string() throws Exception{
		Field field = __inject__(Field.class).setInstance(__inject__(FieldInstancesRuntime.class).get(Class.class, "f01")).setValue(Arrays.asList("12","a"))
				.setValueUsageType(ValueUsageType.BUSINESS).setArithmeticOperator(ArithmeticOperator.IN);
		FieldDto fieldDto = __inject__(MappingHelper.class).getSource(field, FieldDto.class);
		assertThat(fieldDto).isNotNull();
		assertThat(fieldDto.getArithmeticOperator()).isEqualTo(ArithmeticOperator.IN);
		assertThat(fieldDto.getField()).isNotNull();
		assertThat(fieldDto.getField().getKlass()).isEqualTo(Class.class.getSimpleName());
		assertThat(fieldDto.getField().getPath()).isEqualTo("f01");
		assertThat(fieldDto.getField().getType()).isEqualTo(org.cyk.utility.field.FieldDto.Type.STRING);
		assertThat(fieldDto.getValue()).isNotNull();
		assertThat(fieldDto.getValue().getValue()).isEqualTo("[\"12\",\"a\"]");
		assertThat(fieldDto.getValue().getContainer()).isEqualTo(ValueDto.Container.COLLECTION);
		assertThat(fieldDto.getValue().getType()).isEqualTo(ValueDto.Type.STRING);
		assertThat(fieldDto.getValue().getUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void map_filter_dto_to_field_collection_string() throws Exception{
		FieldDto fieldDto = new FieldDto();
		fieldDto.setArithmeticOperator(ArithmeticOperator.IN);
		fieldDto.setField(new org.cyk.utility.field.FieldDto().setKlass(Class.class.getName()).setPath("f01").setType(org.cyk.utility.field.FieldDto.Type.STRING));
		fieldDto.setValue(new ValueDto().setContainer(ValueDto.Container.COLLECTION).setType(ValueDto.Type.STRING).setUsageType(ValueUsageType.BUSINESS).setValue("[\"12\",\"a\"]"));
		
		Field field = __inject__(MappingHelper.class).getDestination(fieldDto, Field.class);
		assertThat(field).isNotNull();
		assertThat(field.getArithmeticOperator()).isEqualTo(ArithmeticOperator.IN);
		assertThat(field.getInstance()).isNotNull();
		assertThat(field.getInstance().getClazz()).isEqualTo(Class.class);
		assertThat(field.getInstance().getPath()).isEqualTo("f01");
		assertThat(field.getInstance().getType()).isEqualTo(String.class);
		assertThat(field.getValue()).isNotNull();
		assertThat(field.getValue()).isInstanceOf(Collection.class);
		assertThat((Collection<String>)field.getValue()).containsOnly("12","a");
		assertThat(field.getValueUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@Test
	public void map_filter_field_to_dto_collection_integer() throws Exception{
		Field field = __inject__(Field.class).setInstance(__inject__(FieldInstancesRuntime.class).get(Class.class, "f01")).setValue(Arrays.asList(12,27))
				.setValueUsageType(ValueUsageType.BUSINESS).setArithmeticOperator(ArithmeticOperator.IN);
		FieldDto fieldDto = __inject__(MappingHelper.class).getSource(field, FieldDto.class);
		assertThat(fieldDto).isNotNull();
		assertThat(fieldDto.getArithmeticOperator()).isEqualTo(ArithmeticOperator.IN);
		assertThat(fieldDto.getField()).isNotNull();
		assertThat(fieldDto.getField().getKlass()).isEqualTo(Class.class.getSimpleName());
		assertThat(fieldDto.getField().getPath()).isEqualTo("f01");
		assertThat(fieldDto.getField().getType()).isEqualTo(org.cyk.utility.field.FieldDto.Type.STRING);
		assertThat(fieldDto.getValue()).isNotNull();
		assertThat(fieldDto.getValue().getValue()).isEqualTo("[12,27]");
		assertThat(fieldDto.getValue().getContainer()).isEqualTo(ValueDto.Container.COLLECTION);
		assertThat(fieldDto.getValue().getType()).isEqualTo(ValueDto.Type.INTEGER);
		assertThat(fieldDto.getValue().getUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void map_filter_dto_to_field_collection_integer() throws Exception{
		FieldDto fieldDto = new FieldDto();
		fieldDto.setArithmeticOperator(ArithmeticOperator.IN);
		fieldDto.setField(new org.cyk.utility.field.FieldDto().setKlass(Class.class.getName()).setPath("f01").setType(org.cyk.utility.field.FieldDto.Type.STRING));
		fieldDto.setValue(new ValueDto().setContainer(ValueDto.Container.COLLECTION).setType(ValueDto.Type.INTEGER).setUsageType(ValueUsageType.BUSINESS).setValue("[12,7]"));
		
		Field field = __inject__(MappingHelper.class).getDestination(fieldDto, Field.class);
		assertThat(field).isNotNull();
		assertThat(field.getArithmeticOperator()).isEqualTo(ArithmeticOperator.IN);
		assertThat(field.getInstance()).isNotNull();
		assertThat(field.getInstance().getClazz()).isEqualTo(Class.class);
		assertThat(field.getInstance().getPath()).isEqualTo("f01");
		assertThat(field.getInstance().getType()).isEqualTo(String.class);
		assertThat(field.getValue()).isNotNull();
		assertThat(field.getValue()).isInstanceOf(Collection.class);
		assertThat((Collection<Integer>)field.getValue()).containsOnly(12,7);
		assertThat(field.getValueUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@Test
	public void map_filter_dto_to_field_string() throws Exception{
		FilterDto filterDto = new FilterDto().setKlass(Class.class);
		filterDto.addField("f01", "hello", org.cyk.utility.field.FieldDto.Type.STRING, ValueDto.Container.NONE, ValueDto.Type.STRING, ValueUsageType.BUSINESS, ArithmeticOperator.LIKE);
		Filter filter = __inject__(MappingHelper.class).getDestination(filterDto, Filter.class);
		assertThat(filter).isNotNull();
		assertThat(filter.getFields()).isNotNull();
		assertThat(filter.getFields().get()).hasSize(1);
		Field field = filter.getFields().getFirst();
		assertThat(field).isNotNull();
		assertThat(field.getArithmeticOperator()).isEqualTo(ArithmeticOperator.LIKE);
		assertThat(field.getInstance()).isNotNull();
		assertThat(field.getInstance().getClazz()).isEqualTo(Class.class);
		assertThat(field.getInstance().getPath()).isEqualTo("f01");
		assertThat(field.getInstance().getType()).isEqualTo(String.class);
		assertThat(field.getValue()).isNotNull();
		assertThat(field.getValue().getClass()).isEqualTo(String.class);
		assertThat(field.getValue()).isEqualTo("hello");
		assertThat(field.getValueUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	@Test
	public void stringfy_filterDto() throws Exception{
		FilterDto filterDto = new FilterDto().setKlass(Class.class);
		filterDto.addField("f01", "hello", org.cyk.utility.field.FieldDto.Type.STRING, ValueDto.Container.NONE, ValueDto.Type.STRING, ValueUsageType.BUSINESS, ArithmeticOperator.LIKE);
		ObjectMapper objectMapper = new ObjectMapper();
		String string = objectMapper.writeValueAsString(filterDto);
		filterDto = objectMapper.readValue(string, FilterDto.class);
		Filter filter = __inject__(MappingHelper.class).getDestination(filterDto, Filter.class);
		assertThat(filter).isNotNull();
		assertThat(filter.getFields()).isNotNull();
		assertThat(filter.getFields().get()).hasSize(1);
		Field field = filter.getFields().getFirst();
		assertThat(field).isNotNull();
		assertThat(field.getArithmeticOperator()).isEqualTo(ArithmeticOperator.LIKE);
		assertThat(field.getInstance()).isNotNull();
		assertThat(field.getInstance().getClazz()).isEqualTo(Class.class);
		assertThat(field.getInstance().getPath()).isEqualTo("f01");
		assertThat(field.getInstance().getType()).isEqualTo(String.class);
		assertThat(field.getValue()).isNotNull();
		assertThat(field.getValue().getClass()).isEqualTo(String.class);
		assertThat(field.getValue()).isEqualTo("hello");
		assertThat(field.getValueUsageType()).isEqualTo(ValueUsageType.BUSINESS);
	}
	
	/**/
	
	@Getter @Setter @Accessors(chain=true) @ToString
	public static class Class {
		private String f01;
	}
	
}
