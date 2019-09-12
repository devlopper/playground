package org.cyk.utility.clazz;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.constant.ConstantString;
import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.helper.AbstractHelper;
import org.cyk.utility.method.MethodHelper;
import org.cyk.utility.number.NumberHelper;
import org.cyk.utility.string.StringHelper;
import org.cyk.utility.string.StringHelperImpl;

@ApplicationScoped
public class ClassHelperImpl extends AbstractHelper implements ClassHelper , Serializable {
	private static final long serialVersionUID = 1L;

	private static ClassHelper INSTANCE;
	public static ClassHelper getInstance(Boolean isNew) {
		//if(INSTANCE == null || Boolean.TRUE.equals(isNew))
			INSTANCE =  DependencyInjection.inject(ClassHelper.class);
		return INSTANCE;
	}
	public static ClassHelper getInstance() {
		return getInstance(null);
	}
	
	private static final Map<Class<?>,Map<Integer,Class<?>>> CLASSES_PARAMETERS_MAP = new HashMap<>();
	private static final Map<String,Class<?>> NAMES_CLASSES_MAP = new HashMap<>();
	
	@Override
	public Boolean isInstanceOf(Class<?> aClass, Class<?> baseClass) {
		if (aClass == null || baseClass == null)
			return Boolean.FALSE;
		return baseClass.isAssignableFrom(aClass);
	}
	
	@Override
	public Boolean isInstanceOfCollection(Class<?> aClass) {
		return isInstanceOf(aClass, Collection.class);
	}
	
	@Override
	public Boolean isInstanceOfNumber(Class<?> aClass) {
		return isInstanceOf(getWrapper(aClass),Number.class);
	}
	
	@Override
	public Boolean isNumberOrStringOrEnum(Class<?> aClass) {
		return isInstanceOfNumber(aClass) || isString(aClass) || isEnum(aClass);
	}

	@Override
	public <T> T instanciate(Class<T> aClass, Object[] constructorParameters) {
		if(constructorParameters == null)
			return instanciateOne(aClass);
		Class<?>[] classes = new Class[constructorParameters.length / 2];
		Object[] arguments = new Object[constructorParameters.length / 2];
		int j = 0;
		for (int i = 0; i < constructorParameters.length; i = i + 2) {
			classes[j] = (Class<?>) constructorParameters[i];
			arguments[j++] = constructorParameters[i + 1];
		}
		try {
			Constructor<T> constructor = __inject__(MethodHelper.class).getConstructor(aClass, classes);
			if (constructor == null) {
				//TODO log error
				//logError("no constructor found in class % with parameters %", aClass, StringUtils.join(classes, ","));
				return null;
			}
			return constructor.newInstance(arguments);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <T> T instanciateOne(Class<T> aClass) {
		try {
			return aClass == null ? null : (aClass.isInterface() ? __inject__(aClass) : aClass.newInstance());
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	@Override
	public <T> Collection<T> instanciate(Class<T> aClass, Integer count) {
		Collection<T> collection = null;
		if(DependencyInjection.inject(NumberHelper.class).isGreaterThanZero(count)){
			collection = new ArrayList<T>();
			for(Integer index = 0; index < count; index++)
				collection.add(instanciateOne(aClass));
		}
		return collection;
	}

	@Override
	public <T> T instanciate(Class<T> aClass) {
		return instanciateOne(aClass);
	}
	
	@Override
	public Boolean areEqual(Class<?> class1, Class<?> class2,Class<?>...classes) {
		if(class1 == null || class2 == null)
			return null;
		return class1.equals(class2);
	}
	
	@Override
	public Class<?> getWrapper(Class<?> aClass) {
		return ClassUtils.primitiveToWrapper(aClass);
	}
	
	@Override
	public Boolean isPrimitive(Class<?> aClass) {
		return aClass.isPrimitive();
	}
	
	@Override
	public Boolean isEnum(Class<?> aClass) {
		return aClass.isEnum();
	}
	
	@Override
	public Boolean isBelongsToJavaPackages(Class<?> aClass) {
		return aClass.isArray() || StringUtils.startsWithAny(aClass.getName(), "java.","javax.");
	}

	@Override
	public Boolean isString(Class<?> aClass) {
		return areEqual(java.lang.String.class, aClass);
	}

	@Override
	public Boolean isDate(Class<?> aClass) {
		return areEqual(java.util.Date.class, aClass);
	}

	@Override
	public Boolean isBoolean(Class<?> aClass) {
		return areEqual(Boolean.class, getWrapper(aClass));
	}
	
	@Override
	public Collection<Class<?>> getInterfaces(Class<?> aClass) {
		return ClassUtils.getAllInterfaces(aClass);
	}
	
	@Override
	public Class<?> getInterfaceByClassSimpleName(Class<?> aClass) {
		Class<?> result = null;
		if(aClass == null){
			
		}else {
			Collection<Class<?>> interfaces = getInterfaces(aClass);
			if(__inject__(CollectionHelper.class).isNotEmpty(interfaces))
				for(Class<?> index : interfaces)
					if(index!=null)
						if(getImplementationClassSimpleName(index).equals(aClass.getSimpleName())){
							result = index;	
							break;
						}
		}
		return result;
	}
	
	@Override
	public String getImplementationClassSimpleName(Class<?> interfaceClass) {
		return interfaceClass == null ? null : interfaceClass.getSimpleName()+ConstantString.IMPL;
	}
	
	@Override
	public String getInterfaceSimpleName(Class<?> aClass) {
		return aClass == null ? null : StringUtils.substringBefore(aClass.getSimpleName(),ConstantString.IMPL);
	}
	
	@Override
	public <TYPE> Class<TYPE> getParameterAt(Class<?> aClass, Integer index, Class<TYPE> typeClass) {
		return __getParameterAt__(aClass, index, typeClass);
	}
	
	@Override
	public Class<?> getByName(String name,Boolean isReturnNullIfNotFound) {
		Class<?> clazz = null;
		if(__inject__(StringHelper.class).isBlank(name)) {
			
		}else {
			try {
				clazz = NAMES_CLASSES_MAP.get(name);
				if(clazz == null) {
					clazz = Class.forName(name);
					NAMES_CLASSES_MAP.put(name, clazz);
				}
			} catch (Exception exception) {
				if(exception instanceof ClassNotFoundException && Boolean.TRUE.equals(isReturnNullIfNotFound))
					clazz = null;
				else
					throw new RuntimeException(exception);
				//__inject__(Log.class).executeThrowable(exception);
			}	
		}
		return clazz;
	}
	
	@Override
	public Class<?> getByName(String name) {
		return getByName(name, Boolean.TRUE);
	}
	
	@Override
	public Class<?> getByName(ClassNameBuilder nameBuilder) {
		return nameBuilder == null ? null : getByName(nameBuilder.execute().getOutput());
	}
	
	@Override
	public String getSimpleName(Class<?> aClass) {
		return aClass == null ? null : aClass.getSimpleName();
	}
	
	@Override
	public String getSimpleName(String string) {
		return StringUtils.contains(string, DOT) ? StringUtils.substringAfterLast(string, DOT) : string;
	}
	
	@Override
	public Boolean isInstanceOfOne(Class<?> aClass,Collection<Class<?>> classes) {
		if(classes!=null)
			for(Class<?> index : classes)
				if(isInstanceOf(aClass, index))
					return Boolean.TRUE;
		return null;
	}
	
	@Override
	public Boolean isInstanceOfOne(Class<?> aClass, Class<?>... classes) {
		return isInstanceOfOne(aClass, __inject__(CollectionHelper.class).instanciate(classes));
	}
	
	@Override
	public String buildNameFrom(String simpleName, String packageName, String sourceNode, String sourceLayer,String sourceLayerSub,String destinationNode, String destinationLayer,String destinationLayerSub) {
		String name = StringUtils.replaceOnce(packageName, sourceNode, destinationNode);
		name = StringUtils.replaceOnce(name, sourceLayer, destinationLayer);
		name = StringUtils.replaceOnce(name, sourceLayerSub, destinationLayerSub);
		name = name + simpleName;
		return name;
	}
	
	/**/
	
	private static final String DOT = ".";
	
	/**/
	
	public static <TYPE> Class<TYPE> __getParameterAt__(Class<?> aClass, Integer index, Class<TYPE> typeClass) {
		Class<TYPE> parameter = null;
		if(aClass != null) {
			Map<Integer,Class<?>> map = CLASSES_PARAMETERS_MAP.get(aClass);
			parameter = map == null ? null : (Class<TYPE>) map.get(index);
			if(parameter == null && aClass != null && index != null && typeClass != null && aClass.getGenericSuperclass() instanceof ParameterizedType){
				parameter = (Class<TYPE>) ((ParameterizedType) aClass.getGenericSuperclass()).getActualTypeArguments()[index];
				if(map == null) {
					map = new HashMap<>();
					CLASSES_PARAMETERS_MAP.put(aClass, map);
				}
				map.put(index, parameter);
			}	
		}
		return parameter;
	}
	
	public static <T> T __instanciateOne__(Class<T> klass) {
		try {
			return klass == null ? null : (klass.isInterface() ? __inject__(klass) : klass.newInstance());
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
	
	public static Class<?> __getByName__(String name,Boolean isReturnNullIfNotFound) {
		Class<?> clazz = null;
		if(StringHelperImpl.__isBlank__(name)) {
			
		}else {
			try {
				clazz = NAMES_CLASSES_MAP.get(name);
				if(clazz == null) {
					clazz = Class.forName(name);
					NAMES_CLASSES_MAP.put(name, clazz);
				}
			} catch (Exception exception) {
				if(exception instanceof ClassNotFoundException && Boolean.TRUE.equals(isReturnNullIfNotFound))
					clazz = null;
				else
					throw new RuntimeException(exception);
				//__inject__(Log.class).executeThrowable(exception);
			}	
		}
		return clazz;
	}
	
	public static Class<?> __getByName__(String name) {
		return __getByName__(name, Boolean.TRUE);
	}
	
	public static Class<?> __getByName__(ClassNameBuilder nameBuilder) {
		return nameBuilder == null ? null : __getByName__(nameBuilder.execute().getOutput());
	}
	
	public static Boolean __isInstanceOf__(Class<?> aClass, Class<?> baseClass) {
		if (aClass == null || baseClass == null)
			return Boolean.FALSE;
		return baseClass.isAssignableFrom(aClass);
	}
	
	public static Boolean __isInstanceOfOne__(Class<?> aClass,Collection<Class<?>> classes) {
		if(classes!=null)
			for(Class<?> index : classes)
				if(__isInstanceOf__(aClass, index))
					return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	public static Boolean __isInstanceOfOne__(Class<?> aClass, Class<?>... classes) {
		return __isInstanceOfOne__(aClass, __inject__(CollectionHelper.class).instanciate(classes));
	}

}
