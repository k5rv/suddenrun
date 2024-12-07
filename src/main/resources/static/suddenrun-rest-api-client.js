//const BASE_URL = "https://suddenrun.com"
const BASE_URL = "http://localhost:8081"
const API_V1_PLAYLISTS = "/api/v1/playlists"
const API_V1_USERS = "/api/v1/users"

function sendRequest(method, url) {
    return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        xhr.open(method, url);
        xhr.onload = function () {
            resolve({
                status: xhr.status,
                message: xhr.responseText
            });
        };
        xhr.onerror = function () {
            reject({
                status: xhr.status,
                message: xhr.responseText
            });
        };
        xhr.send();
    });
}

function getCurrentUser() {
    console.log("Getting current user")
    return sendRequest("GET", BASE_URL + API_V1_USERS + "/current").then((response) => {
        if (response.status >= 200 && response.status < 300) {
            let user = JSON.parse(response.message)
            console.log("Found user [" + user.id + "] with registration status [" + user.isRegistered + "]")
            return user
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function registerUser(userId) {
    console.log("Registering user")
    return sendRequest("POST", BASE_URL + API_V1_USERS + "/" + userId).then((response) => {
        if (response.status === 409) {
            console.log("User already registered")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let user = JSON.parse(response.message)
            console.log("Registered user [" + user.id + "]")
            return user
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function getUserPlaylist(userId) {
    console.log("Getting user [" + userId + "] playlist")
    return sendRequest("GET", BASE_URL + API_V1_USERS + "/" + userId + "/playlists").then((response) => {
        if (response.status === 404) {
            console.log("Playlist doesn't exist")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let playlist = JSON.parse(response.message)
            console.log("Found playlist [" + playlist.id + "]")
            return playlist
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function createPlaylist(userId) {
    console.log("Creating playlist")
    return sendRequest("POST", BASE_URL + API_V1_USERS + "/" + userId + "/playlists").then((response) => {
        if (response.status === 409) {
            console.log("Playlist already exist")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let playlist = JSON.parse(response.message)
            console.log("Created playlist [" + playlist.id + "]")
            return playlist
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function addTracks(playlistId) {
    console.log("Adding tracks to playlist [" + playlistId + "]")
    return sendRequest("PUT", BASE_URL + API_V1_PLAYLISTS + "/" + playlistId + "/tracks").then((response) => {
        if (response.status === 404) {
            console.log("Playlist doesn't exist")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let playlist = JSON.parse(response.message)
            console.log("Added tracks to playlist [" + playlist.id + "]")
            return playlist
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

