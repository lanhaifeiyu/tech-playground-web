var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#send").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#content_container").html("");
}

function connect() {
    var socket = new SockJS('http://localhost/sta/ws/v1/endpoint?token=123');//&sta_detail=1
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        //stompClient.subscribe('http://localhost/sta/topic/sta_notice', function (message) {
        stompClient.subscribe('/topic/sta_notice', function (message) {
            console.log('rsp:' + message);
            showContent(message.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendContent() {
    stompClient.send("/app/demo_sta_notice", {}, JSON.stringify({'content': $("#content").val()}));
}

function showContent(message) {
    $("#content_container").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendContent();
    });
});