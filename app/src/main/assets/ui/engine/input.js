// ----------------- INPUT MODULE -----------------

let history = [];
let historyIndex = 0;
let lastValue = "";

function attachInputListener() {
  const input = document.querySelector("input");
  if (!input) return setTimeout(attachInputListener, 50);

  window.input = input;

  input.addEventListener("keydown", (e) => {

    if (e.key === "Enter") {
      e.preventDefault();

      const command = input.value.trim();
      if (!command) return;

      history.push(command);
      historyIndex = history.length;

      if (window.playTone) window.playTone(520, 0.08, "sawtooth", 0.18);
      if (window.addLine) window.addLine("~ $ " + command, true);
      if (window.executeCommand) window.executeCommand(command);

      input.value = "";
      lastValue = "";

      if (window.scrollBottom) window.scrollBottom();
    }

    if (e.key === "ArrowUp") {
      if (historyIndex > 0) {
        historyIndex--;
        input.value = history[historyIndex];
        lastValue = input.value;
      }
    }

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

  input.addEventListener("input", () => {
    if (!window.playTone) return;

    if (input.value.length > lastValue.length) {
      window.playTone(1350 + Math.random() * 60, 0.02, "square", 0.12);
    } else if (input.value.length < lastValue.length) {
      window.playTone(750, 0.04, "triangle", 0.15);
    }

    lastValue = input.value;
  });
}

document.addEventListener("DOMContentLoaded", attachInputListener);
