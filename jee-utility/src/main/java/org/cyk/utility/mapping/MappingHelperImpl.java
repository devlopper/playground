package org.cyk.utility.mapping;

import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.helper.AbstractHelper;

@ApplicationScoped
public class MappingHelperImpl extends AbstractHelper implements MappingHelper,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public <SOURCE, DESTINATION> Collection<DESTINATION> getDestinations(Collection<SOURCE> sources,Class<DESTINATION> destinationClass) {
		Collection<DESTINATION> destinations = null;
		if(Boolean.TRUE.equals(__inject__(CollectionHelper.class).isNotEmpty(sources))) {
			Class<SOURCE> sourceClass = (Class<SOURCE>) sources.iterator().next().getClass();
			destinations = __inject__(MapperSourceDestinationGetter.class).setSourceClass(sourceClass).setDestinationClass(destinationClass)
					.execute().getOutput().getDestinations(sources);	
		}
		return destinations;
	}	
	
	@Override
	public <SOURCE, DESTINATION> Collection<SOURCE> getSources(Collection<DESTINATION> destinations,Class<SOURCE> sourceClass) {
		Collection<SOURCE> sources = null;
		if(Boolean.TRUE.equals(__inject__(CollectionHelper.class).isNotEmpty(destinations))) {
			Class<DESTINATION> destinationClass = (Class<DESTINATION>) destinations.iterator().next().getClass();
			sources = __inject__(MapperSourceDestinationGetter.class).setSourceClass(sourceClass).setDestinationClass(destinationClass)
					.execute().getOutput().getSources(destinations);	
		}
		return sources;
	}
	
	@Override
	public <SOURCE, DESTINATION> DESTINATION getDestination(SOURCE source, Class<DESTINATION> destinationClass) {
		DESTINATION destination = null;
		if(source != null) {
			Class<SOURCE> sourceClass = (Class<SOURCE>) source.getClass();
			destination = (DESTINATION) __inject__(MapperSourceDestinationGetter.class).setSourceClass(sourceClass).setDestinationClass(destinationClass)
					.execute().getOutput().getDestination(source);	
		}
		return destination;
	}
	
	@Override
	public <SOURCE, DESTINATION> SOURCE getSource(DESTINATION destination, Class<SOURCE> sourceClass) {
		SOURCE source = null;
		if(destination != null) {
			Class<DESTINATION> destinationClass = (Class<DESTINATION>) destination.getClass();
			source = (SOURCE) __inject__(MapperSourceDestinationGetter.class).setSourceClass(sourceClass).setDestinationClass(destinationClass)
					.execute().getOutput().getSource(destination);	
		}
		return source;
	}
}
