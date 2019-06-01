package org.cyk.utility.client.controller.component.window;

import java.io.Serializable;

import org.cyk.utility.client.controller.component.ComponentRole;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;
import org.cyk.utility.client.controller.component.input.InputFile;
import org.cyk.utility.client.controller.component.input.InputFileBuilder;
import org.cyk.utility.client.controller.component.view.ViewBuilder;
import org.cyk.utility.client.controller.data.Data;
import org.cyk.utility.client.controller.data.DataGetter;
import org.cyk.utility.client.controller.data.DataMethodsNamesGetter;
import org.cyk.utility.client.controller.data.Form;
import org.cyk.utility.client.controller.data.FormData;
import org.cyk.utility.client.controller.data.Row;
import org.cyk.utility.string.Strings;
import org.cyk.utility.system.action.SystemAction;

public abstract class AbstractWindowContainerManagedWindowBuilderSelectDataImpl extends AbstractWindowContainerManagedWindowBuilderSelectImpl implements WindowContainerManagedWindowBuilderSelectData,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __execute__(WindowBuilder window,SystemAction systemAction,Class<? extends Form> formClass,Class<? extends Row> rowClass) {
		/*
		if(rowClass!=null) {
			Collection<?> objects = getGridObjects();
			if(objects == null)
				objects = __inject__(Controller.class).readMany(systemAction.getEntities().getElementClass());
			
			@SuppressWarnings({ "rawtypes" })
			GridBuilder gridBuilder = __inject__(GridBuilder.class).setRowClass(rowClass).setRowDataClass((Class<? extends Data>) systemAction.getEntities().getElementClass())
				.addObjects((Collection)objects)
				;
			
			Strings columnsFieldNames = getGridColumnsFieldNames();
			if(columnsFieldNames!=null)
				gridBuilder.addColumnsByFieldNames(columnsFieldNames.get());
			
			if(systemAction.getNextAction() == null)
				gridBuilder.getCommandablesColumnBodyView(Boolean.TRUE).addNavigationCommandableBySystemActionClass(SystemActionProcess.class);
			else
				gridBuilder.getCommandablesColumnBodyView(Boolean.TRUE).addNavigationCommandableBySystemAction(systemAction.getNextAction());
			gridBuilder.getRows(Boolean.TRUE).addRowListeners(new WindowContainerManagedWindowBuilderSelectDataRowListenerAdapter().setWindowContainerManagedWindowBuilder(this));
			
			LayoutTypeGrid layoutTypeGrid = __inject__(LayoutTypeGrid.class);
			gridBuilder.getView(Boolean.TRUE).getComponentsBuilder(Boolean.TRUE).getLayout(Boolean.TRUE).setType(layoutTypeGrid);
			layoutTypeGrid.setIsHasHeader(Boolean.TRUE).setIsHasFooter(Boolean.TRUE).setIsHasOrderNumberColumn(Boolean.TRUE).setIsHasCommandablesColumn(Boolean.TRUE);
			
			viewBuilder = __inject__(ViewBuilder.class);
			viewBuilder.getComponentsBuilder(Boolean.TRUE).setIsCreateLayoutItemOnAddComponent(Boolean.TRUE)
			.addComponents(gridBuilder)
			
			;
			
			__execute__(gridBuilder);
			
			
		}else {
			
		}
		*/
		
		if(formClass!=null) {
			Form form = __inject__(formClass);
			if(window.getTitle()!=null)
				form.setTitle(window.getTitle().getValue());
			Data data = __getData__(window, systemAction, formClass, rowClass);
			if(data == null)
				__injectThrowableHelper__().throwRuntimeException("Data is null for system action "+systemAction);
			if(form instanceof FormData<?>) {
				((FormData<Data>)form).setData(data);	
			}
						
			ViewBuilder viewBuilder = getView();
			if(viewBuilder == null) {
				viewBuilder = __inject__(ViewBuilder.class);
				setView(viewBuilder);
				
			}
			viewBuilder.addComponentBuilderByObjectByFieldNames(form, Form.PROPERTY_TITLE).addRoles(ComponentRole.TITLE);
			
			__execute__(form,systemAction,data,viewBuilder);
			
			Strings methodsNames = __inject__(DataMethodsNamesGetter.class).setSystemAction(systemAction).execute().getOutput();
			if(__injectCollectionHelper__().isNotEmpty(methodsNames)) {
				for(String index : methodsNames.get()) {
					//TODO we can write a DataCommandableBuilderGetter
					CommandableBuilder commandable = (CommandableBuilder) viewBuilder.addComponentBuilderByObjectByMethodName(form, index ,systemAction);
					Boolean isHasInputFile = __injectCollectionHelper__().isNotEmpty(viewBuilder.getComponentsBuilder(Boolean.TRUE).getComponents(Boolean.TRUE)
							.getIsInstanceOf(InputFileBuilder.class,InputFile.class));
					commandable.getCommand(Boolean.TRUE).setIsSynchronous(Boolean.TRUE.equals(isHasInputFile));
				}
			}
		}
	}
	
	//protected abstract void __execute__(GridBuilder gridBuilder);
	
	protected Data __getData__(WindowBuilder window,SystemAction systemAction,Class<? extends Form> formClass,Class<? extends Row> rowClass) {
		return __inject__(DataGetter.class).setSystemAction(systemAction).setIsInjectIfNull(Boolean.TRUE).execute().getOutput();
	}
	
	protected void __execute__(Form form,SystemAction systemAction,Data data,ViewBuilder viewBuilder) {
		
	}
	
}
