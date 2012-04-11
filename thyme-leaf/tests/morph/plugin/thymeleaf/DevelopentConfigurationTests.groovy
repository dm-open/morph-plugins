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
@ActiveProfiles("development")
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes=[ThymeLeafPlugin.class])
class DevelopentConfigurationTests {
	@Autowired
	TemplateEngine templateEngine
	
	@Test
	void 'in development profile caching should be turned off'() {
		def resolver = templateEngine.templateResolvers.toList().first()
		
		resolver.initialize()
		
		assert resolver.cacheable == false
	}
}