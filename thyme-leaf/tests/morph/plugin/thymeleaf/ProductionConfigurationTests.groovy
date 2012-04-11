package morph.plugin.thymeleaf

import morph.plugin.ThymeLeafPlugin

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.thymeleaf.TemplateEngine;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("production")
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes=[ThymeLeafPlugin.class])
class ProductionConfigurationTests {
	@Autowired
	TemplateEngine templateEngine
	
	@Test
	void 'in production profile caching should be turned on'() {
		def resolver = templateEngine.templateResolvers.toList().first()
		
		resolver.initialize()
		
		assert resolver.cacheable == true
	}
}