package org.cyk.utility.common.annotation.user.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD,ElementType.METHOD})
public @interface InputChoice {

	boolean load() default true;

	/**
	 * Set of choices
	 * @return
	 */
	ChoiceSet set() default ChoiceSet.AUTO;
	
	/**/
	
	public enum ChoiceSet{AUTO,YES_NO}
}
