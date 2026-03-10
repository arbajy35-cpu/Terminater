// ----------------- NATIVERUNNER.JS -----------------
try{

function runScript(commandLine) {

if (!commandLine) return false;

try {

if (window.AndroidBridge && typeof window.AndroidBridge.runCommand === "function") {

window.AndroidBridge.runCommand(commandLine);
return true;

}
else if (window.AndroidNative && typeof window.AndroidNative.runNativeCommand === "function") {

window.AndroidNative.runNativeCommand(commandLine);
return true;

}
else {

if (typeof addLine === "function") {
addLine("Error: Native bridge not available", false, true);
}

return false;

}

}
catch (err) {

if (typeof addLine === "function") {
addLine("Error sending command to native: " + err.message, false, true);
}

return false;

}

}

window.runScript = runScript;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.nativeRunner = true;

console.log("NativeRunner module ready");

}catch(e){

console.warn("NativeRunner module crashed", e);

}
