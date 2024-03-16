





window.onload =function(){ getTimesInAspecificMonthAndYearAndCityAndCountry("Amman")};



function getYear(){
    var currentDate = new Date();
    return currentDate.getFullYear();
}
function getMonth(){
    var month = new Date();
    return month.getMonth();
}
function getDay(){
    var day = new Date();
    return day.getDate();
}
function getTimesInAspecificMonthAndYearAndCityAndCountry(city){

    let year = getYear();
    let month = getMonth()+1; //starts from 0
    let day = getDay() -1 ; //because it's an array
   
    
    


axios.get(`http://api.aladhan.com/v1/calendarByCity/${year}/${month}?city=${city}&country=Jordan&method=4`).
then((response)=>{

   
     document.querySelector(".date").innerHTML= response.data.data[day].date.readable;
     document.getElementById("Fajr").innerHTML= response.data.data[day].timings.Fajr.slice(0,5);
     document.getElementById("Sunrise").innerHTML= response.data.data[day].timings.Sunrise.slice(0,5);
     document.getElementById("Dhuhr").innerHTML= response.data.data[day].timings.Dhuhr.slice(0,5);
     document.getElementById("Asr").innerHTML= response.data.data[day].timings.Asr.slice(0,5);
     document.getElementById("Maghrib").innerHTML= response.data.data[day].timings.Maghrib.slice(0,5);
     document.getElementById("Isha").innerHTML= response.data.data[day].timings.Isha.slice(0,5);
    

    
    
}).catch((error)=>{

    console.log("there is a specific error in url");

})

}

let buttons =document.querySelectorAll("button");

for(let i=0 ; i< buttons.length;i++)
    buttons[i].onclick = function(){
        getTimesInAspecificMonthAndYearAndCityAndCountry(buttons[i].id);
        document.querySelector(".container h2").innerHTML=buttons[i].id.toUpperCase();
    };


 

