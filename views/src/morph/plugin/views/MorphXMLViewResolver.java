package morph.plugin.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import morph.plugin.views.xml.ElementTransformer;
import morph.plugin.views.xml.MorphXMLView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.RedirectView;

@Component("morphXMLViewResolver")
public class MorphXMLViewResolver extends AbstractCachingViewResolver implements Ordered {
	public static final String REDIRECT_URL_PREFIX = "redirect:";
	
	private List<ElementTransformer> elementTransformers = new ArrayList<ElementTransformer>();
	
	private int order = Ordered.HIGHEST_PRECEDENCE;
	private Resource stylesheet = new ClassPathResource("morph/plugin/views/xml/html.xslt");

	@Autowired
	public MorphXMLViewResolver(List<ElementTransformer> elementTransformers) {
		OrderComparator.sort(elementTransformers);
		this.elementTransformers = elementTransformers;
	}

	public void setStylesheet(Resource stylesheet) {
		this.stylesheet = stylesheet;
	}
	
	@Override
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	protected View createView(String viewName, Locale locale) throws Exception {
		logger.info("Searching for view " + viewName);
		
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
			return new RedirectView(redirectUrl, true, true);
		}
		
		return super.createView(viewName, locale);
	}
	
	@Override
	public View loadView(String viewName, Locale locale) throws Exception {
		File viewOnDisk = new File(getServletContext().getRealPath("/WEB-INF/views/" + viewName + ".xml"));
		
		if (!viewOnDisk.exists()) {
			logger.info("Not found view " + viewName);
			return null;
		}
		
		MorphXMLView view = new MorphXMLView(new FileSystemResource(viewOnDisk), stylesheet, elementTransformers);
		return view;
	}
}