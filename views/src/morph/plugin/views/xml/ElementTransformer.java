package morph.plugin.views.xml;

import java.util.Map;

import nu.xom.Element;

public interface ElementTransformer {
	boolean canTransform(Element source);
	
	Element transform(Element source, Map<String, ?> model);

}
