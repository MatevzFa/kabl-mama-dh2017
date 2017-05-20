
chrome.tabs.onUpdated.addListener((tabId, changeInfo, tab) => {
    if (changeInfo.status === 'complete') {
        chrome.tabs.query({}, (tabs)=> {
            for (let i = 0; i < tabs.length; i++)
                console.log('%d: %s', i+1, tabs[i].url);
        });
    }
});

const a = 500;