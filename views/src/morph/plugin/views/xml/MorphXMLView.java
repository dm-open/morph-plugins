package morph.plugin.views.xml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.View;

public class MorphXMLView implements View {
	protected final Log logger = LogFactory.getLog(getClass());

	private Resource input;
	private Resource stylesheet;
	private List<ElementTransformer> elementTransformers = new ArrayList<ElementTransformer>();

	public MorphXMLView(Resource input, Resource stylesheet, List<ElementTransformer> elementTransformers) {
		this.input = input;
		this.stylesheet = stylesheet;
		this.elementTransformers = elementTransformers;
	}

	@Override
	public String getContentType() {
		return "text/html";
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Document source = buildSourceDocument();
		Document workingCopy = (Document) source.copy();

		if (anyTransformersDefined()) {
			applyTransformationsTo(workingCopy.getRootElement(), model);
		}

		writeToOutputStream(response, workingCopy);
	}

	private Document buildSourceDocument() throws ParsingException, ValidityException, IOException {
		Builder parser = new Builder();
		return parser.build(input.getInputStream());
	}

	private boolean anyTransformersDefined() {
		return elementTransformers.size() > 0;
	}

	private void writeToOutputStream(HttpServletResponse response, Document workingCopy) throws UnsupportedEncodingException, IOException, ValidityException, ParsingException, XSLException {
		Document transformedDocument = workingCopy;

		if (stylesheetDefined()) {
			Builder parser = new Builder();
	    Document xslt = parser.build(stylesheet.getInputStream());
	    
	    XSLTransform transform = new XSLTransform(xslt);
	    Nodes result = transform.transform(workingCopy);
	    
	    transformedDocument = XSLTransform.toDocument(result);
		}
		
		Serializer serializer = new Serializer(response.getOutputStream(), "UTF8");
		serializer.setIndent(4);
		serializer.write(transformedDocument);
	}

	private boolean stylesheetDefined() {
		return stylesheet != null;
	}

	private void applyTransformationsTo(Element element, Map<String, ?> model) {
		ElementTransformer transformer = findFirstMatchingTransformerFor(element);
		Element transformed = transformer.transform(element, model);

		if (haveChildren(transformed)) {
			applyTransformationsToChildrenOf(transformed, model);
		}
	}

	private ElementTransformer findFirstMatchingTransformerFor(Element element) {
		for (ElementTransformer availableTransformer : elementTransformers) {
			if (availableTransformer.canTransform(element)) {
				if (logger.isDebugEnabled()) {
					logger.info("Transformer [" + availableTransformer.getClass().getName() + "] is transforming element [" + element.getLocalName() + "] with prefix [" + element.getNamespacePrefix() + "]");
				}
				return availableTransformer;
			}
		}

		return null;
	}

	private boolean haveChildren(Element transformed) {
		return transformed.getChildCount() > 0;
	}

	private void applyTransformationsToChildrenOf(Element transformed, Map<String, ?> model) {
		for (int i = 0; i < transformed.getChildCount(); i++) {
			Node child = transformed.getChild(i);

			if (isElement(child)) {
				applyTransformationsTo((Element) child, model);
			}
		}
	}

	private boolean isElement(Node child) {
		return child instanceof Element;
	}
}
