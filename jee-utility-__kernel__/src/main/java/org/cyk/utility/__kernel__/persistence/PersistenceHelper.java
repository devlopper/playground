package org.cyk.utility.__kernel__.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.properties.Properties;
import org.jboss.weld.exceptions.IllegalArgumentException;

public interface PersistenceHelper {

	static EntityManager getEntityManager(Properties properties, Boolean injectIfNull) {
		EntityManager entityManager = (EntityManager) (properties == null ? null : properties.getEntityManager());
		if(entityManager == null && Boolean.TRUE.equals(injectIfNull))
			entityManager = DependencyInjection.inject(EntityManager.class);
		return entityManager;
	}

	static EntityManager getEntityManager(Properties properties) {
		return getEntityManager(properties, Boolean.TRUE);
	}
	
	static <T> T getEntityWithItsReferenceOnly(Class<T> klass,Object identifier,EntityManager entityManager) {
		if(klass == null)
			throw new IllegalArgumentException("class is required");		
		if(identifier == null)
			return null;
		if(entityManager == null)
			entityManager = EntityManagerGetter.getInstance().get();
		return entityManager.getReference(klass, identifier);
	}
	
	static <T> T getEntityWithItsReferenceOnly(Class<T> klass,Object identifier) {
		return getEntityWithItsReferenceOnly(klass, identifier, EntityManagerGetter.getInstance().get());
	}
	
	/**/
	
	static Boolean areRelated(Class<?> class1,Class<?> class2,Collection<Class<? extends Annotation>> relationsAnnotationsClasses) {
		if(class1 == null || class2 == null || CollectionHelper.isEmpty(relationsAnnotationsClasses))
			return Boolean.FALSE;
		Collection<Field> relationsAnnotationsFields = FieldHelper.getByAnnotationsClasses(class1, relationsAnnotationsClasses);
		if(CollectionHelper.isEmpty(relationsAnnotationsFields))
			return Boolean.FALSE;
		for(Field field : relationsAnnotationsFields) {
			Type type = FieldHelper.getType(field, class1);
			if(type.equals(class2))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	@SafeVarargs
	static Boolean areRelated(Class<?> class1,Class<?> class2,Class<? extends Annotation>...relationsAnnotationsClasses) {
		if(class1 == null || class2 == null || ArrayHelper.isEmpty(relationsAnnotationsClasses))
			return Boolean.FALSE;
		return areRelated(class1, class2, CollectionHelper.listOf(relationsAnnotationsClasses));
	}
	
	static void sort(List<Class<?>> classes,Boolean ascending) {
		if(CollectionHelper.isEmpty(classes))
			return;
		if(ascending == null)
			ascending = Boolean.TRUE;
		for(Integer i=0; i<classes.size() - 1;) {
			Boolean swapped = null;
			for(Integer j=i+1; j<classes.size(); j = j+1) {
				if(PersistenceHelper.areRelated(classes.get(i), classes.get(j), ManyToOne.class)) {
					if(!ascending)
						continue;
					CollectionHelper.swap(classes, i, j);
					swapped = Boolean.TRUE;
				}				
			}
			if(Boolean.TRUE.equals(swapped))
				i = 0;
			else
				i = i+1;
			swapped = null;
		}
	}
	
	static void sort(List<Class<?>> classes) {
		if(CollectionHelper.isEmpty(classes))
			return;
		sort(classes, null);
	}
	
	static List<Class<?>> sort(Boolean ascending,Class<?>...classes) {
		if(ArrayHelper.isEmpty(classes))
			return null;
		List<Class<?>> list = CollectionHelper.listOf(classes);
		sort(list,ascending);
		return list;
	}
	
	static List<Class<?>> sort(Class<?>...classes) {
		if(ArrayHelper.isEmpty(classes))
			return null;
		return sort(Boolean.TRUE,classes);
	}

	/**/
	
	
}
