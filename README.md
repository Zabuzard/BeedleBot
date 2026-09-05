# BeedleBot

BeedleBot is a tool for the MMORPG [Freewar.de](https://www.freewar.de), which automizes the trade
and the corresponding sale at the central traders depot. It comes with an integrated web user
interface.

![UI](https://i.vgy.me/Lmfcdw.png)

## Setup

When launching, the login window shows and allows entering credentials, the world (Freewar server),
as well as the desired browser to use.

![Login](https://i.vgy.me/lpVhu5.jpg)

Clicking _Settings_ allows configuring details.

![Settings](https://i.vgy.me/mwBwaN.jpg)

It is necessary to enter the executable (binary) and the Selenium driver (Driver) of the browser to
use. For Firefox, optionally, a User Profile can be given.

## Interface

Once started, the bot logs into the account and is ready for use.

On the right side of the inventory a gray bar is shown. Clicking it expands the user interface of
the bot. Clicking it again will collapse the interface.

| UI Closed                              | UI Opened                              |
|----------------------------------------|----------------------------------------|
| ![Closed](https://i.vgy.me/xk5Iui.jpg) | ![Opened](https://i.vgy.me/Kbtf2U.jpg) |

Clicking the _Play_-Button will start to purchase items from the central traders depot. The _Stop_
-Button will pause the bot.

The lower section of the UI will show each purchased item, how much gold it did cost and how much
profit it will make when selling in a shop.

### Phases

#### Analyse

First, the bot will look through each item category and analyze which items are available, how much
profit they make when purchasing etc. This is done by looking up shop prices in FreewarWiki, as well
as checking out player-to-player prices in an external API.

#### Kaufen

Items that make enough profit will then be purchased one by one in this next phase.

#### Warten

After each purchase, the user has to wait some time before purchasing the next item. This phase
alternates with the _Kaufen_ phase.

#### No Phase

With no phase explicitly selected, the bot is waiting for new items to be delivered to the central
traders depot and will then start the process with the first again once.

### Errors

Should the bot run into a problem, it will stop the routine and display a red ribbon on the _Status_
-Badge. The _Sonstiges_-Tab can be used to show the full error message.

![Error](https://i.vgy.me/g4THYM.jpg)

## Selling

The _Verkauf_-Tab is a placeholder and will be used to automatically sell purchased items at a shop.

## Item Configurations

Exceptions for items can be configured in `ItemDictionary.java`:

| Exceptions                                     |
|------------------------------------------------|
| ![Patterns](https://i.vgy.me/gsSvtK.jpg)       |
| ![PlayerToPlayer](https://i.vgy.me/YKINvt.jpg) |
| ![Prices](https://i.vgy.me/U1r6TS.jpg)         |
