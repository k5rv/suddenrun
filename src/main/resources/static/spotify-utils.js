function buildUri(playlist) {
    return "spotify:playlist:" + playlist.id
}

function waitCache(ms) {
    return new Promise(function (resolve, reject) {
        try {
            console.log("Waiting for cache to update [" + ms + "] ms");
            setTimeout(() => {
                console.log("Waiting complete");
                resolve();
            }, ms)
        } catch (err) {
            console.log("Error during waiting [" + err + "]")
            reject();
        }
    })
}
