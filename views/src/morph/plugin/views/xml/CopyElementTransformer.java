package morph.plugin.views.xml;

import java.util.Map;

import nu.xom.Element;

public class CopyElementTransformer implements ElementTransformer {
	
	@Override
	public boolean canTransform(Element source) {
		return true;
	}

	@Override
	public Element transform(Element source, Map<String, ?> model) {
		return source;
	}

}
