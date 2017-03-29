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
			#beedleInterface {\
				position: absolute;\
				z-index: 3;\
			}\
			\
			#beedleLayout, #beedleContentLayout, #beedleStatusPanelLayout,\
			#beedleValuePanelLayout, #beedleItemPanelLayout {\
				left: 0;\
				top: 0;\
				width: 100%;\
				border-spacing: 0;\
			}\
			\
			#beedleLayout, #beedleContentLayout, #beedleStatusPanelLayout,\
			#beedleValuePanelLayout {\
				height: 100%;\
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
				height: 0/*30*/;\
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
			#beedleStatusPanel {\
				height: 160px;\
			}\
			\
			.beedleStatusLeftOuter, .beedleStatusRightOuter {\
				width: 50%;\
			}\
			\
			.beedleStatusLeftOuter {\
				padding-left: 10px !important;\
				padding-right: 5px !important;\
			}\
			\
			.beedleStatusRightOuter {\
				padding-left: 5px !important;\
				padding-right: 10px !important;\
			}\
			\
			.beedleStatusUpper {\
				padding-top: 10px !important;\
				padding-bottom: 2px !important;\
			}\
			\
			.beedleStatusInner {\
				padding-top: 2px !important;\
				padding-bottom: 2px !important;\
			}\
			\
			.beedleStatusLower {\
				padding-top: 2px !important;\
				padding-bottom: 10px !important;\
			}\
			\
			.statusRibbonTable, #statusActivationTable {\
				height: 40px;\
				border-collapse: collapse;\
				width: 100%;\
			}\
			\
			.statusRibbon {\
				width: 17px;\
				border-right: 2px solid #333;\
			}\
			\
			.statusRibbonText {\
				width: 75px;\
				background-color: #DDD;\
				font-size: 1.5em;\
				text-align: left !important;\
				padding-left: 10px !important;\
			}\
			\
			.grayRibbon {\
				background-color: #969696;\
			}\
			\
			.blueRibbon {\
				background-color: #63A7DA;\
			}\
			\
			.greenRibbon {\
				background-color: #63DA81;\
			}\
			\
			.redRibbon {\
				background-color: #DA6363;\
			}\
			\
			#statusActivationOnCell, #statusActivationOffCell {\
				width: 50%;\
			}\
			\
			#statusActivationOnCell {\
				text-align: left !important;\
				padding-left: 10px !important;\
			}\
			\
			#statusActivationOffCell {\
				text-align: right !important;\
				padding-right: 10px !important;\
			}\
			\
			#beedleValuePanel {\
				height: 120px;\
			}\
			\
			.valueIcon {\
				padding-left: 10px !important;\
			}\
			\
			.valueText {\
				font-weight: bold;\
				text-align: left !important;\
				padding-left: 5px !important;\
				font-size: 1.2em;\
			}\
			\
			#beedleIconPanel {\
				text-align: right !important;\
				vertical-align: bottom;\
			}\
			\
			#beedleItemPanel {\
				border-top: 3px solid black;\
				vertical-align: top;\
			}\
			\
			.beedleItemHeader {\
				background-color: #DDD;\
			}\
			\
			.beedleItemName, .beedleItemCost, .beedleItemProfit {\
				font-weight: bold;\
			}\
			\
			.beedleItemName {\
				text-align: left !important;\
				padding-left: 5px !important;\
				width: 50%;\
			}\
			\
			.beedleItemCost {\
				text-align: right !important;\
				padding-right: 5px !important;\
				padding-left: 5px !important;\
				width: 25%;\
			}\
			\
			.beedleItemProfit {\
				text-align: right !important;\
				padding-right: 5px !important;\
				color: #29AD4A;\
				width: 25%;\
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
 * Builds the key used in the session storage according to the given key identifier.
 * @param key The key which identifies the key used in the session storage to build
 * @returns The key used in the session storage which corresponds
 *  to the given key identifier
 */
function buildKey(key) {
	return storageKeys['keyIndex'] + key;
}

/*
 * Gets the value of the item given by its key from the session storage.
 * @returns The value of item given by its key or null if not defined
 */
function getItem(key) {
	var keyOfStorage = buildKey(key);
	var value = sessionStorage.getItem(keyOfStorage);
	if (value == null || value == '' || value == 'undefined') {
		return null;
	} else if (value.toLowerCase() == 'true') {
		return true;
	} else if (value.toLowerCase() == 'false') {
		return false;
	} else {
		return value;
	}
}

/*
 * Sets the value of the item given by its key in the session storage.
 * @param key The key of the item to set
 * @param value The value to set
 */
function setItem(key, value) {
	sessionStorage.setItem(buildKey(key), value);
}

/*
 * Mockup which acts as Beedle Bot Server.
 * It puts testing data in the session storage such
 * that the interface has data to display.
 * This method is for testing purpose only.
 */
function beedleBotServingMockup() {
	setItem('isActive', false);
	
	setItem('state', states['inactive']);
	setItem('phase', phases['awaitingDelivery']);
	
	setItem('curLifepoints', 321);
	setItem('maxLifepoints', 350);
	setItem('gold', 53502);
	setItem('inventorySize', 295);
	setItem('maxInventorySize', 716);
	setItem('waitingTime', 110);
	
	setItem('totalCost', 19838);
	setItem('totalProfit', 21949);
	
	var valueSeparator = itemEntryFormat['valueSeparator'];
	var entrySeparator = itemEntryFormat['entrySeparator'];
	var itemEntries = '1490796899' + valueSeparator + 'Seelenkapsel'
		+ valueSeparator + '1326' + valueSeparator + '1837' + entrySeparator + 
		'1490796902' + valueSeparator + 'Wakrudpilz'
		+ valueSeparator + '40' + valueSeparator + '45';
	setItem('itemEntries', itemEntries);
	
	setItem('isBeedleBotServing', true);
}

/*
 * Formats the given number by adding a dot as thousand
 * separator every three digits.
 * @param x The number to format
 * @returns The formatted number
 */
function numberFormat(x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
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
				<td id="beedleIconPanel"></td>\
			</tr>\
			<tr>\
				<td id="beedleItemPanel"></td>\
			</tr>\
		</table>');
	$('#beedleStatusPanel, #beedleItemPanel').attr('colspan', 2);
		
	// Create the status panel layout
	$('#beedleStatusPanel').append('<table id="beedleStatusPanelLayout">\
			<tr>\
				<td id="beedleStateCell" class="beedleStatusLeftOuter beedleStatusUpper"></td>\
				<td id="beedlePhaseAnalyseCell" class="beedleStatusRightOuter beedleStatusUpper"></td>\
			</tr>\
			<tr>\
				<td id="beedleActivationCell" class="beedleStatusLeftOuter beedleStatusInner"></td>\
				<td id="beedlePhasePurchaseCell" class="beedleStatusRightOuter beedleStatusInner"></td>\
			</tr>\
			<tr>\
				<td class="beedleStatusLeftOuter beedleStatusLower"></td>\
				<td id="beedlePhaseWaitCell" class="beedleStatusRightOuter beedleStatusLower"></td>\
			</tr>\
		</table>');
		
	// Create the value panel layout
	$('#beedleValuePanel').append('<table id="beedleValuePanelLayout">\
			<tr>\
				<td id="beedleLifepointIconCell" class="valueIcon"></td>\
				<td id="beedleLifepointCell" class="valueText"></td>\
			</tr>\
			<tr>\
				<td id="beedleGoldIconCell" class="valueIcon"></td>\
				<td id="beedleGoldCell" class="valueText"></td>\
			</tr>\
			<tr>\
				<td id="beedleInventoryIconCell" class="valueIcon"></td>\
				<td id="beedleInventoryCell" class="valueText"></td>\
			</tr>\
			<tr>\
				<td id="beedleWaitingTimeIconCell" class="valueIcon"></td>\
				<td id="beedleWaitingTimeCell" class="valueText"></td>\
			</tr>\
		</table>');
		
	// Create the item panel layout
	$('#beedleItemPanel').append('<table id="beedleItemPanelLayout">\
			<tr class="beedleItemHeader">\
				<td class="beedleItemName"></td>\
				<td id="beedleTotalCostCell" class="beedleItemCost" title="'
					+ localization['totalCost'] + '">19.838</td>\
				<td id="beedleTotalProfitCell" class="beedleItemProfit" title="'
					+ localization['totalProfit'] + '">+21.949</td>\
			</tr>\
			<tr class="beedleItemEntryRow">\
				<td class="beedleItemName">Seelenkapsel</td>\
				<td class="beedleItemCost">1.326</td>\
				<td class="beedleItemProfit">1.837</td>\
			</tr>\
			<tr class="beedleItemEntryRow">\
				<td class="beedleItemName">Wakrudpilz</td>\
				<td class="beedleItemCost">40</td>\
				<td class="beedleItemProfit">45</td>\
			</tr>\
		</table>');
}

/*
 * Creates the content of the navigation.
 */
function createNavigationContent() {

}

/*
 * Creates the content of the hidebar.
 */
function createHidebarContent() {
	var hidebar = $('#beedleHidebar');
	// Add hide-show click event handler
	$(hidebar).click(toggleInterface);
	
	// Add tooltip
	var isBeedleInterfaceVisible = getItem('isBeedleInterfaceVisible');
	if (isBeedleInterfaceVisible) {
		$(hidebar).prop('title', localization['hideInterface']);
	} else {
		$(hidebar).prop('title', localization['showInterface']);
	}
}

/*
 * Creates the content of the status panel.
 */
function createStatusPanelContent() {
	$('#beedleStateCell').append(buildStatusRibbonTable('ribbonState', localization['ribbonState']));
	
	$('#beedlePhaseAnalyseCell').append(buildStatusRibbonTable('ribbonAnalyse', localization['ribbonAnalyse']));
	$('#beedlePhasePurchaseCell').append(buildStatusRibbonTable('ribbonPurchase', localization['ribbonPurchase']));
	$('#beedlePhaseWaitCell').append(buildStatusRibbonTable('ribbonWait', localization['ribbonWait']));
	
	$('#beedleActivationCell').append('<table id="statusActivationTable">\
			<tr>\
				<td id="statusActivationOnCell">\
					<img id="activationOn" class="activationIsOn" title="' + localization['activationStart']['on']
						+ '" alt="start" src="' + images['start']['on'] + '"></img>\
				</td>\
				<td id="statusActivationOffCell">\
					<img id="activationOff" class="activationIsOff" title="' + localization['activationStop']['off']
						+ '" alt="stop" src="' + images['stop']['off'] + '"></img>\
				</td>\
			</tr>\
		</table>');
		
	// Add start-stop click event handler
	$('#activationOn').click(sendOnSignal);
	$('#activationOff').click(sendOffSignal);
}

/*
 * Builds and returns the given content of the status ribbon table.
 * By default the ribbon is created with a gray ribbon.
 * @param ribbonId The id for the ribbon cell
 * @param ribbonText The text for the ribbon text cell
 * @returns The build status ribbon table
 */
function buildStatusRibbonTable(ribbonId, ribbonText) {
	var table = '<table class="statusRibbonTable">\
			<tr>\
				<td id="' + ribbonId + '" class="statusRibbon grayRibbon"></td>\
				<td class="statusRibbonText">' + ribbonText + '</td>\
			</tr>\
		</table>';
	return table;
}

/*
 * Creates the content of the value panel.
 */
function createValuePanelContent() {
	$('#beedleLifepointIconCell').append(buildValueIcon('lifepointsIcon', 'lifepoints', 'lifepoints', images['lifepoints']));
	$('#beedleGoldIconCell').append(buildValueIcon('goldIcon', 'gold', 'gold', images['gold']));
	$('#beedleInventoryIconCell').append(buildValueIcon('inventoryIcon', 'inventory', 'inventory', images['inventory']));
	$('#beedleWaitingTimeIconCell').append(buildValueIcon('waitingTimeIcon', 'waitingTime', 'waiting time', images['waitingTime']));
}

/*
 * Builds and returns the content of the given value icon.
 * @param iconId The id for the value icon image
 * @param localizationKey The key for the localization message to show when hovering the icon
 * @param iconAlt The alternative text of the icon image
 * @param iconSrc The source of the icon image
 * @returns The build value icon
 */
function buildValueIcon(iconId, localizationKey, iconAlt, iconSrc) {
	var valueIcon = '<img id="' + iconId + '" title="' + localization[localizationKey]
		+ '" alt="' + iconAlt + '" src="' + iconSrc + '"></img>';
	return valueIcon;
}

/*
 * Creates the content of the icon panel.
 */
function createIconPanelContent() {
	$('#beedleIconPanel').append('<img alt="beedleIcon" src="' + images['icon'] + '"></img>');
}

/*
 * Creates the content of the item panel.
 */
function createItemPanelContent() {

}

/*
 * Updates the data and display of the interface. If BeedleBot is not
 * serving anymore it will remove the interface.
 * Once called the method will call itself every half second.
 */
function update() {
	// If server is offline, stop the interface
	if (!getItem('isBeedleBotServing')) {
		toggleInterface();
		$('#beedleInterface').remove();
		return;
	}
	
	updateStatusPanel();
	updateValuePanel();
	updateItemPanel();
	
	window.setTimeout(update, 500);
}

/*
 * Updates the data and display of the status panel.
 */
function updateStatusPanel() {
	// Update state
	var state = getItem('state');
	
	var ribbonState = $('#ribbonState');
	$(ribbonState).removeClass('grayRibbon blueRibbon greenRibbon redRibbon');
	
	if (state == states['standby']) {
		$(ribbonState).addClass('blueRibbon');
	} else if (state == states['active']) {
		$(ribbonState).addClass('greenRibbon');
	} else if (state == states['problem']) {
		$(ribbonState).addClass('redRibbon');
	} else {
		$(ribbonState).addClass('grayRibbon');
	}
	
	// Update phase
	var phase = getItem('phase');
	var ribbonAnalyse = $('#ribbonAnalyse');
	var ribbonPurchase = $('#ribbonPurchase');
	var ribbonWait = $('#ribbonWait');
	
	if (phase == phases['analyse']) {
		$(ribbonAnalyse).removeClass('grayRibbon');
		$(ribbonAnalyse).addClass('blueRibbon');
	} else {
		$(ribbonAnalyse).removeClass('blueRibbon');
		$(ribbonAnalyse).addClass('grayRibbon');
	}
	
	if (phase == phases['purchase']) {
		$(ribbonPurchase).removeClass('grayRibbon');
		$(ribbonPurchase).addClass('blueRibbon');
	} else {
		$(ribbonPurchase).removeClass('blueRibbon');
		$(ribbonPurchase).addClass('grayRibbon');
	}
	
	if (phase == phases['wait']) {
		$(ribbonWait).removeClass('grayRibbon');
		$(ribbonWait).addClass('blueRibbon');
	} else {
		$(ribbonWait).removeClass('blueRibbon');
		$(ribbonWait).addClass('grayRibbon');
	}
	
	// Update activation images
	var isActive = getItem('isActive');
	var startIcon = $('#activationOn');
	var stopIcon = $('#activationOff');
	if (isActive) {
		if ($(startIcon).hasClass('activationIsOn')) {
			// Deactivate start icon
			$(startIcon).attr('src', images['start']['off']);
			$(startIcon).attr('title', localization['activationStart']['off']);
			$(startIcon).removeClass('activationIsOn');
			$(startIcon).addClass('activationIsOff');
		}
		if ($(startIcon).hasClass('activationIsOff')) {
			// Activate stop icon
			$(stopIcon).attr('src', images['stop']['on']);
			$(stopIcon).attr('title', localization['activationStop']['on']);
			$(stopIcon).removeClass('activationIsOff');
			$(stopIcon).addClass('activationIsOn');
		}
	} else {
		if ($(startIcon).hasClass('activationIsOff')) {
			// Activate start icon
			$(startIcon).attr('src', images['start']['on']);
			$(startIcon).attr('title', localization['activationStart']['on']);
			$(startIcon).removeClass('activationIsOff');
			$(startIcon).addClass('activationIsOn');
		}
		if ($(startIcon).hasClass('activationIsOn')) {
			// Deactivate stop icon
			$(stopIcon).attr('src', images['stop']['off']);
			$(stopIcon).attr('title', localization['activationStop']['off']);
			$(stopIcon).removeClass('activationIsOn');
			$(stopIcon).addClass('activationIsOff');
		}
	}
}

/*
 * Updates the data and display of the value panel.
 */
function updateValuePanel() {
	var valueSeparator = ' / ';
	
	// Update lifepoints
	var curLifepoints = numberFormat(getItem('curLifepoints'));
	var maxLifepoints = numberFormat(getItem('maxLifepoints'));
	
	$('#beedleLifepointCell').text(curLifepoints + valueSeparator + maxLifepoints);
	
	// Update gold
	var gold = numberFormat(getItem('gold'));
	
	$('#beedleGoldCell').text(gold);
	
	// Update inventory size
	var inventorySize = numberFormat(getItem('inventorySize'));
	var maxInventorySize = numberFormat(getItem('maxInventorySize'));
	
	$('#beedleInventoryCell').text(inventorySize + valueSeparator + maxInventorySize);
	
	// Waiting time
	var totalTimeSeconds = Number(getItem('waitingTime'));
	var timeMinutes = Math.floor(totalTimeSeconds / 60);
	var timeSeconds = totalTimeSeconds - (timeMinutes * 60);
	
	var waitingTimeText = '';
	if (timeMinutes > 0) {
		waitingTimeText += timeMinutes + 'm ';
	}
	waitingTimeText += timeSeconds + 's';
	
	$('#beedleWaitingTimeCell').text(waitingTimeText);
	
}

/*
 * Updates the data and display of the item panel.
 */
function updateItemPanel() {
	//var curTimestamp = Number(new Date());
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
		$(beedleInterface).removeClass('hiddenInterface');
		
		$(beedleInterface).css('left', itemFrameLeft);
		$(beedleInterface).width(itemFrameWidth);
		$(content).show();
		$(hidebar).prop('title', localization['hideInterface']);
		
		setItem('isBeedleInterfaceVisible', true);
	} else {
		// Hide the interface
		$(beedleInterface).css('left', itemFrameLeft + (itemFrameWidth - hidebarWidth));
		$(beedleInterface).width(hidebarWidth);
		$(content).hide();
		$(hidebar).prop('title', localization['showInterface']);
		
		$(beedleInterface).addClass('hiddenInterface');
		setItem('isBeedleInterfaceVisible', false);
	}
}

/*
 * Sends an on signal to BeedleBot if the start icon is active.
 */
function sendOnSignal() {
	// Abort if icon is not active
	if (!$('#activationOn').hasClass('activationIsOn')) {
		return;
	}
	
	setItem('startSignal', true);
}

/*
 * Sends an off signal to BeedleBot if the stop icon is active.
 */
function sendOffSignal() {
	// Abort if icon is not active
	if (!$('#activationOff').hasClass('activationIsOn')) {
		return;
	}
	
	setItem('stopSignal', true);
}

/*
 * Creates and loads the web user interface.
 */
function loadInterface() {
	// TODO Remove testing mockup
	beedleBotServingMockup();
	
	// Web storage is necessary for BeedleBots communication
	if (!isSupportingWebStorage()) {
		return;
	}
	
	// Only load the interface if BeedleBot is serving
	if (!getItem('isBeedleBotServing')) {
		return;
	}
	
	setItem('startSignal', false);
	setItem('stopSignal', false);
	
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
	
	// Create the content for every element
	createNavigationContent();
	createHidebarContent();
	createStatusPanelContent();
	createValuePanelContent();
	createIconPanelContent();
	createItemPanelContent();
	
	// Update the data and display of the interface
	update();
		
	// Hide the interface initially or if already hidden
	var isBeedleInterfaceVisible = getItem('isBeedleInterfaceVisible');
	if (isBeedleInterfaceVisible == null || !isBeedleInterfaceVisible) {
		toggleInterface();
	}
}

// Storage key constants
var storageKeys = new Object();
storageKeys['keyIndex'] = 'beedle_';
storageKeys['isBeedleInterfaceVisible'] = 'isBeedleInterfaceVisible';
storageKeys['isBeedleBotServing'] = 'isBeedleBotServing';
storageKeys['isActive'] = 'isActive';
storageKeys['state'] = 'state';
storageKeys['phase'] = 'phase';
storageKeys['startSignal'] = 'startSignal';
storageKeys['stopSignal'] = 'stopSignal';
storageKeys['curLifepoints'] = 'curLife';
storageKeys['maxLifepoints'] = 'maxLife';
storageKeys['gold'] = 'gold';
storageKeys['inventorySize'] = 'invSize';
storageKeys['maxInventorySize'] = 'maxInvSize';
storageKeys['waitingTime'] = 'waitingTime';
storageKeys['totalCost'] = 'totalCost';
storageKeys['totalProfit'] = 'totalProfit';
storageKeys['itemEntries'] = 'itemEntries';

// BeedleBots states
var states = new Object();
states['inactive'] = 'INACTIVE';
states['standby'] = 'STANDBY';
states['active'] = 'ACTIVE';
states['problem'] = 'PROBLEM';

// BeedleBots phases
var phases = new Object();
phases['analyse'] = 'ANALYSE';
phases['awaitingDelivery'] = 'AWAITING_DELIVERY';
phases['purchase'] = 'PURCHASE';
phases['wait'] = 'WAIT';

// Item entry format constants
var itemEntryFormat = new Object();
itemEntryFormat['valueSeparator'] = '?';
itemEntryFormat['entrySeparator'] = ';';

// Image ressource constants
var images = new Object();
images['icon'] = 'http://file1.npage.de/005000/36/bilder/webicon.png';
images['start'] = new Object();
images['start']['on'] = 'http://file1.npage.de/005000/36/bilder/webstarton.png';
images['start']['off'] = 'http://file1.npage.de/005000/36/bilder/webstartoff.png';
images['stop'] = new Object();
images['stop']['on'] = 'http://file1.npage.de/005000/36/bilder/webstopon.png';
images['stop']['off'] = 'http://file1.npage.de/005000/36/bilder/webstopoff.png';
images['lifepoints'] = 'http://file1.npage.de/005000/36/bilder/weblifepoints.png';
images['gold'] = 'http://file1.npage.de/005000/36/bilder/webgold.png';
images['inventory'] = 'http://file1.npage.de/005000/36/bilder/webinventory.png';
images['waitingTime'] = 'http://file1.npage.de/005000/36/bilder/webwaitingtime.png';

// Localization constants
var localization = new Object();
localization['hideInterface'] = 'Interface ausblenden';
localization['showInterface'] = 'Interface einblenden';
localization['ribbonState'] = 'Status';
localization['ribbonAnalyse'] = 'Analyse';
localization['ribbonPurchase'] = 'Kaufen';
localization['ribbonWait'] = 'Warten';
localization['activationStart']  = new Object();
localization['activationStart']['on'] = 'Starte Ankauf';
localization['activationStart']['off'] = 'Ankauf ist bereits gestartet';
localization['activationStop'] = new Object();
localization['activationStop']['on'] ='Stoppe Ankauf';
localization['activationStop']['off'] = 'Ankauf ist bereits gestoppt';
localization['lifepoints'] = 'aktuelle / maximale Lebenspunkte';
localization['gold'] = 'Goldm&uuml;nzen';
localization['inventory'] = 'aktuelle / maximale Inventargr&ouml;&szlig;e';
localization['waitingTime'] = 'Wartezeit pro Itemkauf';
localization['totalCost'] = 'gesamte Kosten';
localization['totalProfit'] = 'gesamte Einnahmen';

// Load the web user interface
loadInterface();