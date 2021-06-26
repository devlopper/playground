package org.cyk.utility.persistence.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.cyk.utility.__kernel__.Helper;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.object.marker.AuditableWhoDoneWhatWhen;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.persistence.server.audit.AuditReader;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface TransientFieldsProcessor {

	void process(Arguments arguments);
	void process(Collection<?> objects,Filter filter,Collection<String> fieldsNames);
	void process(Collection<?> objects,Filter filter,String...fieldsNames);
	
	public static abstract class AbstractImpl extends AbstractObject implements TransientFieldsProcessor,Serializable {
		
		@Override
		public void process(Arguments arguments) {
			if(arguments == null || CollectionHelper.isEmpty(arguments.objects) || CollectionHelper.isEmpty(arguments.fieldsNames))
				return;
			Class<?> klass = arguments.klass;
			if(klass == null)
				klass = arguments.objects.iterator().next().getClass();
			try {
				Collection<String> fieldsNames = CollectionHelper.isEmpty(arguments.fieldsNames) ? null : new ArrayList<String>(arguments.fieldsNames);
				if(CollectionHelper.isNotEmpty(fieldsNames))
					fieldsNames.remove(AuditableWhoDoneWhatWhen.FIELD___AUDIT_RECORDS__);
				__process__(klass, arguments.objects,arguments.filter,fieldsNames);
				if(CollectionHelper.contains(arguments.fieldsNames, AuditableWhoDoneWhatWhen.FIELD___AUDIT_RECORDS__))
					__processAuditsRecords__(klass, arguments.objects,arguments.filter);
			} catch (Exception exception) {
				LogHelper.log(exception, getClass());
			}
		}
		
		protected void __process__(Class<?> klass,Collection<?> objects,Filter filter,Collection<String> fieldsNames) {
			for(String fieldName : fieldsNames) {
				if(StringHelper.isBlank(fieldName))
					continue;
				Boolean processed = __process__(klass,objects,filter,fieldName);
				if(!Boolean.TRUE.equals(processed))
					logFieldNameHasNotBeenSet(klass, fieldName);
			}
		}
		
		protected Boolean __process__(Class<?> klass,Collection<?> objects,Filter filter,String fieldName) {
			return Boolean.TRUE;
		}
		
		protected Boolean __processAuditsRecords__(Class<?> klass,Collection<?> objects,Filter filter) {
			AuditReader.Arguments<Object> arguments = new AuditReader.Arguments<Object>();
			arguments.setIdentifiables(CollectionHelper.cast(Object.class, objects));
			arguments.setAuditsRecordsCollectionSettable(Boolean.TRUE);
			AuditReader.getInstance().read((Class<Object>)klass, arguments);
			return Boolean.TRUE;
		}
		
		protected void logFieldNameHasNotBeenSet(Class<?> klass,String fieldName) {
			LogHelper.logWarning(String.format("Transient field name <<%s.%s>> has not been processed", klass.getName(),fieldName), getClass());
		}
		
		@Override
		public void process(Collection<?> objects,Filter filter,Collection<String> fieldsNames) {
			if(CollectionHelper.isEmpty(objects) || CollectionHelper.isEmpty(fieldsNames))
				return;
			process(new Arguments().setKlass(objects.iterator().next().getClass()).setObjects(objects).setFilter(filter).setFieldsNames(fieldsNames));
		}
		
		@Override
		public void process(Collection<?> objects,Filter filter,String... fieldsNames) {
			if(ArrayHelper.isEmpty(fieldsNames))
				return;
			process(objects,filter,CollectionHelper.listOf(fieldsNames));
		}
		
		protected String ifTrueYesElseNo(Class<?> klass,String fieldName,Boolean value) {
			return org.cyk.utility.persistence.server.Helper.ifTrueYesElseNo(value);
		}
		
		protected String ifTrueYesElseNo(Boolean value) {
			return org.cyk.utility.persistence.server.Helper.ifTrueYesElseNo(value);
		}
	}
	
	/**/
	
	@Getter @Setter @Accessors(chain=true)
	public static class Arguments implements Serializable {
		private Class<?> klass;
		private Collection<?> objects;
		private Collection<String> fieldsNames;
		private Collection<String> flags;
		private Filter filter;
	}
	
	/**/
	
	static TransientFieldsProcessor getInstance() {
		return Helper.getInstance(TransientFieldsProcessor.class, INSTANCE);
	}
	
	Value INSTANCE = new Value();		
}