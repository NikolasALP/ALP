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
 * */
function groupById(settings){
    if (settings.aiDisplay.length == 0) {
        return;
    }
    
    var iCol = 0;
    var classPosition = 1;
    
    var nTrs = $("#testmethods tbody tr");
    var iColspan = nTrs[0].getElementsByTagName('td').length;
    
    var sLastGroup = "";
    for (var i = 0; i < nTrs.length; i++) {
        var iDisplayIndex = settings._iDisplayStart + i;
        var aoData = settings.aoData[settings.aiDisplay[iDisplayIndex]];
        var sGroup = aoData._aData[iCol];
        
        if (sGroup != sLastGroup) {
            var nGroup = document.createElement('tr');
            var nCell = document.createElement('td');
            
            nCell.colSpan = iColspan;
            nCell.className = "group";
            
            // Set class value instead of Id value
            var classCell = aoData._aData[classPosition];
            nCell.innerHTML = classCell;
            
            nGroup.appendChild(nCell);
            nTrs[i].parentNode.insertBefore(nGroup, nTrs[i]);
            sLastGroup = sGroup;
        }
    }
}

function enableGroupById(){
    var table = $("#testmethods").dataTable();
    var settings = table.fnSettings();
    
    settings.aoDrawCallback[0].fn = groupById
    
    // Set 'Id' sorting as fixed	
    settings.aaSortingFixed = [[0, 'desc']];
    
    // Hide 'Id' column
    table.fnSetColumnVis(0, false);
    // Hide 'Class' column
    table.fnSetColumnVis(1, false);
    // Hide 'Class' short name column
    table.fnSetColumnVis(2, false);
    
    table.fnDraw();
}

function disableGroupById(){
    var table = $("#testmethods").dataTable();
    var settings = table.fnSettings();
    
    settings.aoDrawCallback[0].fn = function(){
    };
    
    // Remove fixed sorting
    settings.aaSortingFixed = [];
    
    // Hide 'Id' column
    table.fnSetColumnVis(0, true);
    // Hide 'Class' column
    table.fnSetColumnVis(1, false);
    // Hide 'Class' short name column
    table.fnSetColumnVis(2, true);
    
    table.fnDraw();
}

function groupBy(select){
    
    switch (select.value) {
        case "id":
			enableGroupById();
            break;
        case "disable":
			disableGroupById();
            break;
    }
}

$(document).ready(function(){

    // Change full stack trace visibility
    $("div.exception a.className").click(function(){
        var stackTrace = $(this).parent().children("div.fullStacktrace");
        stackTrace.toggle();
    });
    
    var table = $("#testmethods").dataTable({
        "bJQueryUI": true,
        "sPaginationType": "full_numbers",
        
        "sDom": '<"H"lfr>t<"F"ip>',
        
        "bScrollCollapse": true,
        "sScrollX": "100%",
        
        "aoColumns": [
			null, 
			null, 
			null,
			null,
			null, 
			null,
			null,
			null,
			null,
			null,
			{"bSortable": false}
		],
        
        // Descendant sorting by instance id
        "aaSorting": [[0, 'desc']],
        
        // Initial settings to group by 'Id'
        "aaSortingFixed": [[0, 'desc']],
        "aoColumnDefs": [{
            "bVisible": false,
            "aTargets": [0, 1, 2, 4, 5]
        }],
        
        // Initial grouping function
        "fnDrawCallback": groupById
    });
	
	// Dynamic table resize if window size is changed
    $(window).resize(function(){
        table.fnAdjustColumnSizing();
    })
    
    // Move filter elements to table tool bar
    var filter = $("div#filter").detach();
    $("div#testmethods_length").before(filter);
    
    // Move grouping elements to table tool bar
    var group_by = $("div#group_by").detach();
    $("div#testmethods_length").after(group_by);
});
