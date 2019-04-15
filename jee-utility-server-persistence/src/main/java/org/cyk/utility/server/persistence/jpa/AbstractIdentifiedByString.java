package org.cyk.utility.server.persistence.jpa;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.cyk.utility.__kernel__.annotation.Generatable;
import org.cyk.utility.__kernel__.object.__static__.identifiable.AbstractIdentifiedPersistableByString;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true) @MappedSuperclass @Access(AccessType.FIELD)
public abstract class AbstractIdentifiedByString extends AbstractIdentifiedPersistableByString implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Access(AccessType.PROPERTY) @Id @Generatable
	@Override
	public String getIdentifier() {
		return super.getIdentifier();
	}
	
	@Override
	public AbstractIdentifiedByString setIdentifier(String identifier) {
		return (AbstractIdentifiedByString) super.setIdentifier(identifier);
	}
	
}