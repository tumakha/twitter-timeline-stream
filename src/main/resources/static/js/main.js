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
        $('#tweet' + json.statusId).remove();
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
    $.get( "api/statuses")
        .done(function( data ) {
            $.each(data, function(i, status) {
                $("#tweets-list").append(tweetHtml(status));
            });
        })
        .fail(function( error ) {
            console.log("AJAX ERROR: ", error );
        });
});

function tweetHtml(status) {
    return '<li id="tweet' + status.id + '" class="ui-btn">' +
    '<p>' + status.text + '</p>' +
    '<p class="ui-li-aside"><div class="Twitter-Icon" role="presentation"></div></p>' +
    '</li>';
}

