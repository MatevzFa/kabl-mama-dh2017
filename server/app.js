
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

/* GET route for sending true/false based on latest tab POST */
app.get('/existbadtabs', (req, res) => {
    if (existBadTabs) {
        res.send('true');
    } else {
        res.send('false');
    }
})

/* RegEx for matching bad URLs */
var urlPattern = '(http|https)\://.*?\.(' + badUrls + ')/';

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

var regex = new RegExp(urlPattern);
/* POST route for recieving data about opened tabs */
app.post('/openedtabs', (req, res) => {
    var openTabs = req.body.tabs;
    var badTabs = openTabs.filter((tab) => {
        return regex.exec(tab) !== null;
    });
    existBadTabs = badTabs.length > 0;
    console.log(existBadTabs);
    res.end();
});

app.listen(process.env.PORT, () => {
    console.log('Server up @ %d', process.env.PORT);
});