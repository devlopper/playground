package org.cyk.utility.common.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.common.Constant;

@Singleton
public class CollectionHelper extends AbstractHelper implements Serializable  {

	private static final long serialVersionUID = 1L;

	private static CollectionHelper INSTANCE;
	
	public static CollectionHelper getInstance() {
		if(INSTANCE == null)
			INSTANCE = new CollectionHelper();
		return INSTANCE;
	}
	
	@Override
	protected void initialisation() {
		INSTANCE = this;
		super.initialisation();
	}
	
	public <ELEMENT> Collection<ELEMENT> get(Boolean distinct,@SuppressWarnings("unchecked") ELEMENT...elements){
		Collection<ELEMENT> result = Boolean.TRUE.equals(distinct) ? new LinkedHashSet<ELEMENT>() : new ArrayList<ELEMENT>();
		if(elements!=null)
			for(ELEMENT element : elements)
				if(element!=null)
					result.add(element);
		return result;
	}
	
	public <ELEMENT> Collection<ELEMENT> get(@SuppressWarnings("unchecked") ELEMENT...elements){
		return get(Boolean.FALSE,elements);
	}
	
	public <COLLECTION extends Collection<?>,ELEMENT> Collection<ELEMENT> add(Class<COLLECTION> collectionClass,Collection<ELEMENT> collection,Boolean append,Collection<ELEMENT> elements){
		Collection<ELEMENT> result = Boolean.TRUE.equals(append) ? collection : null;
		if(result==null)
			result = Set.class.isAssignableFrom(collectionClass) ? new LinkedHashSet<ELEMENT>() : new ArrayList<ELEMENT>();
		if(Boolean.TRUE.equals(append))
			;
		else
			if(collection!=null)
				result.addAll(collection);
		result.addAll(elements);
		return result;
	}
	
	public <ELEMENT> Collection<ELEMENT> add(Collection<ELEMENT> collection,Boolean append,Collection<ELEMENT> elements){
		return add(List.class, collection, append, elements);
	}
	
	public <COLLECTION extends Collection<?>,ELEMENT> Collection<ELEMENT> add(Class<COLLECTION> collectionClass,Collection<ELEMENT> collection,Boolean append,@SuppressWarnings("unchecked") ELEMENT...elements){
		return add(collectionClass,collection,append,get(elements));
	}
	
	public <COLLECTION extends Collection<?>,ELEMENT> Collection<ELEMENT> add(Class<COLLECTION> collectionClass,Collection<ELEMENT> collection,@SuppressWarnings("unchecked") ELEMENT...elements){
		return add(collectionClass,collection,Boolean.TRUE,get(elements));
	}
	
	public <ELEMENT> Collection<ELEMENT> add(Collection<ELEMENT> collection,Boolean append,@SuppressWarnings("unchecked") ELEMENT...elements){
		return add(collection,append,get(elements));
	}
	
	public <ELEMENT> Collection<ELEMENT> add(Collection<ELEMENT> collection,@SuppressWarnings("unchecked") ELEMENT...elements){
		return add(collection,Boolean.TRUE,get(elements));
	}
		
	public <ELEMENT> Boolean contains(Collection<ELEMENT> collection1,Collection<ELEMENT> collection2){
		if(collection1==null)
			if(collection2==null)
				return Boolean.TRUE;//TODO is it correct ???
			else
				return Boolean.FALSE;
		else
			if(collection2==null)
				return Boolean.TRUE;
			else
				return collection1.containsAll(collection2);
	}
	
	@SuppressWarnings("unchecked")
	public <ELEMENT> Boolean contains(Collection<ELEMENT> collection1,ELEMENT...elements){
		return contains(collection1, (Collection<ELEMENT>) Arrays.asList(elements));
	}
	
	public <ELEMENT> Boolean equals(Collection<ELEMENT> collection1,Collection<ELEMENT> collection2){
		if(collection1==null)
			if(collection2==null)
				return Boolean.TRUE;//TODO is it correct ???
			else
				return Boolean.FALSE;
		else
			if(collection2==null)
				return Boolean.FALSE;
			else{
				Collection<ELEMENT> c1 = new ArrayList<>(collection2);
				Collection<ELEMENT> c2 = new ArrayList<>(collection1);
				c1.removeAll(collection1);
				c2.removeAll(collection2);
				return c1.isEmpty() && c2.isEmpty();
			}
	}
	
	public Boolean isEmpty(Collection<?> collection){
		return collection==null || collection.isEmpty();
	}

	public String concatenate(Collection<?> collection, String separator) {
		if(isEmpty(collection))
			return Constant.EMPTY_STRING;
		return StringUtils.join(collection,separator);
	}
	
	@SuppressWarnings("unchecked")
	public <ELEMENT> ELEMENT[] getArray(Collection<ELEMENT> collection){
		return (ELEMENT[]) (collection == null ? null : collection.toArray());
	}
	
	public Integer getSize(Collection<?> collection) {
		if(isEmpty(collection))
			return 0;
		return collection.size();
	}
	
	public Boolean isBlank(Collection<?> collection){
		for(Object object : collection){
			if(object instanceof String){
				if( !StringHelper.getInstance().isBlank( (String)object) )
					return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	public <T> T getFirst(Collection<T> collection){
		if(isEmpty(collection))
			return null;
		return collection.iterator().next();
	}
	
	public void clear(Collection<?> collection){
		if(collection!=null)
			collection.clear();
	}
	
	/**/
	
	public static interface Filter<TYPE>  extends org.cyk.utility.common.Action<Collection<TYPE>, Collection<TYPE>>{
		
		public static class Adapter<TYPE> extends org.cyk.utility.common.Action.Adapter.Default<Collection<TYPE>, Collection<TYPE>> implements Filter<TYPE>,Serializable {
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings("unchecked")
			public Adapter(Collection<TYPE> input) {
				super("filter",(Class<Collection<TYPE>>) ClassHelper.getInstance().getByName(Collection.class), input
						, (Class<Collection<TYPE>>) ClassHelper.getInstance().getByName(Collection.class));
			}
			
			public static class Default<TYPE> extends Filter.Adapter<TYPE> implements Serializable {
				private static final long serialVersionUID = 1L;
				
				public Default(Collection<TYPE> collection) {
					super(collection);
				}	
				
				@Override
				protected Collection<TYPE> __execute__() {
					String fieldName = (String) getProperty(PROPERTY_NAME_FIELD_NAME);
					Object fieldValue = getProperty(PROPERTY_NAME_FIELD_VALUE);
					Collection<TYPE> input = getInput();
					Collection<TYPE> collection = null;
					for(TYPE instance : input){
						Object instanceFieldValue = FieldHelper.getInstance().read(instance, fieldName);
						if((fieldValue==null && instanceFieldValue==null) || fieldValue.equals(instanceFieldValue)){
							if(collection==null)
								collection = new ArrayList<>();
							collection.add(instance);
						}
					}
					return collection;
				}
				
			}
			
		}
				
	}
}
