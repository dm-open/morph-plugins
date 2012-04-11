package morph.plugin.thymeleaf

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver

@Profile("development")
@Configuration
class DevelopmentConfiguration {

	@Bean
	ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver()
		resolver.prefix = '/WEB-INF/views/'
		resolver.suffix = '.html'
		resolver.templateMode = 'HTML5'
		resolver.cacheable = false
		resolver
	}
}
