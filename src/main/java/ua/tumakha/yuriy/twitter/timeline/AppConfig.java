package ua.tumakha.yuriy.twitter.timeline;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ua.tumakha.yuriy.twitter.timeline.web.socket.NewTweetsWebSocket;

import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Tumakha
 */
@Configuration

public class AppConfig {

    @Autowired
    private Environment env;

    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return container -> container.setPort(env.getProperty("web.port", Integer.class));
    }

    @Bean
    public ServletRegistrationBean newTweetsServletRegistration(){
        return new ServletRegistrationBean(new NewTweetsSocketServlet(), "/tweets-stream");
    }

    public static class NewTweetsSocketServlet extends WebSocketServlet {
        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.getPolicy().setIdleTimeout(TimeUnit.DAYS.toMillis(7));
            factory.register(NewTweetsWebSocket.class);
        }
    }

}
