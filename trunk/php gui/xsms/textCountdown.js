
var supportsKeys = false


function favChange(rnum) {
	f = document.newMsg;

	if (f.favs.selectedIndex >=0) {
		f.msgTo.value = f.favs.options[f.favs.selectedIndex].value
	}

	if (f.favs.selectedIndex > 0 && f.favs.selectedIndex <= rnum + 1) {
		f.msgText.focus();
	} else {
		f.msgTo.focus();
	}
}
function calcCharLeft(f) {
		lenUSig = f.lenSSig.value
		maxLength = 160 - f.lenSysSig.value - lenUSig
        if (f.msgText.value.length > maxLength) {
	        f.msgText.value = f.msgText.value.substring(0,maxLength)
		    charleft = 0
        } else {
			charleft = maxLength - f.msgText.value.length
		}

        f.msgCL.value = charleft
}

function textKey(f) {
	supportsKeys = true
	calcCharLeft(f)
}
