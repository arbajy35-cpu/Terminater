// ----------------- TERMINAL SETUP -----------------
const terminal = document.querySelector(".terminal");
const input = document.querySelector("input");

let audioCtx = new (window.AudioContext || window.webkitAudioContext)();
let lastValue = "";
let history = [];
let historyIndex = -1;

/*  Resume Audio (mobile fix) */
document.addEventListener("click", () => {
    if (audioCtx.state === "suspended") audioCtx.resume();
}, { once: true });

/*  Retro Terminal Tone */
function playTone(freq, duration = 0.03, type = "square", volume = 0.06) {
    const osc = audioCtx.createOscillator();
    const gain = audioCtx.createGain();

    osc.type = type;
    osc.frequency.setValueAtTime(freq, audioCtx.currentTime);

    osc.connect(gain);
    gain.connect(audioCtx.destination);

    gain.gain.setValueAtTime(volume, audioCtx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.0001, audioCtx.currentTime + duration);

    osc.start();
    osc.stop(audioCtx.currentTime + duration);
}

/*  Typing & Backspace Sound */
input.addEventListener("input", () => {
    if (input.value.length > lastValue.length) {
        playTone(1350 + Math.random() * 60, 0.02, "square", 0.05);
    } else if (input.value.length < lastValue.length) {
        playTone(750, 0.04, "triangle", 0.07);
    }
    lastValue = input.value;
});

/*  Add Line */
function addLine(text, isCommand = false) {
    const line = document.createElement("div");
    line.className = "line";

    if (isCommand) {
        line.innerHTML = `<span style="color:#00ff88;">${text}</span>`;
    } else {
        line.textContent = text;
    }

    terminal.insertBefore(line, document.querySelector(".input-line"));
}

/*  Output from Native */
function printOutput(text) {
    if (!text) return;
    text.split("\n").forEach(line => {
        if (line.trim() !== "") addLine(line, false);
    });
    scrollBottom();
}

/*  Auto Scroll */
function scrollBottom() {
    terminal.scrollTop = terminal.scrollHeight;
}

// ----------------- GLOBAL -----------------
window.input = input;
window.addLine = addLine;
window.playTone = playTone;
window.printOutput = printOutput;

// ----------------- COMMAND EXECUTION ENGINE -----------------
function executeCommand(command) {

    //  pkg install
    if (command.toLowerCase().startsWith("pkg install ")) {
        const pkgName = command.split(" ")[2];
        if (pkgName && typeof downloadPkg === "function") {
            downloadPkg(pkgName);
            return;
        }
    }

    // 1 Try JS Script First
    if (typeof runScript === "function") {
        const parts = command.split(" ");
        const scriptName = parts[0];
        const args = parts.slice(1);

        const handled = runScript(scriptName, args);

        // If runScript explicitly returns false  fallback to native
        if (handled === false) {
            runNative(command);
        }

    } else {
        // No JS loader  go native
        runNative(command);
    }
}

/*  Native Execution */
function runNative(command) {
    if (window.AndroidNative && typeof window.AndroidNative.runNativeCommand === "function") {
        window.AndroidNative.runNativeCommand(command);
    } 
    else if (window.AndroidBridge && typeof window.AndroidBridge.runCommand === "function") {
        window.AndroidBridge.runCommand(command);
    } 
    else {
        addLine("Native bridge not available", true);
    }
}

// ----------------- KEY LISTENER -----------------
input.addEventListener("keydown", function(e) {

    if (e.key === "Enter") {
        e.preventDefault();

        const command = input.value.trim();
        if (!command) return;

        history.push(command);
        historyIndex = history.length;

        playTone(520, 0.08, "sawtooth", 0.09);
        addLine("~ $ " + command, true);

        executeCommand(command);

        input.value = "";
        lastValue = "";
        scrollBottom();
    }

    //  History
    if (e.key === "ArrowUp") {
        if (historyIndex > 0) {
            historyIndex--;
            input.value = history[historyIndex];
            lastValue = input.value;
        }
    }

    //  History
    if (e.key === "ArrowDown") {
        if (historyIndex < history.length - 1) {
            historyIndex++;
            input.value = history[historyIndex];
            lastValue = input.value;
        } else {
            input.value = "";
            lastValue = "";
        }
    }
});