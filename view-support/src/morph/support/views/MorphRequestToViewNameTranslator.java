package morph.support.views;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

@Component("viewNameTranslator")
public class MorphRequestToViewNameTranslator implements RequestToViewNameTranslator {
	public static final String CONTROLLER_NAME_ATTRIBUTE = "controllerClass";
	public static final String CONTROLLER_METHOD_ATTRIBUTE = "controllerMethod";
	
	private String viewNameFormat = "{0}/{1}";

	@Autowired(required=false)
	public void setViewNameFormat(String viewNameFormat) {
		this.viewNameFormat = viewNameFormat;
	}
	
	@Override
	public String getViewName(HttpServletRequest request) throws Exception {
		String controllerClassName = (String) request.getAttribute(CONTROLLER_NAME_ATTRIBUTE);
		String controllerMethodName = (String) request.getAttribute(CONTROLLER_METHOD_ATTRIBUTE);

		if (controllerClassName == null || controllerMethodName == null) {
			return new DefaultRequestToViewNameTranslator().getViewName(request);
		}
		
		String controller = controllerClassName.replace("controllers.", "").replace(".", "/").replace("Controller", "").toLowerCase();
		String method = controllerMethodName.toLowerCase();
		
		return MessageFormat.format(viewNameFormat, controller, method);
	}
}
