let audioCtx = new (window.AudioContext || window.webkitAudioContext)();
let lastValue = "";

/* Resume Audio */
document.addEventListener("click", () => {
if(audioCtx.state === "suspended") audioCtx.resume();
},{once:true});

/* Retro Tone */
function playTone(freq,duration=0.03,type="square",volume=0.06){

const osc = audioCtx.createOscillator();
const gain = audioCtx.createGain();

osc.type = type;
osc.frequency.setValueAtTime(freq,audioCtx.currentTime);

osc.connect(gain);
gain.connect(audioCtx.destination);

gain.gain.setValueAtTime(volume,audioCtx.currentTime);
gain.gain.exponentialRampToValueAtTime(0.0001,audioCtx.currentTime+duration);

osc.start();
osc.stop(audioCtx.currentTime+duration);

}

/* Typing sound */
input.addEventListener("input",()=>{

if(input.value.length > lastValue.length){
playTone(1350 + Math.random()*60,0.02,"square",0.12);
}
else if(input.value.length < lastValue.length){
playTone(750,0.04,"triangle",0.15);
}

lastValue = input.value;

});

window.playTone = playTone;
window.lastValue = lastValue;
