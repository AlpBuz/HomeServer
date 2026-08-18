export function getDate () {
    const date = new Date();
    return `${date.getMonth()}/${date.getDay()}/${date.getFullYear()}`;
}

export function getTime() {
    const now = new Date();

    let hour = now.getHours();
    const minutes = String(now.getMinutes()).padStart(2, "0");
    let abbreviation;

    if (hour >= 12){
        abbreviation = "PM";
    }else{
        abbreviation = "AM";
    }


    // Convert to 12-hour format
    hour = hour % 12;
    if (hour === 0) hour = 12;

    return `${hour}:${minutes} ${abbreviation}`;
}


const PALETTE = [
  "#7c3aed", // purple
  "#2563eb", // blue
  "#0d9488", // teal
  "#c2410c", // orange
  "#16a34a", // green
  "#a16207", // brown/gold
  "#b91c1c", // red
  "#1d4ed8", // indigo
];

export function pickColor(name) {
    if (!name){
        return PALETTE[0];
    }
    let hash = 0;
    for (let i = 0; i < name.length; i ++){
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }

    return PALETTE[Math.abs(hash) % PALETTE.length];
}