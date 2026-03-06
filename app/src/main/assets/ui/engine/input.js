input.addEventListener("keydown",function(e){

if(e.key === "Enter"){

e.preventDefault();

const command = input.value.trim();
if(!command) return;

history.push(command);
historyIndex = history.length;

playTone(520,0.08,"sawtooth",0.18);

addLine("~ $ " + command,true);

executeCommand(command);

input.value = "";
lastValue = "";

scrollBottom();

}

/* ArrowUp */
if(e.key === "ArrowUp"){

if(historyIndex > 0){

historyIndex--;
input.value = history[historyIndex];
lastValue = input.value;

}

}

/* ArrowDown */
if(e.key === "ArrowDown"){

if(historyIndex < history.length-1){

historyIndex++;
input.value = history[historyIndex];
lastValue = input.value;

}
else{

input.value = "";
lastValue = "";

}

}

});
