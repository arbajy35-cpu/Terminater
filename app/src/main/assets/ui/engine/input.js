// ----------------- INPUT MODULE -----------------
try{

const input = document.querySelector("input");

let history = [];
let historyIndex = -1;

window.input = input;

input.addEventListener("keydown", function(e){

try{

// typing keys
if(e.key.length === 1){

if(window.playTypingSound)
playTypingSound("type");

}

// backspace
else if(e.key === "Backspace"){

if(window.playTypingSound)
playTypingSound("backspace");

}

// ENTER
else if(e.key === "Enter"){

e.preventDefault();

const command = input.value.trim();
if(!command) return;

history.push(command);
historyIndex = history.length;

if(window.playTypingSound)
playTypingSound("enter");

if(window.addLine)
addLine("~ $ " + command, true);

if(window.executeCommand)
executeCommand(command);

input.value="";

if(window.scrollBottom)
scrollBottom();

}

// HISTORY UP
else if(e.key === "ArrowUp"){

if(historyIndex>0){

historyIndex--;
input.value = history[historyIndex];

}

}

// HISTORY DOWN
else if(e.key === "ArrowDown"){

if(historyIndex < history.length-1){

historyIndex++;
input.value = history[historyIndex];

}else{

input.value="";

}

}

}catch(err){

console.warn("Input handler error",err);

}

});

window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.input = true;

console.log("Input module ready");

}catch(e){

console.warn("Input module crashed",e);
window.input={};

}
