package ua.tumakha.yuriy.twitter.timeline.api.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import ua.tumakha.yuriy.twitter.timeline.api.TwitterStream;
import ua.tumakha.yuriy.twitter.timeline.model.TimelineStatus;
import ua.tumakha.yuriy.twitter.timeline.web.socket.NewTweetsWebSocket;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Yuriy Tumakha
 */
@Component
public class TwitterStreamImpl implements TwitterStream {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamImpl.class);

    @Value("${twitter.api.consumer.key}")
    private String consumerKey;

    @Value("${twitter.api.consumer.secret}")
    private String consumerSecret;

    @Value("${twitter.api.access.token}")
    private String token;

    @Value("${twitter.api.access.secret}")
    private String secret;

    @Value("${twitter.user.id}")
    private Long twitterUserId;

    private static final Set<NewTweetsWebSocket> sockets = new CopyOnWriteArraySet<>();

    private Twitter4jStatusClient t4jClient;

    @PostConstruct
    public void initStream() {

        final StatusListener listener1 = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                sendToSockets(TimelineStatus.valueOf(status));
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                Map<String, String> data = new HashMap<>();
                data.put("action", "onDeletionNotice");
                data.put("statusId", String.valueOf(statusDeletionNotice.getStatusId()));
                sendToSockets(data);
            }

            private void sendToSockets(Object object) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String json = mapper.writeValueAsString(object);
                    sockets.forEach(socket -> socket.send(json));
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }

            @Override
            public void onTrackLimitationNotice(int limit) {
            }

            @Override
            public void onScrubGeo(long user, long upToStatus) {
            }

            @Override
            public void onStallWarning(StallWarning warning) {
            }

            @Override
            public void onException(Exception e) {
            }
        };

        // Create an appropriately sized blocking queue
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(5000);

        // Define our endpoint: By default, delimited=length is set (we need this for our processor)
        // and stall warnings are on.
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        LOG.info("Twitter User ID: {}", twitterUserId);
        List<Long> followings = Lists.newArrayList(twitterUserId);
        endpoint.followings(followings);

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        // Authentication auth = new BasicAuth(username, password);

        // Create a new BasicClient. By default gzip is enabled.
        BasicClient client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Create an executor service which will do the actual work of parsing the incoming messages
        // and calling the listeners on each message
        ExecutorService service = Executors.newSingleThreadExecutor();

        // Wrap our BasicClient with the twitter4j client
        t4jClient = new Twitter4jStatusClient(client, queue, Lists.newArrayList(listener1), service);

        // Establish a connection
        t4jClient.connect();
        t4jClient.process();

        LOG.info("Twitter Stream initialized.");
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        try {
            t4jClient.stop();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void addListenerSocket(NewTweetsWebSocket newTweetsWebSocket) {
        sockets.add(newTweetsWebSocket);
    }

    public static void removeListenerSocket(NewTweetsWebSocket newTweetsWebSocket) {
        sockets.remove(newTweetsWebSocket);
    }

}
