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
				z-index: 5;\
			}\
			\
			#beedleLayout, #beedleContentLayout, #beedleStatusPanelLayout,\
			#beedleValuePanelLayout, #beedleItemPanelLayout {\
				left: 0px;\
				top: 0px;\
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
			#beedleValuePanelLayout td, #beedleItemPanelLayout td, #beedleItemPanelLayout th {\
				margin: 0px;\
				padding: 0px;\
				text-align: center;\
			}\
			\
			.beedleTab, .statusRibbonText, .valueText, .beedleItemName,\
			.beedleItemCost, .beedleItemProfit {\
				font-family: Arial, helvetica, sans-serif;\
				color: #222;\
				font-size: 12px;\
			}\
			\
			#beedleNavigation {\
				height: 30px;\
				background-color: #778899;\
			}\
			\
			#beedleHidebar {\
				width: 10px;\
				background-color: #B7B7B7;\
			}\
			\
			#beedleContent {\
				background-color: white;\
				border: 1px solid black;\
				border-top-width: 0px;\
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
			#beedleTabNavigation {\
				overflow: hidden;\
				border: 1px solid #505A67;\
				border-bottom-width: 3px;\
				background-color: #778899;\
			}\
			\
			.beedleTab {\
				color: white;\
				font-size: 13px;\
				font-weight: bold;\
				background-color: inherit;\
				float: left;\
				border: none;\
				outline: none;\
				cursor: pointer;\
				padding: 4px 6px;\
				transition: 0.3s;\
			}\
			\
			.beedleTab:hover {\
				background-color: #6A798A;\
			}\
			\
			.beedleTab.activeTab {\
				background-color: #505A67;\
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
				font-size: 18px;\
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
			.valueText, .valueText span {\
				font-weight: bold;\
				text-align: left !important;\
				padding-left: 5px !important;\
				font-size: 16px;\
			}\
			\
			.valueMedium {\
				color: #FFAA37;\
			}\
			\
			.valueCritical {\
				color: #D24444;\
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
			#beedleItemHeader {\
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
 * Mockup which acts as Beedle Bot Server.
 * It puts testing data in the session storage such
 * that the interface has data to display.
 * This method is for testing purpose only.
 */
function beedleBotServingMockup() {
	setItem('isActive', false);

	setItem('state', states.inactive);
	setItem('phase', phases.awaitingDelivery);

	setItem('curLifepoints', 301);
	setItem('maxLifepoints', 350);
	setItem('gold', 5304);
	setItem('inventorySize', 453);
	setItem('maxInventorySize', 716);
	setItem('waitingTime', 8);

	setItem('totalCost', 19838);
	setItem('totalProfit', 21949);

	var valueSeparator = itemEntryFormat.valueSeparator;
	var entrySeparator = itemEntryFormat.entrySeparator;
	var itemEntries = '1490796899' + valueSeparator + 'Seelenkapsel' +
		valueSeparator + '1326' + valueSeparator + '1837' + entrySeparator +
		'1490796902' + valueSeparator + 'Wakrudpilz' +
		valueSeparator + '40' + valueSeparator + '45';
	setItem('itemEntries', itemEntries);

	setItem('isBeedleBotServing', true);
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
 *    to the given key identifier
 */
function buildKey(key) {
	return storageKeys.keyIndex + storageKeys[key];
}

/*
 * Gets the value of the item given by its key from the session storage.
 * @returns The value of item given by its key or null if not defined
 */
function getItem(key) {
	var keyOfStorage = buildKey(key);
	var value = sessionStorage.getItem(keyOfStorage);
	if (value === null || value === '' || value == 'undefined') {
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
 * Formats the given number by adding a dot as thousand
 * separator every three digits.
 * @param x The number to format
 * @returns The formatted number
 */
function numberFormat(x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

/*
 * Fires a notification sound.
 */
function fireNotification() {
	notificationSound.play();
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
 * Builds and returns the content of the given value icon.
 * @param iconId The id for the value icon image
 * @param localizationKey The key for the localization message to show when hovering the icon
 * @param iconAlt The alternative text of the icon image
 * @param iconSrc The source of the icon image
 * @returns The build value icon
 */
function buildValueIcon(iconId, localizationKey, iconAlt, iconSrc) {
	var valueIcon = '<img id="' + iconId + '" title="' + localization[localizationKey] +
		'" alt="' + iconAlt + '" src="' + iconSrc + '"></img>';
	return valueIcon;
}

/*
 * Toggles the visibility of the interface.
 */
function toggleInterface() {
	var itemFrame = $('body');
	var beedleInterface = $('#beedleInterface');
	var hidebar = $('#beedleHidebar');
	var tabNavigation = $('#beedleTabNavigation');
	var content = $('#beedleContent');

	var itemFrameWidth = $(itemFrame)[0].scrollWidth;
	var itemFrameLeft = $(itemFrame).position().left;
	var hidebarWidth = $(hidebar)[0].scrollWidth;

	if ($(beedleInterface).hasClass('hiddenInterface')) {
		// Show the interface
		$(beedleInterface).removeClass('hiddenInterface');

		$(beedleInterface).css('left', itemFrameLeft);
		$(beedleInterface).width(itemFrameWidth);
		$(tabNavigation).show();
		$(content).show();
		$(hidebar).prop('title', localization.hideInterface);

		setItem('isBeedleInterfaceVisible', true);
	} else {
		// Hide the interface
		$(beedleInterface).css('left', itemFrameLeft + (itemFrameWidth - hidebarWidth));
		$(beedleInterface).width(hidebarWidth);
		$(tabNavigation).hide();
		$(content).hide();
		$(hidebar).prop('title', localization.showInterface);

		$(beedleInterface).addClass('hiddenInterface');
		setItem('isBeedleInterfaceVisible', false);
	}
}

/*
 * Opens and loads the given tab to the content panel.
 * @param event An event object with a parameter named 'data' holding a parameter
 *    'tabName' which contains the name of the tab to open. Optionally also a 'forceLoad'
 *    parameter can be set, if true the method will load the tab regardless of
 *    wether it is already loaded or not.
 */
function openTab(event) {
	var tabName = event.data.tabName;
	var forceLoad = event.data.forceLoad;
	var currentInterfaceTab = getItem('currentInterfaceTab');

	// Abort if tab is already loaded
	if (tabName == currentInterfaceTab && !forceLoad) {
		return;
	}

	// Remove current content
	$('#beedleContent').empty();

	// Load the correct tab
	if (tabName == 'sell') {
		openSellTab();
	} else if (tabName == 'miscellaneous') {
		openMiscellaneousTab();
	} else {
		tabName = 'purchase';
		openPurchaseTab();
	}

	// Remove all tab active classes
	$('.beedleTab').removeClass('activeTab');

	// Set the current navigation tab as active
	$('#' + tabName + 'Tab').addClass('activeTab');

	// Set the current loaded tab
	setItem('currentInterfaceTab', tabName);

	update(true);
}

/*
 * Opens and loads the purchase tab to the content panel.
 */
function openPurchaseTab() {
	createPurchaseTabLayout();
	createPurchaseTabContent();
}

/*
 * Opens and loads the sell tab to the content panel.
 */
function openSellTab() {

}

/*
 * Opens and loads the miscellaneous tab to the content panel.
 */
function openMiscellaneousTab() {

}

/*
 * Creates the layout of the web user interface.
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
}

/*
 * Creates the layout of the purchase tab.
 */
function createPurchaseTabLayout() {
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
			<thead>\
				<tr id="beedleItemHeader">\
					<th class="beedleItemName"></td>\
					<th id="beedleTotalCostCell" class="beedleItemCost" title="' +
						localization.totalCost + '">0</td>\
					<th id="beedleTotalProfitCell" class="beedleItemProfit" title="' +
						localization.totalProfit + '">+0</td>\
				</tr>\
			</thead>\
			<tbody>\
			</tbody>\
		</table>');
}

/*
 * Creates the content of the navigation.
 */
function createNavigationContent() {
	$('#beedleNavigation').append('<div id="beedleTabNavigation">\
			<button class="beedleTab" id="purchaseTab">' +
				localization.purchaseTab + '</button>\
			<button class="beedleTab" id="sellTab">' +
				localization.sellTab + '</button>\
			<button class="beedleTab" id="miscellaneousTab">' +
				localization.miscellaneousTab + '</button>\
		</div>');

	// Add click event handler
	$('#purchaseTab').click({tabName : 'purchase'}, openTab);
	$('#sellTab').click({tabName : 'sell'}, openTab);
	$('#miscellaneousTab').click({tabName : 'miscellaneous'}, openTab);
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
		$(hidebar).prop('title', localization.hideInterface);
	} else {
		$(hidebar).prop('title', localization.showInterface);
	}
}

/*
 * Creates the content of the purchase tab.
 */
function createPurchaseTabContent() {
	createStatusPanelContent();
	createValuePanelContent();
	createIconPanelContent();
	createItemPanelContent();
}

/*
 * Creates the content of the status panel.
 */
function createStatusPanelContent() {
	$('#beedleStateCell').append(buildStatusRibbonTable('ribbonState', localization.ribbonState));

	$('#beedlePhaseAnalyseCell').append(buildStatusRibbonTable('ribbonAnalyse', localization.ribbonAnalyse));
	$('#beedlePhasePurchaseCell').append(buildStatusRibbonTable('ribbonPurchase', localization.ribbonPurchase));
	$('#beedlePhaseWaitCell').append(buildStatusRibbonTable('ribbonWait', localization.ribbonWait));

	$('#beedleActivationCell').append('<table id="statusActivationTable">\
			<tr>\
				<td id="statusActivationOnCell">\
					<img id="activationOn" class="activationIsOn" title="' + localization.activationStart.on +
						'" alt="start" src="' + images.start.on + '"></img>\
				</td>\
				<td id="statusActivationOffCell">\
					<img id="activationOff" class="activationIsOff" title="' + localization.activationStop.off +
						'" alt="stop" src="' + images.stop.off + '"></img>\
				</td>\
			</tr>\
		</table>');

	// Add start-stop click event handler
	$('#activationOn').click(sendOnSignal);
	$('#activationOff').click(sendOffSignal);
}

/*
 * Creates the content of the value panel.
 */
function createValuePanelContent() {
	$('#beedleLifepointIconCell').append(
		buildValueIcon('lifepointsIcon', 'lifepoints', 'lifepoints', images.lifepoints));
	$('#beedleGoldIconCell').append(
		buildValueIcon('goldIcon', 'gold', 'gold', images.gold));
	$('#beedleInventoryIconCell').append(
		buildValueIcon('inventoryIcon', 'inventory', 'inventory', images.inventory));
	$('#beedleWaitingTimeIconCell').append(
		buildValueIcon('waitingTimeIcon', 'waitingTime', 'waiting time', images.waitingTime));
}

/*
 * Creates the content of the icon panel.
 */
function createIconPanelContent() {
	$('#beedleIconPanel').append('<img alt="beedleIcon" src="' + images.icon + '"></img>');
}

/*
 * Creates the content of the item panel.
 */
function createItemPanelContent() {

}

/*
 * Updates the data and display of the interface. If BeedleBot is not
 * serving anymore it will remove the interface.
 * @param preventLoop If true the method will not call itself every half second, if false it will
 */
function update(preventLoop) {
	// If server is offline, stop the interface
	if (!getItem('isBeedleBotServing')) {
		toggleInterface();
		$('#beedleInterface').remove();
		// Try again later
		window.setTimeout(loadInterface, 500);
		return;
	}

	// Update the correct tab
	var currentInterfaceTab = getItem('currentInterfaceTab');
	if (currentInterfaceTab == 'sell') {
		updateSellTab();
	} else if (currentInterfaceTab == 'miscellaneous') {
		updateMiscellaneousTab();
	} else {
		updatePurchaseTab();
	}

	if (!preventLoop) {
		window.setTimeout(update, 500);
	}
}

/*
 * Updates the data and display of the purchase tab.
 */
function updatePurchaseTab() {
	updateStatusPanel();
	updateValuePanel();
	updateItemPanel();
}

/*
 * Updates the data and display of the sell tab.
 */
function updateSellTab() {

}

/*
 * Updates the data and display of the miscellaneous tab.
 */
function updateMiscellaneousTab() {

}

/*
 * Updates the data and display of the status panel.
 */
function updateStatusPanel() {
	// Update state
	var state = getItem('state');

	var ribbonState = $('#ribbonState');

	// Determine if a problem is new if there is one
	var isBeedleProblemKnown = getItem('isBeedleProblemKnown');
	if (state == states.problem && !$(ribbonState).hasClass('redRibbon') && !isBeedleProblemKnown) {
		setItem('isBeedleProblemKnown', true);
		// Fire a notification
		fireNotification();
	} else if (state != states.problem && isBeedleProblemKnown) {
		setItem('isBeedleProblemKnown', false);
	}

	// Update the ribbon
	$(ribbonState).removeClass('grayRibbon blueRibbon greenRibbon redRibbon');
	if (state == states.standby) {
		$(ribbonState).addClass('blueRibbon');
	} else if (state == states.active) {
		$(ribbonState).addClass('greenRibbon');
	} else if (state == states.problem) {
		$(ribbonState).addClass('redRibbon');
	} else {
		$(ribbonState).addClass('grayRibbon');
	}

	// Update phase
	var phase = getItem('phase');
	var ribbonAnalyse = $('#ribbonAnalyse');
	var ribbonPurchase = $('#ribbonPurchase');
	var ribbonWait = $('#ribbonWait');

	if (phase == phases.analyse) {
		$(ribbonAnalyse).removeClass('grayRibbon');
		$(ribbonAnalyse).addClass('blueRibbon');
	} else {
		$(ribbonAnalyse).removeClass('blueRibbon');
		$(ribbonAnalyse).addClass('grayRibbon');
	}

	if (phase == phases.purchase) {
		$(ribbonPurchase).removeClass('grayRibbon');
		$(ribbonPurchase).addClass('blueRibbon');
	} else {
		$(ribbonPurchase).removeClass('blueRibbon');
		$(ribbonPurchase).addClass('grayRibbon');
	}

	if (phase == phases.wait) {
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
			$(startIcon).attr('src', images.start.off);
			$(startIcon).attr('title', localization.activationStart.off);
			$(startIcon).removeClass('activationIsOn');
			$(startIcon).addClass('activationIsOff');
		}
		if ($(startIcon).hasClass('activationIsOff')) {
			// Activate stop icon
			$(stopIcon).attr('src', images.stop.on);
			$(stopIcon).attr('title', localization.activationStop.on);
			$(stopIcon).removeClass('activationIsOff');
			$(stopIcon).addClass('activationIsOn');
		}
	} else {
		if ($(startIcon).hasClass('activationIsOff')) {
			// Activate start icon
			$(startIcon).attr('src', images.start.on);
			$(startIcon).attr('title', localization.activationStart.on);
			$(startIcon).removeClass('activationIsOff');
			$(startIcon).addClass('activationIsOn');
		}
		if ($(startIcon).hasClass('activationIsOn')) {
			// Deactivate stop icon
			$(stopIcon).attr('src', images.stop.off);
			$(stopIcon).attr('title', localization.activationStop.off);
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
	var curLifepoints = Number(getItem('curLifepoints'));
	var maxLifepoints = Number(getItem('maxLifepoints'));

	// Determine value status
	var valueClass = 'valueOk';
	var ratio = curLifepoints / maxLifepoints;
	if (ratio <= valueStatus.lifepoints.critical.ratio ||
		curLifepoints <= valueStatus.lifepoints.critical.min) {
		// Critical status
		valueClass = 'valueCritical';
	} else if (ratio <= valueStatus.lifepoints.medium.ratio ||
		curLifepoints <= valueStatus.lifepoints.medium.min) {
		// Medium status
		valueClass = 'valueMedium';
	}

	// Set value
	curLifepoints = numberFormat(curLifepoints);
	maxLifepoints = numberFormat(maxLifepoints);
	$('#beedleLifepointCell').html('<span class="' + valueClass + '">' +
		curLifepoints + '</span>' + valueSeparator + maxLifepoints);

	// Update gold
	var gold = Number(getItem('gold'));

	// Determine value status
	valueClass = 'valueOk';
	if (gold <= valueStatus.gold.critical) {
		// Critical status
		valueClass = 'valueCritical';
	} else if (gold <= valueStatus.gold.medium) {
		// Medium status
		valueClass = 'valueMedium';
	}

	// Set value
	gold = numberFormat(gold);
	$('#beedleGoldCell').html('<span class="' + valueClass + '">' +
		gold + '</span>');

	// Update inventory size
	var inventorySize = Number(getItem('inventorySize'));
	var maxInventorySize = Number(getItem('maxInventorySize'));

	// Determine value status
	valueClass = 'valueOk';
	ratio = inventorySize / maxInventorySize;
	var minDiff = maxInventorySize - inventorySize;
	if (ratio >= valueStatus.inventory.critical.ratio ||
		inventorySize <= valueStatus.inventory.critical.minDiff) {
		// Critical status
		valueClass = 'valueCritical';
	} else if (ratio >= valueStatus.inventory.medium.ratio ||
		inventorySize <= valueStatus.inventory.medium.minDiff) {
		// Medium status
		valueClass = 'valueMedium';
	}

	// Set value
	inventorySize = numberFormat(inventorySize);
	maxInventorySize = numberFormat(maxInventorySize);
	$('#beedleInventoryCell').html('<span class="' + valueClass + '">' +
		inventorySize + '</span>' + valueSeparator + maxInventorySize);

	// Waiting time
	var totalTimeSeconds = Number(getItem('waitingTime'));

	// Determine value status
	valueClass = 'valueOk';
	if (totalTimeSeconds >= valueStatus.waitingTime.critical) {
		// Critical status
		valueClass = 'valueCritical';
	} else if (totalTimeSeconds >= valueStatus.waitingTime.medium) {
		// Medium status
		valueClass = 'valueMedium';
	}

	// Set value
	var timeMinutes = Math.floor(totalTimeSeconds / 60);
	var timeSeconds = totalTimeSeconds - (timeMinutes * 60);

	var waitingTimeText = '';
	if (timeMinutes > 0) {
		waitingTimeText += timeMinutes + 'm ';
	}
	waitingTimeText += timeSeconds + 's';

	$('#beedleWaitingTimeCell').html('<span class="' + valueClass + '">' +
		waitingTimeText + '</span>');
}

/*
 * Updates the data and display of the item panel.
 */
function updateItemPanel() {
	var itemEntriesText = getItem('itemEntries');
	if (itemEntriesText === null || itemEntriesText === '') {
		return;
	}

	// Determine timestamp of newest currently displayed element
	var newestTimestampElement = $('#beedleItemPanelLayout tbody .beedleItemEntryRow .beedleItemName input').first();
	var newestTimestamp = 0;
	if (newestTimestampElement.length > 0) {
		// There are already items displayed, get the timestamp
		newestTimestamp = Number($(newestTimestampElement).val());
	}

	// Iterate and add newest entries
	var itemEntries = itemEntriesText.split(itemEntryFormat.entrySeparator);
	for (i = 0; i < itemEntries.length; i++) {
		var itemData = itemEntries[i].split(itemEntryFormat.valueSeparator);

		var itemTimestamp = Number(itemData[0]);
		// Skip element if it is already displayed
		if (itemTimestamp <= newestTimestamp) {
			continue;
		}

		var itemName = itemData[1];
		var itemCost = Number(itemData[2]);
		var itemProfit = Number(itemData[3]);

		// Append item
		$('#beedleItemPanelLayout tbody').prepend('<tr class="beedleItemEntryRow">\
				<td class="beedleItemName">' + itemName +
					'<input type="hidden" value="' + itemTimestamp + '">\
				</td>\
				<td class="beedleItemCost">' + numberFormat(itemCost) + '</td>\
				<td class="beedleItemProfit">' + numberFormat(itemProfit) + '</td>\
			</tr>');
	}

	// Update total values
	$('#beedleTotalCostCell').text(numberFormat(getItem('totalCost')));
	$('#beedleTotalProfitCell').text(numberFormat('+' + getItem('totalProfit')));
}

/*
 * Creates and loads the web user interface.
 */
function loadInterface() {
	//beedleBotServingMockup();

	// Web storage is necessary for BeedleBots communication
	if (!isSupportingWebStorage()) {
		return;
	}

	// Only load the interface if BeedleBot is serving
	if (!getItem('isBeedleBotServing')) {
		// Try again later
		window.setTimeout(loadInterface, 500);
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

	// Create the content for every element
	createNavigationContent();
	createHidebarContent();

	// Open the correct tab
	var currentInterfaceTab = getItem('currentInterfaceTab');
	if (currentInterfaceTab === null) {
		currentInterfaceTab = 'purchase';
	}
	var event = {};
	event.data = {};
	event.data.tabName = currentInterfaceTab;
	event.data.forceLoad = true;
	openTab(event);

	// Update the data and display of the interface
	update(false);

	// Hide the interface initially or if already hidden
	var isBeedleInterfaceVisible = getItem('isBeedleInterfaceVisible');
	if (isBeedleInterfaceVisible === null || !isBeedleInterfaceVisible) {
		toggleInterface();
	}
}

// Storage key constants
var storageKeys = {};
storageKeys.keyIndex = 'beedle_';
storageKeys.isBeedleInterfaceVisible = 'isBeedleInterfaceVisible';
storageKeys.isBeedleProblemKnown = 'isBeedleProblemKnown';
storageKeys.currentInterfaceTab = 'currentInterfaceTab';
storageKeys.isBeedleBotServing = 'isBeedleBotServing';
storageKeys.isActive = 'isActive';
storageKeys.state = 'state';
storageKeys.phase = 'phase';
storageKeys.startSignal = 'startSignal';
storageKeys.stopSignal = 'stopSignal';
storageKeys.curLifepoints = 'curLife';
storageKeys.maxLifepoints = 'maxLife';
storageKeys.gold = 'gold';
storageKeys.inventorySize = 'invSize';
storageKeys.maxInventorySize = 'maxInvSize';
storageKeys.waitingTime = 'waitingTime';
storageKeys.totalCost = 'totalCost';
storageKeys.totalProfit = 'totalProfit';
storageKeys.itemEntries = 'itemEntries';

// BeedleBots states
var states = {};
states.inactive = 'INACTIVE';
states.standby = 'STANDBY';
states.active = 'ACTIVE';
states.problem = 'PROBLEM';

// BeedleBots phases
var phases = {};
phases.analyse = 'ANALYSE';
phases.awaitingDelivery = 'AWAITING_DELIVERY';
phases.purchase = 'PURCHASE';
phases.wait = 'WAIT';

// Value status constants
var valueStatus = {};
valueStatus.lifepoints = {};
valueStatus.lifepoints.medium = {};
valueStatus.lifepoints.medium.ratio = 0.5;
valueStatus.lifepoints.medium.min = 50;
valueStatus.lifepoints.critical = {};
valueStatus.lifepoints.critical.ratio = 0.16;
valueStatus.lifepoints.critical.min = 10;
valueStatus.gold = {};
valueStatus.gold.medium = 2000;
valueStatus.gold.critical = 500;
valueStatus.inventory = {};
valueStatus.inventory.medium = {};
valueStatus.inventory.medium.ratio = 0.8;
valueStatus.inventory.medium.minDiff = 50;
valueStatus.inventory.critical = {};
valueStatus.inventory.critical.ratio = 0.9;
valueStatus.inventory.critical.minDiff = 10;
valueStatus.waitingTime = {};
valueStatus.waitingTime.medium = 15;
valueStatus.waitingTime.critical = 30;

// Item entry format constants
var itemEntryFormat = {};
itemEntryFormat.valueSeparator = '?';
itemEntryFormat.entrySeparator = ';';

// Image ressource constants
var images = {};
images.icon = 'http://file1.npage.de/005000/36/bilder/webicon.png';
images.start = {};
images.start.on = 'http://file1.npage.de/005000/36/bilder/webstarton.png';
images.start.off = 'http://file1.npage.de/005000/36/bilder/webstartoff.png';
images.stop = {};
images.stop.on = 'http://file1.npage.de/005000/36/bilder/webstopon.png';
images.stop.off = 'http://file1.npage.de/005000/36/bilder/webstopoff.png';
images.lifepoints = 'http://file1.npage.de/005000/36/bilder/weblifepoints.png';
images.gold = 'http://file1.npage.de/005000/36/bilder/webgold.png';
images.inventory = 'http://file1.npage.de/005000/36/bilder/webinventory.png';
images.waitingTime = 'http://file1.npage.de/005000/36/bilder/webwaitingtime.png';

// Localization constants
var localization = {};
localization.purchaseTab = 'Ankauf';
localization.sellTab = 'Verkauf';
localization.miscellaneousTab = 'Sonstiges';
localization.hideInterface = 'Interface ausblenden';
localization.showInterface = 'Interface einblenden';
localization.ribbonState = 'Status';
localization.ribbonAnalyse = 'Analyse';
localization.ribbonPurchase = 'Kaufen';
localization.ribbonWait = 'Warten';
localization.activationStart = {};
localization.activationStart.on = 'Starte Ankauf';
localization.activationStart.off = 'Ankauf ist bereits gestartet';
localization.activationStop = {};
localization.activationStop.on ='Stoppe Ankauf';
localization.activationStop.off = 'Ankauf ist bereits gestoppt';
localization.lifepoints = 'aktuelle / maximale Lebenspunkte';
localization.gold = 'Goldm&uuml;nzen';
localization.inventory = 'aktuelle / maximale Inventargr&ouml;&szlig;e';
localization.waitingTime = 'Wartezeit pro Itemkauf';
localization.totalCost = 'gesamte Kosten';
localization.totalProfit = 'gesamte Einnahmen';

// Notification sound
var notificationSound = document.createElement('audio');
notificationSound.src = 'http://zabuza.square7.ch/freewar/notifier/notification1.mp3';
notificationSound.preload = 'auto';
notificationSound.volume = 0.5;

// Load the web user interface
loadInterface();