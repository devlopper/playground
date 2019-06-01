package org.cyk.utility.field;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.cyk.utility.__kernel__.object.__static__.identifiable.AbstractIdentifiedPersistableByLong;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.clazz.ClassHelper;
import org.cyk.utility.clazz.ClassInstancesRuntime;
import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.function.AbstractFunctionWithPropertiesAsInputAndVoidAsOutputImpl;
import org.cyk.utility.instance.InstanceHelper;
import org.cyk.utility.map.MapHelper;
import org.cyk.utility.value.ValueConverter;
import org.cyk.utility.value.ValueUsageType;

public abstract class AbstractFieldValueCopyImpl extends AbstractFunctionWithPropertiesAsInputAndVoidAsOutputImpl implements FieldValueCopy,Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean isOverridable;
	
	@Override
	protected void ____execute____() throws Exception {
		FieldValueGetter getterModel = getValueGetter();
		FieldValueSetter setterModel = getValueSetter();
		Map<String,String> fieldNameMap = getFieldNameMap();
		Boolean isOverridable = getIsOverridable();
		if(fieldNameMap == null) {
			Boolean isAutomaticallyDetectFields = getIsAutomaticallyDetectFields();
			if(isAutomaticallyDetectFields == null)
				isAutomaticallyDetectFields = Boolean.TRUE;
			if(Boolean.TRUE.equals(isAutomaticallyDetectFields)) {
				Fields fields = __inject__(FieldValueCopyFieldsGetter.class).setSourceClass(getterModel.getObject().getClass())
						.setDestinationClass(setterModel.getObject().getClass()).execute().getOutput();
				if(__inject__(CollectionHelper.class).isNotEmpty(fields)) {
					for(Field index : fields.get()) {
						if(fieldNameMap == null)
							fieldNameMap = new HashMap<>();
						if(!Modifier.isStatic(index.getModifiers()) && !Modifier.isFinal(index.getModifiers()))
							fieldNameMap.put(index.getName(), index.getName());
					}
				}
			}
		}
		if(fieldNameMap == null) {
			Object value = getterModel.execute().getOutput();
			if(setterModel.getField() == null && getterModel.getField()!=null)
				setterModel.setField(getterModel.getField().getName());
			setterModel.setValue(value).execute();
		}else {
			for(Map.Entry<String, String> entry : fieldNameMap.entrySet()) {
				FieldValueSetter setter = __inject__(FieldValueSetter.class).setObject(setterModel.getObject()).setField(entry.getValue());
				if(setter.getField()!=null) {
					if(Boolean.FALSE.equals(isOverridable)) {
						Object setterFieldValue = __inject__(FieldValueGetter.class).execute(setter.getObject(), setter.getField()).getOutput();
						if(setterFieldValue != null)
							continue;
					}
					FieldValueGetter getter = __inject__(FieldValueGetter.class).setObject(getterModel.getObject()).setField(entry.getKey());
					Object value = getter.execute().getOutput();
					if(value!=null)
						value = __processValue__(getter.getField(),setter.getField(),value);
					setter.setValue(value);
					if(setter.getField() == null)
						setter.setField(getter.getField().getName());
					setter.execute();	
				}
			}
		}
	}
	
	protected Object __processValue__(Field source,Field destination,Object value) {
		ClassHelper classHelper = __inject__(ClassHelper.class);
		ClassInstancesRuntime classInstancesRuntime = __inject__(ClassInstancesRuntime.class);
		Class<?> sourceType = __inject__(FieldTypeGetter.class).execute(source).getOutput().getType();
		Class<?> destinationType = __inject__(FieldTypeGetter.class).execute(destination).getOutput().getType();
		Properties properties = new Properties();
		properties.copyFrom(getProperties(), Properties.CONTEXT,Properties.REQUEST);
		
		//Primitive or string or enum
		if(classHelper.isNumberOrStringOrEnum(sourceType)) {
			if(classHelper.isNumberOrStringOrEnum(destinationType)) {
				//convert value
				return __inject__(ValueConverter.class).execute(value, destinationType).getOutput();
			}
			if(!classHelper.isBelongsToJavaPackages(destinationType)) {
				//value might be a destination type instance business identifier
				return __inject__(InstanceHelper.class).getByIdentifierBusiness(destinationType, value,properties);
			}
		}
		
		//Non java classes , means custom classes
		if(!classHelper.isBelongsToJavaPackages(sourceType)) {
			if(classHelper.isNumberOrStringOrEnum(destinationType)) {
				//get value business identifier
				return __inject__(FieldHelper.class).getFieldValueBusinessIdentifier(value);
			}
			if(!classHelper.isBelongsToJavaPackages(destinationType)) {
				if(Boolean.TRUE.equals(classInstancesRuntime.get(destinationType).getIsPersistable())) {
					if(destination.isAnnotationPresent(javax.persistence.ManyToOne.class)) {
						//Find the object to be linked by its identifier (system and/or business)
						Object identifier = __inject__(FieldHelper.class).getFieldValueSystemIdentifier(value);
						if(identifier == null) {
							identifier = __inject__(FieldHelper.class).getFieldValueBusinessIdentifier(value);
							return __inject__(InstanceHelper.class).getByIdentifierBusiness(destinationType, identifier,properties);
						}else {
							Fields fields =  __inject__(FieldGetter.class).setClazz(destinationType).setFieldName(FieldName.IDENTIFIER).setValueUsageType(ValueUsageType.SYSTEM)
									.execute().getOutput();
							if(__inject__(CollectionHelper.class).isNotEmpty(fields)) {
								Class<?> identifierTypeDestinationType = null;//fields.getFirst().getType();
								if(__inject__(ClassHelper.class).isInstanceOf(destinationType, AbstractIdentifiedPersistableByLong.class))
									identifierTypeDestinationType = Long.class;
								identifier = __inject__(ValueConverter.class).execute(identifier, identifierTypeDestinationType).getOutput();	
							}
							
							return __inject__(InstanceHelper.class).getByIdentifierSystem(destinationType, identifier,properties);
						}	
					}
				}
				
				//get the field to be copied and call field value copier
				Object temp = value;
				value = __inject__(destinationType);
				FieldValueCopy fieldValueCopy = __inject__(FieldValueCopy.class).setSource(temp).setDestination(value);
				if(Boolean.TRUE.equals(classInstancesRuntime.get(sourceType).getIsPersistable()) && 
						Boolean.TRUE.equals(classInstancesRuntime.get(destinationType).getIsTransferable())) {
					//TODO if business identifier is there take it first otherwise take system identifier
					fieldValueCopy.setFieldName(__inject__(FieldNameGetter.class).execute(sourceType, FieldName.IDENTIFIER, ValueUsageType.SYSTEM).getOutput());
					//setFieldName(__inject__(FieldNameGetter.class).execute(sourceType, FieldName.IDENTIFIER, ValueUsageType.BUSINESS).getOutput());								
				}else {
					fieldValueCopy.setIsAutomaticallyDetectFields(Boolean.TRUE);
				}
				fieldValueCopy.execute();
				return value;
			}
		}
		
		/*if(isUnSet && !classHelper.isBelongsToJavaPackages(sourceType)) {
			//value will be compute by converter
			isUnSet = Boolean.FALSE;
		}*/
		
		//__inject__(ThrowableHelper.class).throwRuntimeException("value copy from field <<"+source+">> to field <<"+destination+">> is not yet handled");
		
		/*
		if(!sourceType.isPrimitive() && !sourceType.isEnum() && !__inject__(ClassHelper.class).isInstanceOf(sourceType, Collection.class) && !StringUtils.startsWithAny(sourceType.getName(), "java.","javax.")) {
			//source is a custom object
			if(Boolean.TRUE.equals(__inject__(ClassInstancesRuntime.class).get(destinationType).getIsPersistable())) {
				if(destination.isAnnotationPresent(javax.persistence.ManyToOne.class)) {
					//Find the object to be linked by its identifier (system and/or business)
					Object identifier = __inject__(FieldHelper.class).getFieldValueSystemIdentifier(value);
					if(identifier == null) {
						identifier = __inject__(FieldHelper.class).getFieldValueBusinessIdentifier(value);
						value = __inject__(InstanceHelper.class).getByIdentifierBusiness(destinationType, identifier,properties);
					}else {
						value = __inject__(InstanceHelper.class).getByIdentifierSystem(destinationType, identifier,properties);
					}	
					isUnSet = Boolean.FALSE;
				}
			}
			
			if(Boolean.TRUE.equals(isUnSet)) {
				if(String.class.equals(destinationType) || destinationType.isPrimitive()) {
					//single value
					value = __inject__(FieldHelper.class).getFieldValueBusinessIdentifier(value);
					isUnSet = Boolean.FALSE;
				}else if(!StringUtils.startsWithAny(destinationType.getName(), "java.","javax.")){
					//not a single value
					Object temp = value;
					value = __inject__(destinationType);
					FieldValueCopy fieldValueCopy = __inject__(FieldValueCopy.class).setSource(temp).setDestination(value);
					getProperties().setCaller(getFunction());
					if(getFunction().getProperties().getCaller() == null) {
						setIsAutomaticallyDetectFields(Boolean.TRUE);		
					}else {
						setFieldName(__inject__(FieldNameGetter.class).execute(sourceType, FieldName.IDENTIFIER, ValueUsageType.SYSTEM).getOutput());
						setFieldName(__inject__(FieldNameGetter.class).execute(sourceType, FieldName.IDENTIFIER, ValueUsageType.BUSINESS).getOutput());
					}
					execute();
					isUnSet = Boolean.FALSE;
				}	
			}
		}
		
		if(Boolean.TRUE.equals(isUnSet)) {
			if(!destinationType.isPrimitive() && !sourceType.isEnum() && !__inject__(ClassHelper.class).isInstanceOf(sourceType, Collection.class) && !StringUtils.startsWithAny(destinationType.getName(), "java.","javax.")) {
				value = __inject__(InstanceHelper.class).getByIdentifierBusiness(destinationType, value,properties);
			}	
		}
		*/
		return value;
	}
	
	@Override
	public FieldValueCopy execute(Object source, Object destination, Map<String, String> fieldNameMap) {
		return (FieldValueCopy) setValueGetter(__inject__(FieldValueGetter.class).setObject(source))
				.setValueSetter(__inject__(FieldValueSetter.class).setObject(destination)).setFieldNameMap(fieldNameMap)
				.setIsAutomaticallyDetectFields(fieldNameMap == null).execute();
	}
	
	@Override
	public FieldValueCopy execute(Object source, Object destination, String fieldName) {
		@SuppressWarnings("rawtypes")
		Map map = __inject__(MapHelper.class).instanciate(fieldName,fieldName);
		return execute(source, destination,map );
	}
	
	@Override
	public FieldValueCopy execute(Object source, Object destination) {
		return execute(source, destination,(Map<String, String>)null );
	}
	
	@Override
	public FieldValueGetter getValueGetter() {
		return (FieldValueGetter) getProperties().getFromPath(Properties.VALUE,Properties.GETTER);
	}

	@Override
	public FieldValueCopy setValueGetter(FieldValueGetter valueGetter) {
		getProperties().setFromPath(new Object[] {Properties.VALUE,Properties.GETTER},valueGetter);
		return this;
	}
	
	@Override
	public FieldValueGetter getValueGetter(Boolean injectIfNull) {
		FieldValueGetter getter = getValueGetter();
		if(getter == null && Boolean.TRUE.equals(injectIfNull))
			setValueGetter(getter = __inject__(FieldValueGetter.class));
		return getter;
	}

	@Override
	public FieldValueSetter getValueSetter() {
		return (FieldValueSetter) getProperties().getFromPath(Properties.VALUE,Properties.SETTER);
	}

	@Override
	public FieldValueCopy setValueSetter(FieldValueSetter valueSetter) {
		getProperties().setFromPath(new Object[] {Properties.VALUE,Properties.SETTER},valueSetter);
		return this;
	}
	
	@Override
	public FieldValueSetter getValueSetter(Boolean injectIfNull) {
		FieldValueSetter setter = getValueSetter();
		if(setter == null && Boolean.TRUE.equals(injectIfNull))
			setValueSetter(setter = __inject__(FieldValueSetter.class));
		return setter;
	}
	
	@Override
	public Map<String, String> getFieldNameMap() {
		return (Map<String, String>) getProperties().getFromPath(Properties.MAP,Properties.FIELD_NAME);
	}
	
	@Override
	public FieldValueCopy setFieldNameMap(Map<String, String> fieldNameMap) {
		getProperties().setFromPath(new Object[] {Properties.MAP,Properties.FIELD_NAME},fieldNameMap);
		return this;
	}
	
	@Override
	public FieldValueCopy setFieldName(String source, String destination) {
		Map<String,String> map = getFieldNameMap();
		if(map == null)
			setFieldNameMap(map = new HashMap<>());
		map.put(source, destination);
		return this;
	}
	
	@Override
	public FieldValueCopy setFieldName(String fieldName) {
		return setFieldName(fieldName, fieldName);
	}
	
	@Override
	public FieldValueCopy setSource(Object source) {
		getValueGetter(Boolean.TRUE).setObject(source);
		return this;
	}
	
	@Override
	public FieldValueCopy setDestination(Object destination) {
		getValueSetter(Boolean.TRUE).setObject(destination);
		return this;
	}

	@Override
	public FieldValueCopy setIsAutomaticallyDetectFields(Boolean value) {
		getProperties().setFromPath(new String[] {Properties.IS,Properties.DETECT,Properties.FIELD},value);
		return this;
	}
	
	@Override
	public Boolean getIsAutomaticallyDetectFields() {
		return (Boolean) getProperties().getFromPath(Properties.IS,Properties.DETECT,Properties.FIELD);
	}

	@Override
	public Boolean getIsOverridable() {
		return isOverridable;
	}
	
	@Override
	public FieldValueCopy setIsOverridable(Boolean isOverridable) {
		this.isOverridable = isOverridable;
		return this;
	}
}
