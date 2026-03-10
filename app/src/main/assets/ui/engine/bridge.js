// ----------------- BRIDGE MODULE -----------------
try{

function runNative(command){

if(
window.AndroidNative &&
typeof window.AndroidNative.runNativeCommand === "function"
){

window.AndroidNative.runNativeCommand(command);

}
else if(
window.AndroidBridge &&
typeof window.AndroidBridge.runCommand === "function"
){

window.AndroidBridge.runCommand(command);

}
else{

if(window.addLine){
addLine("Native bridge not available",false,true);
}

}

}

window.runNative = runNative;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.bridge = true;

console.log("Bridge module ready");

}catch(e){

console.warn("Bridge module crashed", e);

}
