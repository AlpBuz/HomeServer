export function getDate(date) {
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${month}/${day}/${date.getFullYear()}`;
}

export function getTime(date) {
    let hour = date.getHours();
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds());
    const abbreviation = hour >= 12 ? "PM" : "AM";

    hour = hour % 12;
    if (hour === 0) hour = 12;

    return `${hour}:${minutes}:${seconds} ${abbreviation}`;
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