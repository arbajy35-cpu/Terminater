// ----------------- DOWNLOAD MODULE -----------------
try {

const GITHUB_RAW = "https://raw.githubusercontent.com/arbajy35-cpu/vi-scripts/main/";

async function downloadPkg(pkgName) {
    if(!pkgName){
        if(typeof addLine === "function") addLine("Error: No package name provided", false, true);
        return;
    }

    try {
        if(typeof addLine === "function") addLine(`Downloading ${pkgName}...`, true);

        // Use AndroidBridge.downloadOfficialScript instead of saveScript
        if(window.AndroidBridge && typeof window.AndroidBridge.downloadOfficialScript === "function"){
            window.AndroidBridge.downloadOfficialScript(pkgName);
            if(typeof addLine === "function") addLine(`Installation started: ${pkgName}`, true);
        } else {
            if(typeof addLine === "function") addLine("Error: Native storage bridge not available", false, true);
            return;
        }

    } catch(err) {
        if(typeof addLine === "function") addLine(`Error: ${err.message}`, false, true);
        if(typeof playTone === "function") playTone(300, 0.06, "triangle", 0.08);
    }
}

window.downloadPkg = downloadPkg;

// Module ready signal
window.engineModuleReady = window.engineModuleReady || {};
window.engineModuleReady.download = true;

console.log("Download module ready");

} catch(e) {
    console.warn("Download module crashed", e);
}
