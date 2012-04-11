package morph.plugin.views.xml;

import java.util.Map;

import org.springframework.core.Ordered;

import nu.xom.Element;

public interface ElementTransformer extends Ordered {
	boolean canTransform(Element source);
	
	Element transform(Element source, Map<String, ?> model);

}
