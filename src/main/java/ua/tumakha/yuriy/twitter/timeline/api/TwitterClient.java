package ua.tumakha.yuriy.twitter.timeline.api;

import twitter4j.Paging;
import twitter4j.Status;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
public interface TwitterClient {

    List<Status>  getUserTimeline(long userId, Paging paging);

    List<Status> getUserTimeline(String screenName, Paging paging);

}
