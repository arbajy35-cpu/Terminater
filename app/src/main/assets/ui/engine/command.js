function executeCommand(command){

/* pkg install */
if(command.toLowerCase().startsWith("pkg install ")){

const pkgName = command.split(" ")[2];

if(pkgName && typeof downloadPkg === "function"){
downloadPkg(pkgName);
return;
}

}

/* JS Script */
if(typeof runScript === "function"){

const parts = command.split(" ");
const scriptName = parts[0];
const args = parts.slice(1);

const handled = runScript(scriptName,args);

if(handled === false){

addLine("Error: Script execution failed",false,true);
runNative(command);

}

}
else{

runNative(command);

}

}

window.executeCommand = executeCommand;
