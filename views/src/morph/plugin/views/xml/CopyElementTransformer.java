package morph.plugin.views.xml;

import java.util.Map;

import nu.xom.Element;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class CopyElementTransformer implements ElementTransformer {
	int order = Ordered.LOWEST_PRECEDENCE;
	
	@Override
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public boolean canTransform(Element source) {
		return true;
	}

	@Override
	public Element transform(Element source, Map<String, ?> model) {
		return source;
	}

}
