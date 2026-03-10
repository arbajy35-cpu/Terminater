// ----------------- COMMAND MODULE -----------------
try {

function executeCommand(command){
    if(!command) return;

    // pkg install handling
    if(command.toLowerCase().startsWith("pkg install ")){
        const pkgName = command.split(" ")[2];
        if(pkgName && typeof downloadPkg === "function") downloadPkg(pkgName);
        return;
    }

    // script execution
    if(typeof runScript === "function"){
        const parts = command.split(" ");
        const scriptName = parts[0];
        const args = parts.slice(1);

        const handled = runScript(scriptName, args);

        if(handled === false){
            if(typeof addLine === "function") addLine("Error: Script execution failed", false, true);
            if(typeof runNative === "function") runNative(command);
        }

    } else {
        if(typeof runNative === "function") runNative(command);
    }
}

window.executeCommand = executeCommand;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.command = true;

console.log("Command module ready");

} catch(e) {
    console.warn("Command module crashed", e);
}
