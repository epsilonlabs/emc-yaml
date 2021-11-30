package org.eclipse.epsilon.emc.yaml;

import java.io.File;
import org.eclipse.epsilon.eol.EolModule;

public class Test {

	public static void main(String[] args) throws Exception {
						
		EolModule module = new EolModule();
		module.parse(new File("C:\\Workspaces\\emc-yaml\\src\\eol\\createNodes.eol"));
		
		YamlModel model = new YamlModel();
		model.setName("M");
		model.setFile(new File("C:\\Workspaces\\emc-yaml\\src\\yaml\\emptyLibrary.yaml"));
		model.load();
		
		model.setStoredOnDisposal(true);
		module.getContext().getModelRepository().addModel(model);
		module.getContext().setModule(module);
		module.execute();
    	module.getContext().getModelRepository().dispose();
	}

}