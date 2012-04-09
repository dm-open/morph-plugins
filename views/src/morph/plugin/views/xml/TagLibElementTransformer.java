package morph.plugin.views.xml;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;

public class TagLibElementTransformer implements ElementTransformer {
	private Map<String, Object> tagLibs = new HashMap<String, Object>();
	
	public TagLibElementTransformer(Map<String, Object> tagLibs) {
		this.tagLibs = tagLibs;
	}

	@Override
	public boolean canTransform(Element source) {
		return tagLibs.containsKey(source.getNamespacePrefix());
	}

	@Override
	public Element transform(Element source, Map<String, ?> model) {
		if (noNamespacePrefix(source)) {
			return source;
		}

		Object tagLib = lookupTagLibForElement(source);
		
		if (cannotFind(tagLib)) {
			return source;
		}

		Method method = findTagLibMethod(source, tagLib);
		
		if (cannotFind(method)) {
			return source;
		}
		
		return invokeTagLib(source, model, tagLib, method);
	}

	private boolean noNamespacePrefix(Element source) {
		return source.getNamespacePrefix() == "";
	}

	private boolean cannotFind(Object object) {
		return object == null;
	}

	private Method findTagLibMethod(Element source, Object tagLib) {
		for (Method method : tagLib.getClass().getMethods()) {
			if (source.getLocalName().equals(method.getName())) {
				return method;
			}
		}
		return null;
	}

	private Object lookupTagLibForElement(Element source) {
		return tagLibs.get(source.getNamespacePrefix());
	}

	private Element invokeTagLib(Element source, Map<String, ?> model, Object tagLib, Method found) {
		try {
			Map<String, Object> attributes = populateAttributes(source, model);
			
			Element transformed = (Element) found.invoke(tagLib, attributes, source);
			
			if (source.getParent() != null) {
				source.getParent().replaceChild(source, transformed);
			}
			
			return transformed;
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot invoke method [" + source.getLocalName() + "] on taglib [" + tagLib.getClass().getName() + "]", ex);
		}
	}

	private Map<String, Object> populateAttributes(Element source, Map<String, ?> model) {
		Map<String, Object> attributes = populateAttributesFromModel(model);
		
		mergeAttributesFromElement(source, attributes);
		
		return attributes;
	}

	private Map<String, Object> populateAttributesFromModel(Map<String, ?> model) {
		return new HashMap<String, Object>(model);
	}

	private void mergeAttributesFromElement(Element source, Map<String, Object> attributes) {
		for (int i = 0; i < source.getAttributeCount(); i++) {
			Attribute attribute = source.getAttribute(i);
			attributes.put(attribute.getLocalName(), attribute.getValue());
		}
	}
}
