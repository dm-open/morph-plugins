package morph.support.views.annotation;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Component
public class MorphRequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter {
	private RequestMappingHandlerAdapter adapter;

	@Autowired
	public MorphRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter) {
		this.adapter = adapter;
	}
	
	public RequestMappingHandlerAdapter getWrappedAdapter() {
		return adapter;
	}
	
	@Override
	protected boolean supportsInternal(HandlerMethod handlerMethod) {
		return adapter.supports(handlerMethod);
	}

	@Override
	protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
		Class<?> controller = handlerMethod.getBean().getClass();
		Method action = handlerMethod.getMethod(); 
		
		request.setAttribute("controllerClass", controller.getName());
		request.setAttribute("controllerMethod", action.getName());

		return adapter.handle(request, response, handlerMethod);
	}

	@Override
	protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
		return adapter.getLastModified(request, handlerMethod);
	}
	
}
