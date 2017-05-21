
if (!process.env.PORT)
    process.env.PORT = 8080;

/**
 * Init
 */
var express = require('express');
var urlParser = require('url');
var bodyParser = require('body-parser');
var badUrls = require('./bad-urls.json').join('|');

var app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

/**
 * Server
 */
app.get('/', (req, res) => {
    res.send('Hello world!');
});

var existBadTabs = false;

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

/* GET route for sending true/false based on latest tab POST */
app.get('/existbadtabs', (req, res) => {
    if (existBadTabs) {
        res.send('true');
        console.log('bad-tabs-exist ' + existBadTabs);
        taskCompleted = false;
        existBadTabs = false;
    } else {
        res.send('false');
    }
});

app.get('/cancontinue', (req, res) => {
    if (taskCompleted) {
        res.send('continue');
        taskCompleted = false;
    } else {
        res.send('block');
    }
})

/* RegEx for matching bad URLs */
var urlPattern = '(http|https)\://.*?\.?(' + badUrls + ')/';

var regex = new RegExp(urlPattern);
/* POST route for recieving data about opened tabs */
app.post('/openedtabs', (req, res) => {
    var openTabs = req.body.tabs;
    var badTabs = openTabs.filter((tab) => {
        return regex.exec(tab) !== null;
    });
    console.log('Bad: ' + badTabs);
    existBadTabs = badTabs.length > 0;
    console.log(existBadTabs);
    res.end();
});


var taskCompleted = false;
/* POST route for task completed */
app.post('/taskcompleted', (req, res) => {
    taskCompleted = true;
    console.log('Completed task');
    res.end();
});

app.listen(process.env.PORT, () => {
    console.log('Server up @ %d', process.env.PORT);
});