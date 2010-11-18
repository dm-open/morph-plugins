package morph.plugin.views;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.File;
import java.util.List;
import java.util.Locale;

import morph.plugin.views.groovy.GroovyTemplateView;
import morph.plugin.views.taglib.TagLib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

public class MorphViewResolver extends AbstractCachingViewResolver {
	private SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();
	private List<TagLib> tagLibs;
	
	@Autowired
	public MorphViewResolver(List<TagLib> tagLibs) {
		this.tagLibs = tagLibs;
	}
	
	@Override
	public View loadView(String viewName, Locale locale) throws Exception {
		logger.info("Searching for view " + viewName);
		
		File viewOnDisk = new File(getServletContext().getRealPath("/WEB-INF/views/" + viewName + ".html"));
		
		if (!viewOnDisk.exists()) {
			logger.info("Not found view " + viewName);
			return null;
		}
		
		Template template = templateEngine.createTemplate(viewOnDisk);
	
		return new GroovyTemplateView(template, tagLibs);
	}
}