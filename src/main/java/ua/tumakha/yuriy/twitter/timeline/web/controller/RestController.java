package ua.tumakha.yuriy.twitter.timeline.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import twitter4j.Paging;
import twitter4j.Status;
import ua.tumakha.yuriy.twitter.timeline.api.TwitterClient;
import ua.tumakha.yuriy.twitter.timeline.model.TimelineStatus;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Yuriy Tumakha
 */
@Controller
@RequestMapping("/api")
public class RestController {

    private static final Logger LOG = LoggerFactory.getLogger(RestController.class);
    public static final int MAX_COUNT = 200;
    public static final Paging DEFAULT_PAGING = new Paging(1, MAX_COUNT);

    @Autowired
    private TwitterClient twitterClient;

    @Value("${twitter.user.id}")
    private Long twitterUserId;

    @RequestMapping(value = "/statuses", method = RequestMethod.GET, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TimelineStatus>> statuses(@RequestParam(value="maxId", required = false) Long maxId) {

        Paging paging;
        if (maxId == null) {
            paging = DEFAULT_PAGING;
        } else {
            paging = new Paging();
            paging.setCount(MAX_COUNT);
            paging.setMaxId(maxId - 1);
        }

        List<Status> statuses = twitterClient.getUserTimeline(twitterUserId, paging);
        LOG.debug("Fetched {} statuses", statuses.size());
        List<TimelineStatus> timelineStatuses = statuses.stream().map(status -> TimelineStatus.valueOf(status))
                .collect(Collectors.toList());

        return new ResponseEntity<>(timelineStatuses, HttpStatus.OK);
    }

}
