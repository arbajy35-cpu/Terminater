// ----------------- AUDIO MODULE -----------------
try{

const audioCtx = new (window.AudioContext || window.webkitAudioContext)();

document.addEventListener("click",()=>{
if(audioCtx.state==="suspended") audioCtx.resume();
},{once:true});

// load sound
async function loadSound(url){

const res = await fetch(url);
const arr = await res.arrayBuffer();
return await audioCtx.decodeAudioData(arr);

}

let typeSound;
let backspaceSound;
let enterSound;

(async()=>{

typeSound = await loadSound("sound/type.wav");
backspaceSound = await loadSound("sound/backspace.wav");
enterSound = await loadSound("sound/enter.wav");

console.log("Keyboard sounds ready");

})();

// play engine
function playBuffer(buffer,volume=1){

if(!buffer) return;

const source = audioCtx.createBufferSource();
const gain = audioCtx.createGain();

source.buffer = buffer;
gain.gain.value = volume;

source.connect(gain);
gain.connect(audioCtx.destination);

source.start(0);

}

function playTypingSound(type){

if(type==="type"){

playBuffer(typeSound,0.7);

}

else if(type==="backspace"){

playBuffer(backspaceSound,0.9);

}

else if(type==="enter"){

playBuffer(enterSound,1);

}

}

window.playTypingSound = playTypingSound;

window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.audio = true;

console.log("Audio module ready");

}catch(e){

console.warn("Audio module crashed",e);
window.playTypingSound=()=>{};

}
