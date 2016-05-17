function getHost() {
    var url = window.location.href;
    var arr = url.split("/");
    return arr[2];
}

// establish the communication channel over a websocket
var ws = new WebSocket("ws://" + getHost() + "/tweets-stream");

// called when socket connection established
ws.onopen = function() {
    console.log("Connected to websocket.")
};

// called when a message received from server
ws.onmessage = function (evt) {
    console.log("PUSH from server: " + evt.data)
    var json = $.parseJSON(evt.data);
    if (json.action !== undefined && json.action !== null && json.action === 'onDeletionNotice') {
        $('#tweets-list #' + json.statusId).remove();
    } else {
        // getting new tweet
        $("#tweets-list").prepend(tweetHtml(json));
    }
};

// called when socket connection closed
ws.onclose = function() {
    console.log("Disconnected from websocket.")
};

// called in case of an error
ws.onerror = function(err) {
    console.log("WebSocket ERROR: ", err )
};

// sends msg to the server over websocket
function sendToServer(msg) {
    ws.send(msg);
}

$(function() {
    getStatuses(-1);
    $('.timeline-LoadMore-button').hide();
    $('.timeline-LoadMore-endOfTimelineMessage').hide();
    $('.timeline-LoadMore').show();
});

function getStatuses(maxId) {
    var url = "api/statuses";
    if (maxId != -1) {
        url += '?maxId=' + maxId;
    }
    $.get( url )
        .done(function( data ) {
            $.each(data, function(i, status) {
                $("#tweets-list").append(tweetHtml(status));
            });
            prepareLoadMore(data.length > 99, maxId == -1);
        })
        .fail(function( error ) {
            console.log("AJAX ERROR: ", error );
        });
}

function prepareLoadMore(isMore, isFirstPage) {
    if (isMore) {
        $('.timeline-LoadMore-button').show();
        $('.timeline-LoadMore-endOfTimelineMessage').hide();
    } else if (!isFirstPage) {
        $('.timeline-LoadMore-button').hide();
        $('.timeline-LoadMore-endOfTimelineMessage').show();
    }
}

function loadMoreTweets() {
    $('.timeline-LoadMore-button').hide();
    $('.timeline-LoadMore-button').blur();

    var maxId = $('#tweets-list li:last').attr('id');
    console.log("maxId=" + maxId);
    getStatuses(maxId);
}

function tweetHtml(status) {
    return '<li id="' + status.id + '" class="ui-btn">' +
        '<div class="Twitter-Icon"></div>' +
        '<p>' + status.text + '</p>' +
        '<div class="Twitter-Time">' + status.dateStr + '</div>' +
    '</li>';
}

