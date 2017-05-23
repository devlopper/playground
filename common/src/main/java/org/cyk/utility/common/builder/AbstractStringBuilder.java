package org.cyk.utility.common.builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.common.AbstractBuilder;
import org.cyk.utility.common.ListenerUtils;
import org.cyk.utility.common.builder.UrlStringBuilder.PathStringBuilder;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractStringBuilder extends AbstractBuilder<String> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String identifier;
	
	@Override
	public String build() {
		if(StringUtils.isBlank(instance))
			return buildWhenBlank();
		return instance;
	}
	
	protected String getIdentifierMapping(){
		return listenerUtils.getString(Listener.COLLECTION, new ListenerUtils.StringMethod<Listener>() {
			@Override
			public String execute(Listener listener) {
				return listener.getIdentifierMapping(identifier);
			}
		});
	}
	
	protected abstract String buildWhenBlank();
	
	protected Map<String,String> getTokenReplacementMap(){
		Map<String,String> tokenReplacementMap=null;
		for(Listener listener : PathStringBuilder.Listener.COLLECTION){
			Map<String, String> v = listener.getTokenReplacementMap();
			if(v!=null)
				tokenReplacementMap = v;
		}
		if(tokenReplacementMap==null)
			tokenReplacementMap = new HashMap<>();
		return tokenReplacementMap;
	}
		
	/**/
	
	public static interface Listener extends AbstractBuilder.Listener<String> {
		
		Collection<Listener> COLLECTION = new ArrayList<>();
		
		String getIdentifierMapping(String identifier);
		String getDefaultIdentifierMapping(String identifier);
		String getIdentifierWhenMappingIsBlank();
		Map<String,String> getTokenReplacementMap();
		String getSeparator();
		
		@Getter @Setter @Accessors(chain=true)
		public static class Adapter extends AbstractBuilder.Listener.Adapter.Default<String> implements Listener,Serializable {
			private static final long serialVersionUID = 1L;
			
			protected String identifierWhenMappingIsBlank;
			protected Map<String,String> tokenReplacementMap;
			
			@Override
			public String getIdentifierMapping(String identifier) {
				return null;
			}
			
			@Override
			public String getDefaultIdentifierMapping(String identifier) {
				return null;
			}
			
			@Override
			public String getSeparator() {
				return null;
			}
			
			public static class Default extends Listener.Adapter implements Serializable {
				private static final long serialVersionUID = 1L;
			
			}
		}
	}
}
