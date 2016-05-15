package ua.tumakha.yuriy.twitter.timeline.web.socket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.tumakha.yuriy.twitter.timeline.api.TwitterStream;
import ua.tumakha.yuriy.twitter.timeline.api.impl.TwitterStreamImpl;

import javax.annotation.PreDestroy;

/**
 * @author Yuriy Tumakha
 */
@WebSocket
@Component
public class NewTweetsWebSocket {

    private static final Logger LOG = LoggerFactory.getLogger(NewTweetsWebSocket.class);

    @Autowired
    private TwitterStream twitterStream;

    private Session session;

    // called when the socket connection with the browser is established
    @OnWebSocketConnect
    public void handleConnect(Session session) {
        this.session = session;
        TwitterStreamImpl.addListenerSocket(this);
    }

    // called when the connection closed
    @OnWebSocketClose
    public void handleClose(int statusCode, String reason) {
        LOG.debug("Connection closed with statusCode=" + statusCode + ", reason=" + reason);
        TwitterStreamImpl.removeListenerSocket(this);
    }

    // called when a message received from the browser
    @OnWebSocketMessage
    public void handleMessage(String message) {
        LOG.debug("Server got message: " + message);
    }

    // called in case of an error
    @OnWebSocketError
    public void handleError(Throwable error) {
        LOG.error("WebSocketError: " + error.getMessage(), error);
    }

    // sends message to browser
    public void send(String message) {
        if (isActive()) {
            session.getRemote().sendStringByFuture(message);
        }
    }

    // closes the socket
    @PreDestroy
    private void stop() {
        try {
            if (isActive()) {
                session.disconnect();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public boolean isActive() {
        return session != null && session.isOpen();
    }

}
