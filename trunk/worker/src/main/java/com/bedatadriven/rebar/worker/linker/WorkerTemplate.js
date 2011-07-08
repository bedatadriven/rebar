// not sure what the stats thing is all about but not
// sure we need in a worker
function $stats(a) {
}

$strongName = '__STRONG_NAME__';


// there are some stray references to browser objects in the GWT
// code base that will not available to us. obviously we don't
// want to emulate the DOM but let's do all we can to get
// non-UI code to run.

navigator = { userAgent: 'gears' };
window = {};
window.location = {};
window.google = google;
$wnd = window;
$doc = {};
$sessionId = 1; // TODO: look this up

$wnd.alert = function(msg) { };

// we also need to emulate the timer API...
$gtimer = google.gears.factory.create('beta.timer');

$wnd.setTimeout = function(a, b) { return $gtimer.setTimeout(a, b); };
$wnd.setInterval = function(a, b) { return $gtimer.setInterval(a, b); };
$wnd.clearTimeout = function(id) { return $gtimer.clearTimeout(id); };
$wnd.clearInterval = function(id) { return $gtimer.clearInterval(id); };

gwtOnLoad(null, '__MODULE_NAME__', '');