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

addLine("Native bridge not available",false,true);

}

}

window.runNative = runNative;
