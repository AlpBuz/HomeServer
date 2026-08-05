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