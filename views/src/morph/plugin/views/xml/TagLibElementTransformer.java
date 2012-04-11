package morph.plugin.views.xml;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import morph.annotations.TagLib;
import nu.xom.Attribute;
import nu.xom.Element;

@Component
public class TagLibElementTransformer implements ElementTransformer, InitializingBean, ApplicationContextAware {
	protected final Log logger = LogFactory.getLog(getClass());

	private Map<String, Object> tagLibs = new HashMap<String, Object>();
	private ApplicationContext applicationContext;
	private int order = Ordered.HIGHEST_PRECEDENCE;
	
	public TagLibElementTransformer() {
		
	}
	
	public TagLibElementTransformer(Map<String, Object> tagLibs) {
		this.tagLibs = tagLibs;
	}
	
	@Override
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public boolean canTransform(Element source) {
		logger.info("Looking for prefix [" + source.getNamespacePrefix() + "] from [" + Integer.toString(tagLibs.size()) + "] taglibs");

		return tagLibs.containsKey(source.getNamespacePrefix());
	}

	@Override
	public Element transform(Element source, Map<String, ?> model) {
		if (noNamespacePrefix(source)) {
			return source;
		}

		Object tagLib = lookupTagLibForElement(source);
		
		if (cannotFind(tagLib)) {
			logger.warn("Cannot find taglib for [" + source.getLocalName() + "]");
			return source;
		}

		Method method = findTagLibMethod(source, tagLib);
		
		if (cannotFind(method)) {
			logger.warn("Cannot find taglib method for [" + source.getLocalName() + "]");
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Searching for taglibs defined in Spring context");
		}
		
		Map<String, Object> definedTagLibs = applicationContext.getBeansWithAnnotation(TagLib.class);
		
		for (Object tagLib : definedTagLibs.values()) {
			String prefix = tagLib.getClass().getAnnotation(TagLib.class).prefix();
			
			if (logger.isDebugEnabled()) {
				logger.debug("Found taglib [" + tagLib.getClass().getName() + "] with prefix [" + prefix + "]");
			}
			tagLibs.put(prefix, tagLib);
		}
	}
}
