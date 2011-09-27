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
    along with ALP.  If not, see <http://www.gnu.org/licenses/>.*/
function fnShowHide( iCol )
{
	/* Get the DataTables object again - this is not a recreation, just a get of the object */
	var oTable = $('#tests').dataTable();
	
	var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;
	oTable.fnSetColumnVis( iCol, bVis ? false : true );
}

$(document).ready(function(){
	
     var table = $('#tests').dataTable({
		"bJQueryUI": true,
		"sPaginationType": "full_numbers",
		
		"sDom": '<"H"lfr>t<"F"ip>',
		
		"bScrollCollapse": true,
		"sScrollX": "100%",
				
		// Disable sorting for 'Details' columns
        "aoColumns": [
			null, // ID ( 0 )
			null, // Suite ( 1 )
			{"bSortable": false}, //Details ( 2 )
			null, // Suite Section ( 3 )
			null, // Total ( 4 )
			null, // Failed ( 5 )
			null, // Skipped ( 6 )
			null, // Status ( 7 )
			{"bSortable": false} // Tests ( 8 )
		],

		// Disable visibility of 'Id', 'Suite' and 'Details'
        "aoColumnDefs": [
            {
			"fnRender": function ( oObj ) {
            	var w = $('#tests th:eq(7)').width(); // get width of 'Status' column
            	var k = w / oObj.aData[4]; // width / total tests
				var r = '<dl style="float: left; margin: 0px; padding: 0px;">';
				if (oObj.aData[5]>0)
					r += '<dd class="bar failed" style="width:'+k*oObj.aData[5]+'px;">'+oObj.aData[5] +'</dd>';
				if (oObj.aData[6]>0)
					r += '<dd class="bar skipped" style="width:'+k*oObj.aData[6]+'px;">'+ oObj.aData[6] +'</dd>';
				if (oObj.aData[7]>0)
					r += '<dd class="bar passed" style="width:'+k*oObj.aData[7]+'px;">'+ oObj.aData[7]+'</dd>';
				r += '</dl>';
				return r;
			},
			"aTargets": [ 7 ]
	        },
	        {"bVisible": false, "aTargets": [0, 1, 2,  4,5,6]}
        ],
		
		// Descendant sorting by suite id
		"aaSortingFixed": [[0, 'desc']],
		 
		 // Grouping tests by suite
        "fnDrawCallback": function(settings){
			
            if (settings.aiDisplay.length == 0) {
                return;
            }
            
            var nTrs = $('#tests tbody tr');
            var iColspan = nTrs[0].getElementsByTagName('td').length;
			
            var lastSuiteId = "";
            for (var i = 0; i < nTrs.length; i++) {
                var iDisplayIndex = settings._iDisplayStart + i;
				
                var suiteId = settings.aoData[settings.aiDisplay[iDisplayIndex]]._aData[0];                
                var suite = settings.aoData[settings.aiDisplay[iDisplayIndex]]._aData[1];
				var details = settings.aoData[settings.aiDisplay[iDisplayIndex]]._aData[2];
                
                if (suiteId != lastSuiteId) {
                    var nGroup = document.createElement('tr');
					
					// Copy suite name
                    var nCell = document.createElement('td');
                    nCell.colSpan = iColspan - 1;
                    nCell.className = "group";
					nCell.innerHTML = suite;
                    nGroup.appendChild(nCell);
					
					// Copy suite details link
					var nCell = document.createElement('td');
                    nCell.className = "group center";
					nCell.innerHTML = details;
                    nGroup.appendChild(nCell)					
					
                    nTrs[i].parentNode.insertBefore(nGroup, nTrs[i]);
                    lastSuiteId = suiteId;
                }
            }
        }
    });
	
	// Dynamic table resize if window size is changed
    $(window).resize(function(){
        table.fnAdjustColumnSizing();
    })
	
	// Move filter elements to table tool bar    
	var filter = $("div#filter").detach();
    $("div#tests_length").before(filter);

    // Add "Change view" controls to filter of grid on the top    
	var view = $("div#view").detach();
	$("div#tests_filter label").css("float", "right");
	$(view).appendTo("div#tests_filter");

	// Add onChange event for "Change view" control 
	$("#view_select").change(function () {
		switch ($("#view_select option:selected").text()){
			case 'Columns':
			case 'Chart':
				fnShowHide(4); fnShowHide(5); fnShowHide(6);
				fnShowHide(7);
				break;
			default:
				alert('There is no selected view!');
		}
	});

});