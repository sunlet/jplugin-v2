package net.luis.testautosearch;

import java.util.List;

import net.jplugin.core.kernel.api.AbstractPlugin;
import net.jplugin.core.kernel.api.PluginAnnotation;
import net.jplugin.core.kernel.api.PluginError;
import net.jplugin.core.kernel.api.PluginRegistry;

@PluginAnnotation
public class Plugin extends AbstractPlugin {

	public Plugin() {
		System.out.println("constructing.....");
	}
	@Override
	public void init() {
	}

	@Override
	public int getPrivority() {
		return 0;
	}

	@Override
	public void wire(PluginRegistry pluginRegistry, List<PluginError> errorList) {
		System.out.println("wiring....");
		super.wire(pluginRegistry, errorList);
	}
}
