function postSelectedComponent(componentid) {
    var http = new XMLHttpRequest();
    http.open("POST", "selectcomponent.html", true);
    http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    http.send(encodeURI("selectedcompid="+componentid));
}
function showComponent(className, componentid) {
    var rows = document.getElementsByClassName(className);
    var selectedClass = "Component" + componentid;
    for (var i = 0; i < rows.length; ++i) {
        var selected = (rows[i].className.indexOf(selectedClass) > -1);
        rows[i].style.display = selected ? '' : 'none';
    }
}
function selectComponent(className, componentid) {
    showComponent(className, componentid);
    postSelectedComponent(componentid);
}
function localizeUTCTimes(className) {
    var els = document.getElementsByClassName(className);
    for (var i = 0; i < els.length; ++i) {
        els[i].textContent = els[i].textContent.replace(
            /\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d(\.\d+)?Z/g,
            function(s) { return new Date(s).toLocaleString(); });
    }
}