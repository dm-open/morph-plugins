package morph.plugin.views;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import morph.plugin.views.xml.CopyElementTransformer;
import morph.plugin.views.xml.ElementTransformer;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.View;

public class MorphXMLView implements View {
	private Resource input;
	private List<ElementTransformer> elementTransformers = new ArrayList<ElementTransformer>() {{
		add(new CopyElementTransformer());
	}};

	public MorphXMLView(Resource input) {
		this.input = input;
	}

	public void setElementTransformers(List<ElementTransformer> elementTransformers) {
		this.elementTransformers = elementTransformers;
	}
	
	@Override
	public String getContentType() {
		return "text/html";
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	  Document source = buildSourceDocument();
	  Document workingCopy = (Document)source.copy();
	  
	  applyTransformationsTo(workingCopy.getRootElement(), model);
	  
	  writeToOutputStream(response, workingCopy);  
	}

	private Document buildSourceDocument() throws ParsingException, ValidityException, IOException {
		Builder parser = new Builder();
	  return parser.build(input.getInputStream());
	}

	private void writeToOutputStream(HttpServletResponse response, Document workingCopy) throws UnsupportedEncodingException, IOException {
		Serializer serializer = new Serializer(response.getOutputStream(), "UTF8");    
	  serializer.setIndent(4);
    serializer.write(workingCopy);
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
				applyTransformationsTo((Element)child, model);
			}
		}
	}

	private boolean isElement(Node child) {
		return child instanceof Element;
	}
}
