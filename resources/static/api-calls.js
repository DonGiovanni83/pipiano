function send_sound_call(soundName) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/change_sound", true);
    xhr.send(soundName);
}