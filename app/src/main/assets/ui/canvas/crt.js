// ----------------- CRT CANVAS ENGINE -----------------

// Create overlay canvas
const crtCanvas = document.createElement("canvas");
const crtCtx = crtCanvas.getContext("2d");
document.body.appendChild(crtCanvas);

crtCanvas.style.position = "fixed";
crtCanvas.style.top = "0";
crtCanvas.style.left = "0";
crtCanvas.style.pointerEvents = "none"; // Clicks pass through
crtCanvas.style.zIndex = "9999";        // On top
crtCanvas.width = window.innerWidth;
crtCanvas.height = window.innerHeight;

// Resize handler
window.addEventListener("resize", () => {
    crtCanvas.width = window.innerWidth;
    crtCanvas.height = window.innerHeight;
});

// ----------------- CRT SETTINGS -----------------
const scanlineOpacity = 0.08;
const flickerStrength = 0.06;
let frameCount = 0;

// ----------------- DRAW FUNCTION -----------------
function drawCRT() {
    const w = crtCanvas.width;
    const h = crtCanvas.height;

    // Clear canvas
    crtCtx.clearRect(0, 0, w, h);

    // ----------------- EDGE GLOW -----------------
    const grad = crtCtx.createRadialGradient(w/2, h/2, 0, w/2, h/2, Math.max(w,h)/1.5);
    grad.addColorStop(0,"rgba(255,255,255,0.015)");
    grad.addColorStop(0.6,"rgba(0,0,0,0.15)");
    grad.addColorStop(1,"rgba(0,0,0,0.55)");
    crtCtx.fillStyle = grad;
    crtCtx.fillRect(0,0,w,h);

    // ----------------- SCANLINES -----------------
    crtCtx.fillStyle = `rgba(0,255,88,${scanlineOpacity})`;
    for(let y=0; y<h; y+=2){
        crtCtx.fillRect(0,y,w,1);
    }

    // ----------------- CRT FLICKER -----------------
    const flicker = (Math.random()*flickerStrength);
    crtCtx.fillStyle = `rgba(0,255,88,${flicker})`;
    crtCtx.fillRect(0,0,w,h);

    frameCount++;
    requestAnimationFrame(drawCRT);
}

// Start drawing
drawCRT();
