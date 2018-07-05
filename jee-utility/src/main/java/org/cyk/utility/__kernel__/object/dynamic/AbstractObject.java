package org.cyk.utility.__kernel__.object.dynamic;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.cyk.utility.__kernel__.properties.Properties;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class AbstractObject extends org.cyk.utility.__kernel__.object.AbstractObject implements Objectable, Serializable {
	private static final long serialVersionUID = 1L;

	private Properties properties;
	
	@PostConstruct
	private void listenPostConstruct(){
		__listenPostConstruct__();
	}
	
	protected void __listenPostConstruct__(){}
	
	@Override
	public Properties getProperties() {
		if(properties == null)
			properties = new Properties();
		return properties;
	}
	
	@Override
	public Objectable setProperties(Properties properties) {
		this.properties = properties;
		return this;
	}
	
	@Override
	public Object getParent() {
		return getProperties().getParent();
	}
	
	@Override
	public Objectable setParent(Object parent) {
		getProperties().setParent(parent);
		return this;
	}
	
	@Override
	public String getIdentifier() {
		return (String) getProperties().getIdentifier();
	}
	
	@Override
	public Objectable setIdentifier(Object identifier) {
		getProperties().setIdentifier(identifier);
		return this;
	}
}
