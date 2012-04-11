package morph.plugin

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;

import morph.plugin.views.MorphRequestToViewNameTranslator
import morph.plugin.views.MorphViewResolver
import morph.plugin.views.MorphXMLViewResolver;
import morph.plugin.views.annotation.AnnotationMethodHandlerAdapter;
import morph.plugin.views.xml.TagLibElementTransformer;
import morph.plugins.Plugin

@Configuration
@ComponentScan("morph.plugin.views")
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

	@Bean
	AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
		new AnnotationMethodHandlerAdapter()
	}
}
