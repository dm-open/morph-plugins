package morph.plugin

import morph.plugins.Plugin

class ViewsPlugin implements Plugin {

	@Override
	String description() {
		"Provides Groovy Views for Morph"
	}

	@Override
	public String name() {
		"Views"
	}

	@Override
	public String version() {
		"0.4.10"
	}
		
}
