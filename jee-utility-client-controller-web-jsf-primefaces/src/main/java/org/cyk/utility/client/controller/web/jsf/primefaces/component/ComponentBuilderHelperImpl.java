package org.cyk.utility.client.controller.web.jsf.primefaces.component;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.clazz.ClassHelper;
import org.cyk.utility.client.controller.component.Component;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.component.input.InputStringLineOne;
import org.cyk.utility.client.controller.component.output.OutputStringMessage;
import org.cyk.utility.client.controller.component.output.OutputStringText;
import org.cyk.utility.client.controller.component.view.View;
import org.cyk.utility.helper.AbstractHelper;

@ApplicationScoped
public class ComponentBuilderHelperImpl extends AbstractHelper implements ComponentBuilderHelper , Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object build(Component component) {
		Object object = null;
		if(component != null) {
			Class<? extends ComponentBuilder<?, Component>> builderClass = __getBuilderClass__(component);
			if(builderClass==null) {
				__logWarning__("No primefaces component builder found for class <<"+component.getClass()+">>");
			}else
				object =  __inject__(builderClass).setModel(component).execute().getOutput();	
		}
		return object;
	}
	
	@SuppressWarnings("unchecked")
	private static <COMPONENT extends Component> Class<? extends ComponentBuilder<?, COMPONENT>> __getBuilderClass__(COMPONENT component) {
		/* Input */
		
		if(component instanceof InputStringLineOne) 
			return (Class<? extends ComponentBuilder<?, COMPONENT>>) __inject__(ClassHelper.class).getByName(InputTextBuilder.class.getName());
		
		/* Output */
		
		if(component instanceof OutputStringText) 
			return (Class<? extends ComponentBuilder<?, COMPONENT>>) __inject__(ClassHelper.class).getByName(OutputTextBuilder.class.getName());
		
		//if(component instanceof OutputStringMessage) 
		//	return (Class<? extends ComponentBuilder<?, COMPONENT>>) __inject__(ClassHelper.class).getByName(MessageBuilder.class.getName());
		
		/* Command */
		
		if(component instanceof Commandable) 
			return (Class<? extends ComponentBuilder<?, COMPONENT>>) __inject__(ClassHelper.class).getByName(CommandButtonBuilder.class.getName());
		
		/* Panel */
		if(component instanceof View) 
			return (Class<? extends ComponentBuilder<?, COMPONENT>>) __inject__(ClassHelper.class).getByName(OutputPanelFromViewBuilder.class.getName());
		
		return null;
	}

}
