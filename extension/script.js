
const serverURL = 'https://kabl-mama-dh2071-matevzfa.c9users.io';
// const serverURL = 'http://127.0.0.1:8080';

function tabChangeListener(tabId, changeInfo, tab) {
    if (changeInfo.status === 'complete') {
        chrome.tabs.query({}, (tabs) => {
            tabs = tabs.map((t) => { return t.url });
            let xhr = new XMLHttpRequest();

            $.ajax({
                type: 'POST',
                url: serverURL + '/openedtabs',
                data: {
                    tabs: tabs
                }
            });
            for (let i = 0; i < tabs.length; i++)
                console.log('%d: %s', i + 1, tabs[i]);
        });
    }
}

chrome.tabs.onUpdated.addListener(tabChangeListener);
chrome.tabs.onRemoved.addListener((tabId, removeInfo) => {
    console.log(tabId);
    console.log(removeInfo);
})
const a = 500;