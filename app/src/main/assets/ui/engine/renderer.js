// ----------------- RENDERER MODULE -----------------
try{

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

            if(typeof addLine === "function"){
                addLine(line,isCommand,isError);
            }

            // 🔹 Optional Debug
            console.log("DEBUG JS Output:", line);

        }

    });

    if(typeof scrollBottom === "function"){
        scrollBottom();
    }

}

function scrollBottom(){

    if(typeof terminal !== "undefined" && terminal){
        terminal.scrollTop = terminal.scrollHeight;
    }

}

window.printOutput = printOutput;
window.scrollBottom = scrollBottom;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.renderer = true;

console.log("Renderer module ready");

}catch(e){

console.warn("Renderer module crashed", e);

}
