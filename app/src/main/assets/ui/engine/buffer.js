// ----------------- BUFFER MODULE -----------------
try{

const terminal = document.querySelector(".terminal");

function escapeHTML(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

function addLine(text, isCommand = false, isError = false) {

  if (!terminal) return;

  const line = document.createElement("div");
  line.className = "line";

  const safeText = escapeHTML(text);

  if (isCommand) {
    line.innerHTML = `<span style="color:#00ff88;">${safeText}</span>`;
  }
  else if (isError) {
    line.innerHTML = `<span style="color:#ff4444;">${safeText}</span>`;
  }
  else {
    line.innerHTML = `<span style="color:#e8ffe8;">${safeText}</span>`;
  }

  const inputLine = document.querySelector(".input-line");

  if (inputLine) {
    terminal.insertBefore(line, inputLine);
  }
  else {
    terminal.appendChild(line);
  }

  // 🔹 Optional Debug
  console.log("DEBUG JS addLine:", text);

}

// Expose globally
window.addLine = addLine;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.buffer = true;

console.log("Buffer module ready");

}catch(e){

console.warn("Buffer module crashed", e);

}
