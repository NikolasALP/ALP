/*
 * Copyright 2011 Lohika .  This file is part of ALP.

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
jQuery.fn.dataTableExt.oSort['num-html-asc']  = function(a,b) {
	var x = a.replace( /<.*?>/g, "" );
	var y = b.replace( /<.*?>/g, "" );
	x = parseFloat( x );
	y = parseFloat( y );
	return ((x < y) ? -1 : ((x > y) ?  1 : 0));
};

jQuery.fn.dataTableExt.oSort['num-html-desc'] = function(a,b) {
	var x = a.replace( /<.*?>/g, "" );
	var y = b.replace( /<.*?>/g, "" );
	x = parseFloat( x );
	y = parseFloat( y );
	return ((x < y) ?  1 : ((x > y) ? -1 : 0));
};

jQuery.fn.dataTableExt.aTypes.push(function(sData) {
	sData = typeof sData.replace == 'function' ? sData.replace(/<.*?>/g, "")
			: sData;

	var sValidFirstChars = "0123456789-";
	var sValidChars = "0123456789.";
	var Char;
	var bDecimal = false;

	/* Check for a valid first char (no period and allow negatives) */
	Char = sData.charAt(0);
	if (sValidFirstChars.indexOf(Char) == -1) {
		return null;
	}

	/* Check all the other characters are valid */
	for ( var i = 1; i < sData.length; i++) {
		Char = sData.charAt(i);
		if (sValidChars.indexOf(Char) == -1) {
			return null;
		}

		/* Only allowed one decimal place... */
		if (Char == ".") {
			if (bDecimal) {
				return null;
			}
			bDecimal = true;
		}
	}

	return 'num-html';
});