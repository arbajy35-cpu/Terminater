// ----------------- NATIVERUNNER MODULE -----------------
try {

    function runScript(commandLine){
        if(!commandLine) return false;

        // -----------------------------
        // STRONG INPUT CLEAN & DEBUG
        // -----------------------------
        commandLine = commandLine.replace(/[\r\n;]/g,"").trim();
        if(!commandLine) return false;

        if(typeof addLine === "function") addLine("DEBUG NativeRunner JS: [" + commandLine + "]", true);

        try {
            if(window.AndroidNative && typeof window.AndroidNative.runScript === "function"){
                window.AndroidNative.runScript(commandLine);
                return true;
            } else if(window.AndroidBridge && typeof window.AndroidBridge.runCommand === "function"){
                window.AndroidBridge.runCommand(commandLine);
                return true;
            } else {
                if(typeof addLine === "function") addLine("Error: Native bridge not available", false, true);
                return false;
            }
        } catch(err) {
            if(typeof addLine === "function") addLine("Error sending command to native: " + err.message, false, true);
            return false;
        }
    }

    window.runScript = runScript;

    // Module ready signal
    window.engineModuleReady = window.engineModuleReady || {};
    window.engineModuleReady.nativeRunner = true;

    console.log("NativeRunner module ready");

} catch(e) {
    console.warn("NativeRunner module crashed", e);
}
