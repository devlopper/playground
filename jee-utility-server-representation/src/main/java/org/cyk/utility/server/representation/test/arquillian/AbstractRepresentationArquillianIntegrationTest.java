package org.cyk.utility.server.representation.test.arquillian;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.random.RandomHelperImpl;
import org.cyk.utility.server.representation.AbstractEntity;
import org.cyk.utility.server.representation.AbstractEntityCollection;
import org.cyk.utility.server.representation.RepresentationEntity;
import org.cyk.utility.system.action.SystemAction;
import org.cyk.utility.system.layer.SystemLayer;
import org.cyk.utility.system.layer.SystemLayerRepresentation;
import org.cyk.utility.test.arquillian.AbstractSystemServerArquillianIntegrationTestImpl;
import org.cyk.utility.test.arquillian.SystemServerIntegrationTest;
import org.cyk.utility.value.ValueUsageType;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.openqa.selenium.WebDriver;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class AbstractRepresentationArquillianIntegrationTest extends AbstractSystemServerArquillianIntegrationTestImpl<RepresentationEntity> implements SystemServerIntegrationTest<RepresentationEntity>, Serializable {
	private static final long serialVersionUID = 1L;
	
	/* Global variable */
	protected static ResteasyClient CLIENT;
	protected static ResteasyWebTarget TARGET;
	
	@Drone protected WebDriver browser;
	@ArquillianResource protected URL contextPath;
	
	@Override
	protected void __listenBeforeCallCountIsZero__() throws Exception{
		super.__listenBeforeCallCountIsZero__();
		CLIENT = new ResteasyClientBuilder().build();
		TARGET = CLIENT.target(UriBuilder.fromPath(contextPath.toExternalForm()));
	}
	
	@Override
	protected void __initializeApplicationScopeLifeCycleListener__() {
		
	}
	
	@Override
	protected <ENTITY> void ____createEntity____(ENTITY entity, RepresentationEntity representation) {
		Response response = representation.createOne(entity);
		assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
		response.close();
		if(entity instanceof AbstractEntity) {
			response = representation.getOne(((AbstractEntity)entity).getCode(), ValueUsageType.BUSINESS.name());
			entity = (ENTITY) response.readEntity(entity.getClass());
			assertThat(((AbstractEntity)entity).getIdentifier()).isNotBlank();	
			response.close();
		}
	}
	
	@Override
	protected <ENTITY> void ____createEntity____(Collection<ENTITY> entities,RepresentationEntity representation) {
		AbstractEntityCollection<ENTITY> collection = (AbstractEntityCollection<ENTITY>) instanciateOne(__getEntityCollectionClass__(entities.iterator().next().getClass()));
		collection.add(entities);		
		Response response = representation.createMany(collection);
		assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
		response.close();
		for(ENTITY index : entities)
			if(index instanceof AbstractEntity) {
				Object entity = index;
				response = representation.getOne(((AbstractEntity)entity).getCode(), ValueUsageType.BUSINESS.name());
				entity = (ENTITY) response.readEntity(entity.getClass());
				assertThat(((AbstractEntity)entity).getIdentifier()).isNotBlank();	
				response.close();
			}
	}
	
	@Override
	protected <ENTITY> void ____assertThatLogSaysEntityHasBeen____(Class<? extends SystemAction> systemActionClass,ENTITY entity,RepresentationEntity layerEntityInterface) {}

	@Override
	protected <ENTITY> ENTITY ____readEntity____(Class<ENTITY> entityClass, Object identifier,ValueUsageType valueUsageType, Properties expectedFieldValues, RepresentationEntity representation) {
		return (ENTITY) (ValueUsageType.SYSTEM.equals(valueUsageType) ? representation.getOne(identifier.toString(),ValueUsageType.SYSTEM.name()).readEntity(entityClass) 
				: representation.getOne(identifier.toString(),ValueUsageType.BUSINESS.name()).readEntity(entityClass));
	}

	@Override
	protected <ENTITY> void ____updateEntity____(ENTITY entity, RepresentationEntity representation) {
		representation.updateOne(entity);
	}

	@Override
	protected <ENTITY> void ____deleteEntity____(ENTITY entity, RepresentationEntity representation) {
		representation.deleteOne(((AbstractEntity)entity).getIdentifier(),ValueUsageType.SYSTEM.name());
	}
	
	@Override
	protected <ENTITY> void ____deleteEntityAll____(Class<ENTITY> entityClass,RepresentationEntity representation) {
		representation.deleteAll();
	}
	
	@Override
	protected <ENTITY> Long ____countEntitiesAll____(Class<ENTITY> entityClass,RepresentationEntity representation) {
		return representation.count().readEntity(Long.class);
	}
	
	@Override
	protected RepresentationEntity ____getLayerEntityInterfaceFromClass____(Class<?> aClass) {
		return TARGET.proxy(__getLayerEntityInterfaceClass__());
	}

	@Override
	public SystemLayer __getSystemLayer__() {
		return __inject__(SystemLayerRepresentation.class);
	}

	@Override
	protected String __getRandomCode__() {
		return RandomHelperImpl.GENERATOR_NUMERIC.generate(5);
	}
	
	@Override
	protected <T> T instanciateOne(Class<T> aClass) {
		try {
			return aClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void __setFieldValues__(Object object) {
		if(object instanceof AbstractEntity) {
			((AbstractEntity)object).setCode(__getRandomCode__());
		}
	}
	
	protected abstract <ENTITY> Class<? extends AbstractEntityCollection<ENTITY>> __getEntityCollectionClass__(Class<ENTITY> aClass);
}
