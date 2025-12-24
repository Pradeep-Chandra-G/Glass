package app.glass.timer.GlassTimer.config;

import app.glass.timer.GlassTimer.handler.CountdownWebSocketHandler;
import app.glass.timer.GlassTimer.handler.TimeWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CountdownWebSocketHandler handler;

    public WebSocketConfig(CountdownWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(handler, "/ws/countdown")
                .setAllowedOrigins("*"); // restrict later
    }
}


