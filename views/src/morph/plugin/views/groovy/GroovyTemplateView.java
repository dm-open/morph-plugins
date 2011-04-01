package morph.plugin.views.groovy;

import groovy.lang.Writable;
import groovy.text.Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import morph.annotations.TagLib;

import org.springframework.web.servlet.View;

public class GroovyTemplateView implements View {
	private Template template;
	private List<Object> tagLibs;

	public GroovyTemplateView(Template template, List<Object> tagLibs) {
		this.template = template;
		this.tagLibs = tagLibs;
	}

	@Override
	public String getContentType() {
		return "text/html";
	}

	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> mergedModel = new HashMap<String, Object>();
		if (model != null) {
			mergedModel.putAll(model);
		}

		for (Object tagLib : tagLibs) {
			if (tagLib.getClass().isAnnotationPresent(TagLib.class)) {
				TagLib annotation = tagLib.getClass().getAnnotation(TagLib.class);
				mergedModel.put(annotation.prefix(), tagLib);
			}
		}

		Writable result = template.make(mergedModel);

		result.writeTo(response.getWriter());
	}
}