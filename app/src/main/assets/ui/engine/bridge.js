// ----------------- BRIDGE MODULE -----------------
try {

function runNative(command){
    if(window.AndroidNative && typeof window.AndroidNative.runCommand === "function"){
        window.AndroidNative.runCommand(command);
    } else if(window.AndroidBridge && typeof window.AndroidBridge.runCommand === "function"){
        window.AndroidBridge.runCommand(command);
    } else {
        if(typeof addLine === "function") addLine("Native bridge not available", false, true);
    }
}

window.runNative = runNative;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.bridge = true;

console.log("Bridge module ready");

} catch(e) {
    console.warn("Bridge module crashed", e);
}
