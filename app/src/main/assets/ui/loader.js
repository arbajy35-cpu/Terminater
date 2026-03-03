// ----------------- LOADER.JS -----------------

/**
 * Terminal → Native bridge runner
 * Sends commands from WebView to Android native
 * Native executes and returns output
 */

function runScript(commandLine) {
    if (!commandLine) return;

    try {
        // Check if Android interface exists
        if (window.AndroidBridge && typeof window.AndroidBridge.runCommand === "function") {
            // Send command to native
            window.AndroidBridge.runCommand(commandLine);
        } else {
            addLine(`Error: Native bridge not available`, true);
        }
    } catch (err) {
        addLine(`Error sending command to native: ${err.message}`, true);
    }
}

// Make global for terminal input
window.runScript = runScript;

/**
 * Called by Android native to print output to terminal
 * @param {string} text - output from native command
 * @param {boolean} isCommand - true if this is a command echo
 */
function printOutput(text, isCommand = false) {
    if (typeof addLine === "function") {
        addLine(text, isCommand);
    } else {
        console.log(text);
    }
}

// Make printOutput global so Android can call it
window.printOutput = printOutput;