package org.eclipse.epsilon.emc.yaml;

import java.io.File;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.emc.emf.EmfModel;

public class Test {

	public static void main(String[] args) throws Exception {    	
		//yamlModelConfiguration();	
		emf2yamlTransformation();
	}
	
	public static void yamlModelConfiguration() throws Exception {
		YamlModel model = new YamlModel();
		model.setName("M");
		model.setFile(new File("C:\\Workspaces\\emc-yaml\\src\\yaml\\sample.yaml"));
		model.load();
		model.setStoredOnDisposal(true);
		
		EolModule module = new EolModule();
		module.parse(new File("C:\\Workspaces\\emc-yaml\\src\\eol\\test.eol"));
		module.getContext().getModelRepository().addModel(model);
		module.getContext().setModule(module);
		module.execute();
    	module.getContext().getModelRepository().dispose();
   	}
	
	public static void emf2yamlTransformation() throws Exception {
		EmfModel sourceModel = new EmfModel();
		sourceModel.setName("Emf");
		sourceModel.setModelFileUri(URI.createFileURI("C:\\Workspaces\\emc-yaml\\src\\emf\\cloudmanager_connector.ontap"));
		sourceModel.setMetamodelFileUri(URI.createFileURI("C:\\Workspaces\\emc-yaml\\src\\emf\\oNTAP.ecore"));
		sourceModel.setReadOnLoad(true);
		sourceModel.setStoredOnDisposal(true);
		sourceModel.load();
		
		YamlModel targetModel = new YamlModel();
		targetModel.setName("Yaml");
		targetModel.setFile(new File("C:\\Workspaces\\emc-yaml\\src\\yaml\\transformation.yaml"));
		targetModel.setStoredOnDisposal(true);
		targetModel.load();
		
		EtlModule module = new EtlModule();
		module.parse(new File("C:\\Workspaces\\emc-yaml\\src\\etl\\emf2yaml.etl"));
		module.getContext().getModelRepository().addModel(sourceModel);
		module.getContext().getModelRepository().addModel(targetModel);
		module.getContext().setModule(module);
		module.execute();
    	module.getContext().getModelRepository().dispose();
	}
	
}