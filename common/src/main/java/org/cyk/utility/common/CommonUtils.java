package org.cyk.utility.common;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import javax.inject.Singleton;

import lombok.extern.java.Log;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.cyk.utility.common.cdi.AbstractBean;

@Singleton @Log
public class CommonUtils extends AbstractBean implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6146661020703974108L;

	public Collection<Field> getAllFields(Collection<Field> fields,Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			fields.add(field);
		}

		if (type.getSuperclass() != null) {
			fields = getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}
	
	public Collection<Field> getAllFields(Class<?> type) {
		Collection<Field> fields = new ArrayList<>();
		return getAllFields(fields, type);
	}
	
	public Collection<Field> getAllFields(Class<?> type,Collection<Class<? extends Annotation>> annotationClasses) {
		Collection<Field> fields = new ArrayList<>();
		for(Field field : getAllFields(type))
			for(Class<? extends Annotation> annotationClass : annotationClasses)
				if(field.isAnnotationPresent(annotationClass))
					fields.add(field);
		return fields;
	}

	public Boolean isNumberClass(Class<?> aClass){
		return Number.class.isAssignableFrom(ClassUtils.primitiveToWrapper(aClass));
	}
	
	public Object readField(Object object,Field field,Boolean createIfNull){
		Object r = null;
		try {
			r = FieldUtils.readField(field, object, true);
			if(r==null && Boolean.TRUE.equals(createIfNull))
				r = field.getType().newInstance();
		} catch (Exception e) {
			log.log(Level.SEVERE,e.toString(),e);
		}
		return r;
	}
}
