/*Copyright 2011 Lohika .  This file is part of ALP.

    ALP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ALP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ALP.  If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * Hides/shows element with given id
 * @param {Object} id
 */
function collapse(id){
    if (document.getElementById(id)) {
        elm = document.getElementById(id);
        elm.style.display = elm.style.display == 'none' || elm.style.display == '' ? 'block' : 'none';
    }
}

/**
 * Hides/shows all elements with given classname
 * @param {Object} classname
 */
function collapseByClass(classname){
	var i;
    var elements = getElementsByClassName(classname);
	if (elements[0].className.search("invisible")==-1)	{
    	for (i in elements) {
    	elements[i].className += " invisible";
    	}
	}
	else	{
		for (i in elements) {
		elements[i].className = elements[i].className.replace(" invisible","");
		}
	}
}

/**
 * Converts timestamp into human date
 * @param {Object} unix_timestamp
 */
function getTimeFromTimestamp(unix_timestamp){
    var date = new Date(unix_timestamp);
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    var milliseconds = date.getMilliseconds();
    var formattedTime = pad(hours, 2) + ':' + pad(minutes, 2) + ':' + pad(seconds, 2) + '.' + pad(milliseconds, 3);
    return formattedTime;
}

/**
 * Adds zeros before number e.g '02' instead of '2'
 * @param {Object} number
 * @param {Object} length
 */
function pad(number, length){
    var str = '' + number
    while (str.length < length) {
        str = '0' + str;
    }
    return str;
}

/**
 * Opens picture in pop-up window
 * @param {Object} url
 */
function newPopup(url){
    popupWindow = window.open(url, 'popUpWindow', 'height=700,width=800,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes')
}

/**
 * Returns an array with elements which has given classname
 * @param {Object} clsName
 */
function getElementsByClassName(clsName){
    var retVal = new Array();
    var elements = document.getElementsByTagName("*");
    for (var i = 0; i < elements.length; i++) {
        if (elements[i].className.indexOf(" ") >= 0) {
            var classes = elements[i].className.split(" ");
            for (var j = 0; j < classes.length; j++) {
                if (classes[j] == clsName) 
                    retVal.push(elements[i]);
            }
        }
        else 
            if (elements[i].className == clsName) 
                retVal.push(elements[i]);
    }
    return retVal;
}

/**
 * Handles which entries to show depending on given log level
 * @param {Object} level
 */
function levelHander(level){
	var i;
	var definedLevel = getLevel(level);
	var currentLevel;
    var elements = getElementsByClassName('level');
	var shownEntries = 0;
	
	for (i in elements) {
		currentLevel = getLevel(elements[i].textContent.trim());
		if ((currentLevel < definedLevel)&&(elements[i].parentNode.className.search("invisible") == -1))	{
		    	elements[i].parentNode.className += " invisible";
		}
		if (currentLevel >= definedLevel)	{
			elements[i].parentNode.className = elements[i].parentNode.className.replace(" invisible","");
			shownEntries++;
		}
    }
	
	// If no entries found display a message
	message = document.getElementById("nomessages");
	logtable = document.getElementById("log");
	if (shownEntries==0)	{
		message.textContent = "no log messages at this logging level";
		shownEntries==0;
		
		if (logtable.className.search("invisible")==-1)	{
			logtable.className += " invisible";			
		}
	}
	else	{
		message.textContent = "";
		logtable.className = logtable.className.replace(" invisible","");
	}
}

/**
 * Transform level name into a number
 * @param {Object} level
 */
function getLevel(level){
	if (level.toLowerCase().trim()=='trace') return 0;
	if (level.toLowerCase().trim()=='debug') return 1;
	if (level.toLowerCase().trim()=='info') return 2;
	if (level.toLowerCase().trim()=='warn') return 3;
	if (level.toLowerCase().trim()=='error') return 4;
	if (level.toLowerCase().trim()=='fatal') return 5;
}
