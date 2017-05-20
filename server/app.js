
if (!process.env.PORT)
    process.env.PORT = 8080;

/**
 * Init
 */
const express = require('express');
const urlParser = require('url');
const bodyParser = require('body-parser');
const badUrls = require('./bad-urls.json').join('|');

const app = express();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

/**
 * Server
 */
app.get('/', (req, res) => {
    res.send('Hello world!');
});

let existBadTabs = false;

/* GET route for sending true/false based on latest tab POST */
app.get('/existbadtabs', (req, res) => {
    if (existBadTabs) {
        res.send('true');
    } else {
        res.send('false');
    }
})

/* RegEx for matching bad URLs */
let urlPattern = '(http|https)\://.*?\.(' + badUrls + ')/';

var regex = new RegExp(urlPattern);
/* POST route for recieving data about opened tabs */
app.post('/openedtabs', (req, res) => {
    let openTabs = req.body.tabs;
    let badTabs = openTabs.filter((tab) => {
        return regex.exec(tab) !== null;
    });
    existBadTabs = badTabs.length > 0;
    // console.log(existBadTabs);
    res.end();
});

app.listen(process.env.PORT, () => {
    console.log('Server up @ %d', process.env.PORT);
});