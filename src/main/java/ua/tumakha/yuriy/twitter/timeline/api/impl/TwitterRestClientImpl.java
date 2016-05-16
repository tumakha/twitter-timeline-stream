package ua.tumakha.yuriy.twitter.timeline.api.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import ua.tumakha.yuriy.twitter.timeline.api.TwitterClient;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Component
public class TwitterRestClientImpl implements TwitterClient {

    @Value("${twitter.api.consumer.key}")
    private String consumerKey;

    @Value("${twitter.api.consumer.secret}")
    private String consumerSecret;

    @Value("${twitter.api.access.token}")
    private String token;

    @Value("${twitter.api.access.secret}")
    private String secret;

    private TwitterFactory twitterFactory;

    @PostConstruct
    public void initConnection() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setTrimUserEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(token)
                .setOAuthAccessTokenSecret(secret);
        twitterFactory = new TwitterFactory(cb.build());
    }

    public List<Status> getUserTimeline(long userId, Paging paging) {

        Twitter twitter = twitterFactory.getInstance();
        try {
            return twitter.getUserTimeline(userId, paging);
        } catch (TwitterException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Status> getUserTimeline(String screenName, Paging paging) {
        Twitter twitter = twitterFactory.getInstance();
        try {
            return twitter.getUserTimeline(screenName, paging);
        } catch (TwitterException ex) {
            throw new RuntimeException(ex);
        }
    }

}
