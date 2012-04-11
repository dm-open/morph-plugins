package morph.plugin.views


import morph.plugin.views.xml.CopyElementTransformer
import morph.plugin.views.xml.MorphXMLView;
import morph.plugin.views.xml.TagLibElementTransformer
import nu.xom.Attribute
import nu.xom.Element

import org.custommonkey.xmlunit.XMLAssert
import org.custommonkey.xmlunit.XMLUnit
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockHttpServletResponse

class MorphXMLViewTests extends spock.lang.Specification {
	def setup() {
		XMLUnit.ignoreWhitespace = true
	}
	
	def 'should return input xml if no processors specified'() {
		given:
			def input = new ByteArrayResource("<html><body><h1>test</h1></body></html>".getBytes())
			def response = new MockHttpServletResponse()
			def view = new MorphXMLView(input, null, [])
			
		when:
			view.render([:], null, response)

		then:
			XMLAssert.assertXMLEqual(response.getContentAsString(), '<?xml version="1.0" encoding="UTF8"?><html><body><h1>test</h1></body></html>')
	}

	def 'should process taglib if one registered for element namespace'() {
		given:
			def input = new ByteArrayResource("<html xmlns:g='http://morphframework.org/taglibs/default'><body><g:heading>test</g:heading></body></html>".getBytes())
			def response = new MockHttpServletResponse()
			def view = new MorphXMLView(input, null, [new TagLibElementTransformer([g: new TagLib()]), new CopyElementTransformer()])
			
		when:
			view.render([:], null, response)

		then:
			XMLAssert.assertXMLEqual(response.getContentAsString(), '<?xml version="1.0" encoding="UTF8"?><html><body><h1>test</h1></body></html>')
	}
	
	def 'should process taglib as a container if container attribute set'() {
		given:
			def input = new ByteArrayResource("<html xmlns:is='http://morphframework.org/taglib/is' xmlns:g='http://morphframework.org/taglibs/default'><body><g:wrap><g:heading>test</g:heading></g:wrap></body></html>".getBytes())
			def response = new MockHttpServletResponse()
			def view = new MorphXMLView(input, null, [new TagLibElementTransformer([g: new TagLib()]), new CopyElementTransformer()])
			
		when:
			view.render([:], null, response)
	
		then:
			XMLAssert.assertXMLEqual(response.getContentAsString(), '<?xml version="1.0" encoding="UTF8"?><html><body><div class="wrap"><h1>test</h1></div></body></html>')
	}
	
	def 'should use supplied stylesheet if configured'() {
		given:
			def input = new ByteArrayResource("<html><body><h1>test</h1></body></html>".getBytes())
			def response = new MockHttpServletResponse()
			def view = new MorphXMLView(input, new ClassPathResource("/morph/plugin/views/xml/html.xslt"), [])
			
		when:
			view.render([:], null, response)
	
		then:
			println response.getContentAsString()
			
			XMLAssert.assertXMLEqual(response.getContentAsString(), '<?xml version="1.0" encoding="UTF8"?><html><body><div class="wrap"><h1>test</h1></div></body></html>')
	}
}

class TagLib {

	def wrap(attrs, body) {
		Element result = new Element("div")
		result.addAttribute new Attribute("class", "wrap")

		for (int i = 0; i < body.getChildCount(); i++) {
			result.appendChild(body.getChild(i).copy())
		}
		
		result
	}
	
	def heading(attrs, body) {
		Element result = new Element("h1")
		result.appendChild(body.getValue())
		result
	}
}