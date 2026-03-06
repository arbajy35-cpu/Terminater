// ---------------- TERMINATER ENGINE LOADER ----------------

const terminal = document.querySelector(".terminal");
const input = document.querySelector("input");

window.terminal = terminal;
window.input = input;

const engineFiles = [
    "engine/audio.js",
    "engine/buffer.js",
    "engine/renderer.js",

    "engine/nativeRunner.js",
    "engine/bridge.js",
    "engine/download.js",

    "engine/command.js",
    "engine/input.js",
    "canvas/crt.js"
];

function loadEngine(index = 0) {
    if (index >= engineFiles.length) {
        console.log("Terminater Engine Loaded ✅");
        return;
    }

    const script = document.createElement("script");
    script.src = engineFiles[index];

    script.onload = () => loadEngine(index + 1);
    script.onerror = () => console.error("Failed to load:", engineFiles[index]);

    document.body.appendChild(script);
}

loadEngine();
