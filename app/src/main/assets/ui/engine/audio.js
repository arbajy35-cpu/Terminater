// ----------------- AUDIO MODULE -----------------
let audioCtx = new (window.AudioContext || window.webkitAudioContext)();

// Resume Audio on first interaction (mobile fix)
document.addEventListener("click", () => {
  if (audioCtx.state === "suspended") audioCtx.resume();
}, { once: true });

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

window.playTone = playTone;
