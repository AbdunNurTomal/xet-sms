function blinkOn(){
  theWin.document.bgColor = "0000ff"
  nTimes++
  JSCTimeOutID = window.setTimeout("blinkOff()",50);
}

function blinkOff(){
  theWin.document.bgColor = "FFFFFF"
  if (nTimes < 3)
    JSCTimeOutID = window.setTimeout("blinkOn()",50);
  else theWin.history.go(0)
}

function blinkit(aWin){
  nTimes = 0
  theWin = aWin
  JSCTimeOutID = window.setTimeout("blinkOn()",50);
}


function blinker(farbe1, farbe2, tempo) {
  faktor = 0;
  heller = 1;
  rot1 = farbe1.substr(0, 2);
  gruen1 = farbe1.substr(2, 2);
  blau1 = farbe1.substr(4, 2);
  rot1 = parseInt(rot1, 16);
  gruen1 = parseInt(gruen1, 16);
  blau1 = parseInt(blau1, 16);
  rot2 = farbe2.substr(0, 2);
  gruen2 = farbe2.substr(2, 2);
  blau2 = farbe2.substr(4, 2);
  rot2 = parseInt(rot2, 16);
  gruen2 = parseInt(gruen2, 16);
  blau2 = parseInt(blau2, 16);
  rot_diff = rot2 - rot1;
  gruen_diff = gruen2 - gruen1;
  blau_diff = blau2 - blau1;
  tempo_verlauf = tempo;
  blinker_verlauf();
	}
function blinker_verlauf() {
  rot_neu = rot1 + rot_diff / 100 * faktor;
  gruen_neu = gruen1 + gruen_diff / 100 * faktor;
  blau_neu = blau1 + blau_diff / 100 * faktor;
  rot_neu = Math.floor(rot_neu);
  gruen_neu = Math.floor(gruen_neu);
  blau_neu = Math.floor(blau_neu);
  if(heller == 0) {
    faktor += 10;
  } else {
    faktor -= 10;
  }
  if(faktor >= 100) {
    heller = 1;
  }
  if(faktor <= 0) {
    heller = 0;
  }
  for(i=0; i<document.getElementsByTagName("span").length; i++) {
    if(document.getElementsByTagName("span")[i].className == "blink") document.getElementsByTagName("span")[i].style.color = "rgb(" + rot_neu + ", " + gruen_neu + ", " + blau_neu + ")";
  }
  window.setTimeout("blinker_verlauf()", tempo_verlauf);
}


/***********************************************
* Flashing Link Script- © Dynamic Drive (www.dynamicdrive.com)
* This notice must stay intact for use
* Visit http://www.dynamicdrive.com/ for full source code
***********************************************/

var flashlinks=new Array()

function changelinkcolor(){
  for (i=0; i< flashlinks.length; i++){
    var flashtype=document.getElementById? flashlinks[i].getAttribute("flashtype")*1 : flashlinks[i].flashtype*1
    var flashcolor=document.getElementById? flashlinks[i].getAttribute("flashcolor") : flashlinks[i].flashcolor
    if (flashtype==0){
      if (flashlinks[i].style.color!=flashcolor)
        flashlinks[i].style.color=flashcolor
      else
        flashlinks[i].style.color=''
    }
    else if (flashtype==1){
    if (flashlinks[i].style.backgroundColor!=flashcolor)
      flashlinks[i].style.backgroundColor=flashcolor
    else
      flashlinks[i].style.backgroundColor=''
    }
  }
}

function init(){
  var i=0
    if (document.all){
    while (eval("document.all.flashlink"+i)!=null){
      flashlinks[i]= eval("document.all.flashlink"+i)
      i++
    } 
  }
  else if (document.getElementById){
    while (document.getElementById("flashlink"+i)!=null){
      flashlinks[i]= document.getElementById("flashlink"+i)
      i++
    }
  }
  setInterval("changelinkcolor()", 1000)
}

if (window.addEventListener)
  window.addEventListener("load", init, false)
else if (window.attachEvent)
  window.attachEvent("onload", init)
else if (document.all)
  window.onload=init
