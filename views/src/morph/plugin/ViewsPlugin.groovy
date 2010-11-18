package morph.plugin

import morph.plugin.views.MorphRequestToViewNameTranslator;
import morph.plugin.views.MorphViewResolver;
import morph.plugin.views.annotation.AnnotationMethodHandlerAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class ViewsPlugin {

	@Bean
	def viewNameTranslator() {
		new MorphRequestToViewNameTranslator()
	}
	
	@Bean
	def viewResolver() {
		def resolver = new MorphViewResolver([])
		resolver.cache = false
		resolver
	}
	
	@Bean
	def annotationMethodHandlerAdapter() {
		new AnnotationMethodHandlerAdapter()
	}
}
