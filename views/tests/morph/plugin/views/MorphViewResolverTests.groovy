package morph.plugin.views

import javax.servlet.ServletContext;

import morph.annotations.TagLib;
import morph.plugin.views.MorphViewResolver 
import morph.plugins.PluginManager;

import org.junit.runner.RunWith;
import org.spockframework.runtime.Sputnik;

@RunWith(Sputnik.class)
class MorphViewResolverTests extends spock.lang.Specification {

	def "resolver should return null if view not on disk"() {
		given:
			def resolver = new MorphViewResolver()
			resolver.servletContext = [getRealPath: { path -> 'some/path'} ] as ServletContext

		expect:
			resolver.loadView("view/that/doesnt/exist", null) == null
	}

	def "resolver should return valid view if view is on disk"() {
		given:
			def tempView = File.createTempFile('morph_', '.html')
			def resolver = new MorphViewResolver()
			resolver.servletContext = [getRealPath: { path -> tempView.path} ] as ServletContext

		expect:
			resolver.loadView("view/that/doesnt/exist", null) != null
	}

		def "registerTagLib should register multiple taglibs with different prefies"() {
		given:
			def resolver = new MorphViewResolver()
			
		when: "we register a tag lib"
			resolver.registerTagLib(new FredTagLib())
		
		and: "we register a tag lib with the a different prefix"
			resolver.registerTagLib(new JoeTagLib())

		then: "both taglibs are registered"
			resolver.tagLibs.size() == 2
	}

	def "registerTagLib should replace existing taglib if same prefix as existing taglib"() {
		given:
			def resolver = new MorphViewResolver()
			
		when: "we register a tag lib"
			resolver.registerTagLib(new FredTagLib())
		
		and: "we register a tag lib with the same prefix"
			resolver.registerTagLib(new AlsoFredTagLib())

		then: "the new tag lib has replaced the existing one"
			resolver.tagLibs.size() == 1
			resolver.tagLibs[0].name() == "also-fred"
	}
}

@TagLib(prefix="fred")
class FredTagLib {
	def name() {
		"fred"	
	}
}

@TagLib(prefix="joe")
class JoeTagLib {
	def name() {
		"joe"
	}
}

@TagLib(prefix="fred")
class AlsoFredTagLib {
	def name() {
		"also-fred"
	}
}

