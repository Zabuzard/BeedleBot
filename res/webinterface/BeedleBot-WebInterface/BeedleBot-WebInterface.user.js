// ==UserScript==
// @name        BeedleBot-WebInterface
// @namespace   Zabuza
// @description This is the web user interface of BeedleBot. BeedleBot is tool for the MMORPG Freewar which automizes the trade and the corresponding sale at the central traders depot.
// @include     *.freewar.de/freewar/internal/item.php*
// @version     1
// @require https://code.jquery.com/jquery-3.2.0.min.js
// @grant       none
// ==/UserScript==

/*
 * Adds the CSS rules of the web user interface to the body.
 */
function addCssRules() {
	$('body').append('<style type="text/css">\
			#beedleInterface{\
				position: absolute;\
				z-index: 3;\
			}\
			\
			#beedleLayout, #beedleContentLayout, #beedleStatusPanelLayout,\
			#beedleValuePanelLayout, #beedleItemPanelLayout {\
				left: 0;\
				top: 0;\
				width: 100%;\
				height: 100%;\
				border-spacing: 0;\
			}\
			\
			#beedleLayout td, #beedleContentLayout td, #beedleStatusPanelLayout td,\
			#beedleValuePanelLayout td, #beedleItemPanelLayout td {\
				margin: 0;\
				padding: 0;\
				text-align: center;\
			}\
			\
			#beedleNavigation {\
				height: 30;\
				background-color: lightslategray;\
			}\
			\
			#beedleHidebar {\
				width: 10;\
				background-color: #B7B7B7;\
				border-top: 3px solid black;\
			}\
			\
			#beedleContent {\
				background-color: white;\
				border: 1px solid black;\
				border-top-width: 3px\
			}\
			\
			.hiddenInterface #beedleNavigation {\
				background-color: transparent;\
			}\
			\
			.hiddenInterface #beedleHidebar {\
				border: 1px solid black;\
			}\
			\
			.hiddenInterface #beedleContent {\
				border-width: 0px;\
			}\
			\
			#beedleItemPanel {\
				border-top: 3px solid black;\
			}\
			\
			.beedleItemHeader {\
				background-color: #DDD;\
			}\
			\
			.beedleItemHeader tr, .beedleItemHeader td,\
			.beedleItemEntryRow tr, .beedleItemEntryRow td {\
				height: 3px;\
				line-height: 3px;\
			}\
			\
			.beedleItemName {\
				text-align: left !important;\
			}\
			\
			.beedleItemCost {\
				text-align: right !important;\
			}\
			\
			.beedleItemProfit {\
				color: #63DA81;\
				font-weight: bold;\
				text-align: right !important;\
			}\
		</style>');
}

/*
 * Checks whether the browser does support webstorage or not.
 * @returns True if it is supported, false if not
 */
function isSupportingWebStorage() {
	return typeof(Storage) !== "undefined";
}

/*
 * Toggles the visibility of the interface.
 */
function toggleInterface() {
	var itemFrame = $('body');
	var beedleInterface = $('#beedleInterface');
	var hidebar = $('#beedleHidebar');
	var content = $('#beedleContent');
	
	var itemFrameWidth = $(itemFrame)[0].scrollWidth;
	var itemFrameLeft = $(itemFrame).position().left;
	var hidebarWidth = $(hidebar)[0].scrollWidth;
	
	if ($(beedleInterface).hasClass('hiddenInterface')) {
		// Show the interface
		$(beedleInterface).removeClass('hiddenInterface')
		
		$(beedleInterface).css('left', itemFrameLeft);
		$(beedleInterface).width(itemFrameWidth);
		$(content).show();
	} else {
		// Hide the interface
		$(beedleInterface).css('left', itemFrameLeft + (itemFrameWidth - hidebarWidth));
		$(beedleInterface).width(hidebarWidth);
		$(content).hide();
		
		$(beedleInterface).addClass('hiddenInterface')
	}
}

/*
 * Creates and the layout of the web user interface.
 */
function createLayout() {
	// Create the layout table
	$('#beedleInterface').append('<table id="beedleLayout">\
			<tr>\
				<td id="beedleNavigation"></td>\
			</tr>\
			<tr>\
				<td id="beedleHidebar"></td>\
				<td id="beedleContent"></td>\
			<tr/>\
		</table>');
	$('#beedleNavigation').attr('colspan', 2);
	
	// Create the content layout table
	$('#beedleContent').append('<table id="beedleContentLayout">\
			<tr>\
				<td id="beedleStatusPanel"></td>\
			</tr>\
			<tr>\
				<td id="beedleValuePanel"></td>\
				<td id="beedleIconPanel">c</td>\
			</tr>\
			<tr>\
				<td id="beedleItemPanel"></td>\
			</tr>\
		</table>');
	$('#beedleStatusPanel, #beedleItemPanel').attr('colspan', 2);
		
	// Create the status panel layout
	$('#beedleStatusPanel').append('<table id="beedleStatusPanelLayout">\
			<tr>\
				<td id="beedleStateCell">aa</td>\
				<td id="beedlePhaseAnalyseCell">ab</td>\
			</tr>\
			<tr>\
				<td id="beedleActivationCell">ba</td>\
				<td id="beedlePhasePurchaseCell">bb</td>\
			</tr>\
			<tr>\
				<td>ca</td>\
				<td id="beedlePhaseWaitCell">cb</td>\
			</tr>\
		</table>');
		
	// Create the value panel layout
	$('#beedleValuePanel').append('<table id="beedleValuePanelLayout">\
			<tr>\
				<td id="beedleLifepointIconCell">aa</td>\
				<td id="beedleLifepointCell">ab</td>\
			</tr>\
			<tr>\
				<td id="beedleGoldIconCell">ba</td>\
				<td id="beedleGoldCell">bb</td>\
			</tr>\
			<tr>\
				<td id="beedleInventoryIconCell">ca</td>\
				<td id="beedleInventoryCell">cb</td>\
			</tr>\
			<tr>\
				<td id="beedleWaitingTimeIconCell">da</td>\
				<td id="beedleWaitingTimeCell">db</td>\
			</tr>\
		</table>');
		
	// Create the item panel layout
	$('#beedleItemPanel').append('<table id="beedleItemPanelLayout">\
			<tr class="beedleItemHeader">\
				<td>aa</td>\
				<td id="beedleTotalCostCell" class="beedleItemCost">ab</td>\
				<td id="beedleTotalProfitCell" class="beedleItemProfit">ac</td>\
			</tr>\
			<tr class="beedleItemEntryRow">\
				<td class="beedleItemName">ba</td>\
				<td class="beedleItemCost">bb</td>\
				<td class="beedleItemProfit">bc</td>\
			</tr>\
		</table>');
}

/*
 * Creates and loads the web user interface.
 */
function loadInterface() {
	// Web storage is necessary for BeedleBots communication
	if (!isSupportingWebStorage()) {
		return;
	}
	
	// Determine the size and position of the item frame,
	// the interface will overlap it
	var itemFrame = $('body');
	var itemFrameWidth = $(itemFrame)[0].scrollWidth;
	var itemFrameHeight = $(itemFrame)[0].scrollHeight;
	var itemFramePosition = $(itemFrame).position();
	// If frame is not yet ready
	if (!$.isNumeric(itemFrameWidth) || !$.isNumeric(itemFrameHeight)) {
		return;
	}
	
	// Introduce new css rules
	addCssRules();
	
	// Create the container of the interface
	$('body').append('<div id="beedleInterface"></div>');
	var beedleInterface = $('#beedleInterface');
	
	// Position the container
	$(beedleInterface).css(itemFramePosition);
	$(beedleInterface).width(itemFrameWidth);
	$(beedleInterface).height(itemFrameHeight);
	
	// Create the layout of the interface
	createLayout();
		
	// Initially hide the interface
	toggleInterface();
	
	// Add hide-show click event handler
	$('#beedleHidebar').click(toggleInterface);
}

// Load the web user interface
loadInterface();