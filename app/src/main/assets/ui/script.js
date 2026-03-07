const engineFiles = [
  "engine/audio.js",
  "engine/input.js",
  "engine/command.js",
  "engine/renderer.js",
  "canvas/crt.js",
  "engine/nativeRunner.js",
  "engine/bridge.js",
  "engine/download.js"
];

function loadEngine(index = 0) {

  if (index >= engineFiles.length) {
    console.log("Terminater Engine Loaded");
    return;
  }

  const script = document.createElement("script");
  script.src = engineFiles[index];

  script.onload = () => loadEngine(index + 1);

  document.body.appendChild(script);
}

loadEngine();
