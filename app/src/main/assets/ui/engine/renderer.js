function printOutput(text,isCommand=false){

if(!text) return;

text.split("\n").forEach(line=>{

if(line.trim() !== ""){

const isError =
!isCommand &&
(
line.toLowerCase().includes("error") ||
line.toLowerCase().includes("failed") ||
line.toLowerCase().includes("not found")
);

addLine(line,isCommand,isError);

}

});

scrollBottom();

}

function scrollBottom(){
terminal.scrollTop = terminal.scrollHeight;
}

window.printOutput = printOutput;
window.scrollBottom = scrollBottom;
