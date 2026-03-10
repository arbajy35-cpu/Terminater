// ---------------- TERMINATER SELF REPAIR ENGINE LOADER ----------------

// Engine modules list
const engineFiles = [
  "engine/audio.js",
  "engine/input.js",
  "engine/buffer.js",
  "engine/command.js",
  "engine/renderer.js",
  "canvas/crt.js",
  "engine/nativeRunner.js",
  "engine/bridge.js",
  "engine/download.js"
];

// Engine status
window.engineStatus = {
  loaded: [],
  failed: [],
  retries: {}
};

// Retry limit
const MAX_RETRY = 3;


// ---------------- GLOBAL ERROR PROTECTION ----------------

window.onerror = function(msg, src, line, col, err){

  console.warn("Engine Runtime Error:", msg, "in", src);

  if(src){
    attemptRepair(src);
  }

  return true; // prevent crash

};


// ---------------- MODULE REPAIR SYSTEM ----------------

function attemptRepair(src){

  if(!window.engineStatus.retries[src]){
    window.engineStatus.retries[src] = 0;
  }

  window.engineStatus.retries[src]++;

  if(window.engineStatus.retries[src] > MAX_RETRY){
    console.warn("Module permanently disabled:", src);
    return;
  }

  console.log("Repairing module:", src);

  const script = document.createElement("script");

  script.src = src + "?repair=" + Date.now();

  script.onload = () => {
    console.log("Repair success:", src);
  };

  script.onerror = () => {
    console.warn("Repair failed:", src);
  };

  document.body.appendChild(script);

}


// ---------------- SAFE SCRIPT LOADER ----------------

function safeLoadScript(src){

  return new Promise((resolve)=>{

    try{

      const script = document.createElement("script");

      script.src = src;

      script.onload = () => {

        console.log("Loaded:", src);

        window.engineStatus.loaded.push(src);

        resolve(true);

      };

      script.onerror = () => {

        console.warn("Failed:", src);

        window.engineStatus.failed.push(src);

        attemptRepair(src);

        resolve(false);

      };

      document.body.appendChild(script);

    }
    catch(e){

      console.error("Loader crash:", src, e);

      window.engineStatus.failed.push(src);

      resolve(false);

    }

  });

}


// ---------------- ENGINE LOADER ----------------

async function loadEngine(){

  console.log("Starting Terminater Engine...");

  for(let i = 0; i < engineFiles.length; i++){

    await safeLoadScript(engineFiles[i]);

  }

  console.log("Terminater Engine Loaded");

  console.log("Loaded Modules:", window.engineStatus.loaded.length);

  console.log("Failed Modules:", window.engineStatus.failed.length);

  console.log("Retries:", window.engineStatus.retries);


  // ---------------- FALLBACK PROTECTION ----------------

  window.playTone = window.playTone || function(){};
  window.addLine = window.addLine || function(){};
  window.executeCommand = window.executeCommand || function(){};
  window.scrollBottom = window.scrollBottom || function(){};


  console.log("Engine Safe Mode Active");

}


// ---------------- ENGINE WATCHDOG ----------------

// agar engine core future me break ho jaye
setInterval(()=>{

  if(!window.engineStatus){

    console.warn("Engine status missing, restarting engine");

    loadEngine();

  }

},5000);


// ---------------- START ENGINE ----------------

loadEngine();
