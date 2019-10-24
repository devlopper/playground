package org.cyk.utility.__kernel__.collection;

import java.util.Collection;

import org.cyk.utility.__kernel__.AbstractAsFunctionParameter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class CollectionsAsFunctionParameter extends AbstractAsFunctionParameter<Collection<?>> {

	private Class<?> elementClass;
	private Collection<CollectionAsFunctionParameter> collectionAsFunctionParameters;
}
