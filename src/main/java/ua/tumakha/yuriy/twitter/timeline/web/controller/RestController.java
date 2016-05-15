package ua.tumakha.yuriy.twitter.timeline.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Yuriy Tumakha
 */
@Controller
@RequestMapping("/api")
public class RestController {

    private static final Logger LOG = LoggerFactory.getLogger(RestController.class);

    @RequestMapping(value = "/statuses", method = RequestMethod.GET, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Status>> statuses() {

        final List<Status> statuses = new ArrayList<>();

        LOG.info("Fetched {} statuses", statuses.size());

        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

}
