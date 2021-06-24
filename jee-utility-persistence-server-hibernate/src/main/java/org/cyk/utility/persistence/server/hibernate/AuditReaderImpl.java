package org.cyk.utility.persistence.server.hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.persistence.EntityManagerGetter;
import org.cyk.utility.persistence.query.Querier;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.cyk.utility.persistence.server.audit.AuditReader;
import org.cyk.utility.persistence.server.hibernate.annotation.Hibernate;
import org.cyk.utility.persistence.server.query.executor.DynamicManyExecutor;
import org.hibernate.envers.AuditReaderFactory;

@Hibernate
public class AuditReaderImpl extends AuditReader.AbstractImpl implements Serializable {

	@Override
	protected <T> Collection<T> __readByIdentifiers__(Class<T> klass, Arguments<T> arguments,Collection<Object> identifiers) {
		Collection<T> collection = null;
		org.hibernate.envers.AuditReader reader = AuditReaderFactory.get(EntityManagerGetter.getInstance().get());
		for(Object identifier : identifiers) {
			Collection<T> revisions = getRevisions(klass, arguments, reader, identifier);
			if(CollectionHelper.isEmpty(revisions))
				continue;
			if(collection == null)
				collection = new ArrayList<>();
			collection.addAll(revisions);		
		}
		return collection;
	}
	
	protected <T> Collection<T> getRevisions(Class<T> klass, Arguments<T> arguments,org.hibernate.envers.AuditReader reader,Object identifier) {
		//Get all revisions numbers by identifier
		List<Number> numbers = reader.getRevisions(klass, identifier);
		if(CollectionHelper.isEmpty(numbers))
			return null;
		Collection<T> collection = getInstanceByIdentifierByRevision(klass, arguments, reader, identifier, numbers);		
		return collection;
	}
	
	protected <T> Collection<T> getInstanceByIdentifierByRevision(Class<T> klass, Arguments<T> arguments,org.hibernate.envers.AuditReader reader,Object identifier
			,Collection<Number> numbers) {
		AbstractAuditsRecordsByRevisionsNumbersNativeReader<T> nativeReader = getAuditsRecordsByRevisionsNumbersNativeReader(klass, arguments, identifier, numbers);
		Collection<T> collection = null;
		if(nativeReader == null) {
			for(Number number : numbers) {
				//Get instance by identifier and revision number
				T instance = reader.find(klass, identifier, number);
				if(instance == null)
					continue;
				if(collection == null)
					collection = new ArrayList<>();
				collection.add(instance);
			}
		}else {
			collection = (Collection<T>) nativeReader.readByIdentifiersThenInstantiate(List.of(identifier.toString())
					, Map.of(AbstractAuditsRecordsByRevisionsNumbersNativeReader.PARAMETER_NAME_NUMBERS,numbers));
		}
		return collection;
	}
	
	protected <T> AbstractAuditsRecordsByRevisionsNumbersNativeReader<T> getAuditsRecordsByRevisionsNumbersNativeReader(Class<T> klass, Arguments<T> arguments,Object identifier,Collection<Number> numbers) {
		return null;
	}
	
	/**/
	
	@Override
	protected <T> Collection<T> __readByDates__(Class<T> klass, Arguments<T> arguments, LocalDateTime fromDate,LocalDateTime toDate) {
		Collection<T> collection = null;
		collection = DynamicManyExecutor.getInstance().read(klass, null/*new QueryExecutorArguments().addFilterFieldsValues("fromDate",fromDate,"toDate",toDate)*/);
		return collection;
	}
}