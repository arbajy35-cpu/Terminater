// ----------------- CRT CANVAS ENGINE -----------------
try{

// const crtCanvas = document.createElement("canvas");
// const crtCtx = crtCanvas.getContext("2d");
// document.body.appendChild(crtCanvas);

// ... baki saara canvas code ...

// drawCRT();


// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.crt = true;

console.log("CRT module ready");

}catch(e){

console.warn("CRT module crashed", e);

}
