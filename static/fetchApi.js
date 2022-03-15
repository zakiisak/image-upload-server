function getJson(url, onResponse) {
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", url);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == XMLHttpRequest.DONE)
        {
            onResponse(xhttp);
        }
    }
    xhttp.send();
}

function postJson(url, data, onResponse) {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", url);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == XMLHttpRequest.DONE)
        {
            onResponse(xhttp);
        }
    }
    return xhttp.send(JSON.stringify(data));
}