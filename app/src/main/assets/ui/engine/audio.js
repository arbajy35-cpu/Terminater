// ----------------- AUDIO MODULE -----------------
let audioCtx = new (window.AudioContext || window.webkitAudioContext)();
let lastValue = "";

// 🔊 Resume Audio on first interaction (mobile fix)
function resumeAudioContext() {
    if (audioCtx.state === "suspended") audioCtx.resume();
}
document.addEventListener("click", resumeAudioContext);
document.addEventListener("keydown", resumeAudioContext);

// 🔊 Retro Terminal Tone
function playTone(freq, duration = 0.03, type = "square", volume = 0.06) {
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();

    osc.type = type;
    osc.frequency.setValueAtTime(freq, audioCtx.currentTime);

    osc.connect(gain);
    gain.connect(audioCtx.destination);

    gain.gain.setValueAtTime(volume, audioCtx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + duration);

    osc.start();
    osc.stop(audioCtx.currentTime + duration);
}

// 🔊 Hook to terminal input for typing/backspace sounds
function attachTypingSounds() {
    const input = window.input;
    if (!input) {
        setTimeout(attachTypingSounds, 50); // Retry until input exists
        return;
    }

    lastValue = input.value;

    input.addEventListener("input", () => {
        if (input.value.length > lastValue.length) {
            // Typing sound
            playTone(1350 + Math.random() * 60, 0.02, "square", 0.12);
        } else if (input.value.length < lastValue.length) {
            // Backspace sound
            playTone(750, 0.04, "triangle", 0.15);
        }
        lastValue = input.value;
    });
}

// Start waiting for input to attach typing sounds
attachTypingSounds();

// Make globally available
window.playTone = playTone;
window.audioCtx = audioCtx;
