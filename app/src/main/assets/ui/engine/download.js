// ----------------- DOWNLOAD MODULE -----------------
try{

const GITHUB_RAW = "https://raw.githubusercontent.com/arbajy35-cpu/vi-scripts/main/";

async function downloadPkg(pkgName) {

  if (!pkgName) {
    if (typeof addLine === "function") {
      addLine("Error: No package name provided", false, true);
    }
    return;
  }

  try {

    const url = `${GITHUB_RAW}${pkgName}`;

    if (typeof addLine === "function") {
      addLine(`Downloading ${pkgName}...`, true);
    }

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error("Not found on GitHub");
    }

    const data = await response.text();

    if (
      window.AndroidBridge &&
      typeof window.AndroidBridge.saveScript === "function"
    ) {

      window.AndroidBridge.saveScript(pkgName, data);

      if (typeof addLine === "function") {
        addLine(`Installation complete: ${pkgName}`, true);
      }

      if (typeof playTone === "function") {
        playTone(800, 0.06, "square", 0.08);
      }

    } 
    else {

      if (typeof addLine === "function") {
        addLine("Error: Native storage bridge not available", false, true);
      }

    }

  } 
  catch (err) {

    if (typeof addLine === "function") {
      addLine(`Error: ${err.message}`, false, true);
    }

    if (typeof playTone === "function") {
      playTone(300, 0.06, "triangle", 0.08);
    }

  }

}

window.downloadPkg = downloadPkg;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.download = true;

console.log("Download module ready");

}catch(e){

console.warn("Download module crashed", e);

}
