package org.cyk.utility.clazz;

import java.util.Collection;

import org.cyk.utility.helper.Helper;

public interface ClassHelper extends Helper {

	Boolean isInstanceOf(Class<?> aClass, Class<?> baseClass);
	Boolean isInstanceOfCollection(Class<?> aClass);
	Boolean isInstanceOfNumber(Class<?> aClass);
	
	<T> T instanciate(Class<T> aClass, Object[] constructorParameters);
	<T> T instanciateOne(Class<T> aClass);
	<T> Collection<T> instanciate(Class<T> aClass,Integer count);
	<T> T instanciate(Class<T> aClass);

	Boolean areEqual(Class<?> class1, Class<?> class2,Class<?>...classes);

	Class<?> getWrapper(Class<?> aClass);

	Boolean isString(Class<?> aClass);

	Boolean isDate(Class<?> aClass);

	Boolean isBoolean(Class<?> aClass);

	Boolean isPrimitive(Class<?> aClass);
	
	Boolean isEnum(Class<?> aClass);
	
	Boolean isBelongsToJavaPackages(Class<?> aClass);
	
	Boolean isNumberOrStringOrEnum(Class<?> aClass);
	
	Collection<Class<?>> getInterfaces(Class<?> aClass);

	Class<?> getInterfaceByClassSimpleName(Class<?> aClass);
	
	String getImplementationClassSimpleName(Class<?> interfaceClass);
	
	String getInterfaceSimpleName(Class<?> aClass);
	
	<TYPE> Class<TYPE> getParameterAt(Class<?> aClass, Integer index, Class<TYPE> typeClass);
	
	Class<?> getByName(String name,Boolean isReturnNullIfNotFound);
	Class<?> getByName(String name);
	Class<?> getByName(ClassNameBuilder nameBuilder);
	
	String getSimpleName(Class<?> aClass);
	String getSimpleName(String string);
	
	Boolean isInstanceOfOne(Class<?> aClass,Collection<Class<?>> classes);
	Boolean isInstanceOfOne(Class<?> aClass,Class<?>...classes);
	
	/**/
	
	String buildNameFrom(String simpleName,String packageName,String sourceNode,String sourceLayer,String sourceLayerSub,String destinationNode,String destinationLayer,String destinationLayerSub);
	
	/**/

}
