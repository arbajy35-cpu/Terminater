// ----------------- INPUT MODULE -----------------

// Ensure global history array
window.history = window.history || [];
let historyIndex = window.history.length;

// Track last typed value for sound effects
let lastValue = "";

// Attach listener when input element is ready
function attachInputListener() {
    const input = window.input;
    if (!input) return setTimeout(attachInputListener, 50);

    input.addEventListener("keydown", function(e) {

        // ENTER -> send command
        if (e.key === "Enter") {
            e.preventDefault();
            const command = input.value.trim();
            if (!command) return;

            // Save history
            window.history.push(command);
            historyIndex = window.history.length;

            // Typing sound
            if (window.playTone) window.playTone(520, 0.08, "sawtooth", 0.18);

            // Show command in terminal
            if (window.addLine) window.addLine("~ $ " + command, true);

            // Execute command
            if (window.executeCommand) window.executeCommand(command);

            // Clear input
            input.value = "";
            lastValue = "";

            // Scroll terminal
            if (window.scrollBottom) window.scrollBottom();
        }

        // Arrow Up -> previous command
        if (e.key === "ArrowUp") {
            if (historyIndex > 0) {
                historyIndex--;
                input.value = window.history[historyIndex];
                lastValue = input.value;
            }
        }

        // Arrow Down -> next command
        if (e.key === "ArrowDown") {
            if (historyIndex < window.history.length - 1) {
                historyIndex++;
                input.value = window.history[historyIndex];
                lastValue = input.value;
            } else {
                input.value = "";
                lastValue = "";
            }
        }

    });

    // Hook input events for typing/backspace sound
    input.addEventListener("input", () => {
        if (!window.playTone) return;

        if (input.value.length > lastValue.length) {
            // Typing
            window.playTone(1350 + Math.random() * 60, 0.02, "square", 0.12);
        } else if (input.value.length < lastValue.length) {
            // Backspace
            window.playTone(750, 0.04, "triangle", 0.15);
        }

        lastValue = input.value;
    });
}

// Start listener
attachInputListener();
