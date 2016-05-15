/**
 * @author Yuriy Tumakha
 */
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FilterStreamExample {

    private static final String consumerKey = "Q6bAGsowqfNqc0vHDRaoFAA7w";
    private static final String consumerSecret = "OgoIBKa5jc8Yq7exmpUW6poHtnyJY4MieNsISykgku5SlMeXNo";
    private static final String token = "3259821471-wqeTY9xEsli3cCfyRZgPFRtKoIchmazUKUCu9kG";
    private static final String secret = "vywHUS9FISy9Q4QrBd6KlFy2R0w4syzRefaRFBW6BFO1a";

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        List<Long> followings = Lists.newArrayList(50998548L);
        endpoint.followings(followings);


        // addListenerSocket some track terms
        //endpoint.trackTerms(Lists.newArrayList("twitterapi", "#yolo"));

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        // Authentication auth = new BasicAuth(username, password);

        // Create a new BasicClient. By default gzip is enabled.
        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Establish a connection
        client.connect();

        // Do whatever needs to be done with messages
        for (int msgRead = 0; msgRead < 1000; msgRead++) {
            String msg = queue.take();
            System.out.println(msg);
        }

        client.stop();

    }

}