let history = [];
let historyIndex = -1;

function escapeHTML(text){

const div = document.createElement("div");
div.textContent = text;
return div.innerHTML;

}

function addLine(text,isCommand=false,isError=false){

const line = document.createElement("div");
line.className = "line";

const safeText = escapeHTML(text);

if(isCommand){
line.innerHTML = `<span style="color:#00ff88;">${safeText}</span>`;
}
else if(isError){
line.innerHTML = `<span style="color:#ff4444;">${safeText}</span>`;
}
else{
line.innerHTML = `<span style="color:#e8ffe8;">${safeText}</span>`;
}

terminal.insertBefore(line,document.querySelector(".input-line"));

}

window.addLine = addLine;
window.history = history;
window.historyIndex = historyIndex;
