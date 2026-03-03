// ----------------- DOWNLOAD.JS -----------------

const GITHUB_RAW = "https://raw.githubusercontent.com/arbajy35-cpu/vi-scripts/main/";

async function downloadPkg(pkgName) {
    if (!pkgName) return;

    try {
        const url = `${GITHUB_RAW}${pkgName}`;
        addLine(`Downloading ${pkgName}...`, true);

        const response = await fetch(url);
        if (!response.ok) throw new Error("Not found on GitHub");

        const data = await response.text();

        // Send script to Android native for saving
        if (window.AndroidBridge && typeof window.AndroidBridge.saveScript === "function") {
            window.AndroidBridge.saveScript(pkgName, data);
            addLine(`Installation complete: ${pkgName}`, true);
            playTone(800, 0.06, "square", 0.08);
        } else {
            addLine("Error: Native storage bridge not available", true);
        }

    } catch (err) {
        addLine(`Error: ${err.message}`, true);
        playTone(300, 0.06, "triangle", 0.08);
    }
}

window.downloadPkg = downloadPkg;