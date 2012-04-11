package morph.plugin


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.thymeleaf.spring3.SpringTemplateEngine
import org.thymeleaf.spring3.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import morph.plugin.thymeleaf.DevelopmentConfiguration;
import morph.plugin.thymeleaf.ProductionConfiguration;
import morph.plugins.Plugin

@Configuration
@Import([DevelopmentConfiguration, ProductionConfiguration])
class ThymeLeafPlugin implements Plugin {
	
	@Autowired 
	ServletContextTemplateResolver templateResolver

	@Override
	String description() {
		'Provides Thyme Leaf Views for Morph'
	}

	@Override
	String name() {
		'Thyme Leaf'
	}

	@Override
	String version() {
		'0.1.0'
	}
	
	@Bean
	def templateEngine() {
		def engine = new SpringTemplateEngine()
		engine.templateResolver = templateResolver
		engine
	}

	@Bean	
	def viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver()
		resolver.templateEngine = templateEngine()
		resolver
	}
}
