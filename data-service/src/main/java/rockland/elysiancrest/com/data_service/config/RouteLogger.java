//package rockland.elysiancrest.com.data_service.config;
//
//import org.springframework.context.ApplicationEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//
//import java.util.Map;
//
//@Component
//public class RouteLogger implements ApplicationListener<ContextRefreshedEvent> {
//    @Override
//    public void onApplicationEvent(ApplicationEvent event) {
//        RequestMappingHandlerMapping mapping = event.getApplicationContext().getBean(RequestMappingHandlerMapping.class);
//        Map<RequestMappingInfo, HandlerMethod> methods = mapping.getHandlerMethods();
//        methods.forEach((info, method) -> System.out.println(info + " Handled by: " + method));
//    }
//
//
//}
