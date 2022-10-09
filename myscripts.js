var video = document.getElementById("myVideo");

// Get the button
var btn = document.getElementById("myBtn");

// Pause and play the video, and change the button text

function myFunction() {
  if (video.paused) {
    video.play();
    btn.innerHTML = "Pause";
  } else {
    video.pause();
    btn.innerHTML = "Play";
  }
}

//count-down

const daysE1 = document.getElementById("days");
const hoursE1 = document.getElementById("hours");
const minsE1 = document.getElementById("mins");
const secondsE1 = document.getElementById("seconds");

const newYears ="10 oct 2022";

function countdown() {
  const newYearsDate = new Date(newYears);
  const currentDate = new Date();
  const totalSeconds =  ( newYearsDate - currentDate )/
  1000;
  
  const days = Math.floor(totalSeconds/ 3600 /24); 
  const hours = Math.floor(totalSeconds/ 3600) % 24;
  const mins = Math.floor(totalSeconds/ 60) % 60;
  const seconds = Math.floor(totalSeconds) % 60;

  
  daysE1.innerHTML = days;
  hoursE1.innerHTML = formatTime(hours);
  minsE1.innerHTML = formatTime(mins);
  secondsE1.innerHTML = formatTime(seconds);
}

function formatTime(time) {
  return time < 10 ? `0${time}` : time;
}

countdown()
setInterval(countdown ,1000);