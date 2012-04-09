package morph.plugin.views.xml


import morph.plugin.views.xml.TagLibElementTransformer
import nu.xom.Attribute
import nu.xom.Element
import nu.xom.Text

import static org.custommonkey.xmlunit.XMLAssert.*

class TagLibElementTransformerTests extends spock.lang.Specification {
	
	def 'should do nothing to source if source element has no namespace'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			def source = new Element("nothing")
			
		when:
			Element result = transformer.transform(source, [:])
	
		then:
			assertXMLEqual(result.toXML(), '<nothing/>')
	}
	
	def 'should do nothing to source if source element has namespace but no taglib found'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			def source = new Element("unknown:nothing", "http://blaah.com")
			
		when:
			Element result = transformer.transform(source, [:])
	
		then:
			assertXMLEqual(result.toXML(), '<nothing xmlns="http://blaah.com"/>')
	}

	def 'should do nothing to source if taglib found but no corresponding method'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			def source = new Element("g:nothing", "http://blaah.com")
			
		when:
			Element result = transformer.transform(source, [:])
	
		then:
			assertXMLEqual(result.toXML(), '<nothing xmlns="http://blaah.com"/>')
	}

	def 'should invoke taglib if found and correct method name'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			def source = new Element("g:greet", "http://blaah.com")
			
		when:
			Element result = transformer.transform(source, [:])
	
		then:
			assertXMLEqual(result.toXML(), '<hello/>')
	}

	def 'should invoke taglib with any attributes passed as map of values'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			Element source = new Element("g:model", "http://blaah.com")
			source.addAttribute new Attribute("param", "value")
			
		when:
			Element result = transformer.transform(source, [:])
	
		then:
			println result.toXML()
			assertXMLEqual(result.toXML(), '<model>param:value</model>')
	}

	def 'should invoke taglib with any model attributes applied'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			Element source = new Element("g:model", "http://blaah.com")
			
		when:
			Element result = transformer.transform(source, [param:'value'])
	
		then:
			println result.toXML()
			assertXMLEqual(result.toXML(), '<model>param:value</model>')
	}

	def 'should invoke taglib with xml attributes overriding model attributes'() {
		given:
			def transformer = new TagLibElementTransformer([g:new TagLib()])
			Element source = new Element("g:model", "http://blaah.com")
			source.addAttribute new Attribute("param", "value")
			
		when:
			Element result = transformer.transform(source, [param:'overriden'])
	
		then:
			println result.toXML()
			assertXMLEqual(result.toXML(), '<model>param:value</model>')
	}
}

class TagLib {
	def greet(attrs, body) {
		new Element("hello")	
	}
	
	def model(attrs, body) {
		def model = new Element("model")
		
		def data = attrs.collect { k, v -> "$k:$v" }.join(',')
		model.appendChild(new Text(data))
		
		model
	}
}