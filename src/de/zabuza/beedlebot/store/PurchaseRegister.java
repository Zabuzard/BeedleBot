package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import de.zabuza.sparkle.freewar.EWorld;

public final class PurchaseRegister {
	private static final String ARG_COST = "cost";
	private static final String ARG_ID = "id";
	private static final String ARG_ITEM = "item";
	private static final String ARG_PLAYER_PRICE = "playerPrice";
	private static final String ARG_PROFIT = "profit";
	private static final String ARG_SHOP_PRICE = "shopPrice";
	private static final String ARG_TS_CACHE = "ts_cache";
	private static final String ARG_TS_PLAYER_PRICE = "ts_playerPrice";
	private static final String ARG_USER = "user";
	private static final String ARG_WAS_CACHED = "wasCached";
	private static final String ARG_WORLD = "world";
	private static final String SERVER_FILE = "registerPurchase.php";

	private final String mUser;
	private final EWorld mWorld;

	public PurchaseRegister(final String user, final EWorld world) {
		mUser = user;
		mWorld = world;
	}

	public void registerPurchase(final Item item) {
		try {
			final URL url = new URL(StoreUtil.SERVER_URL + StoreUtil.BEEDLE_BOT_SERVICE + SERVER_FILE);
			final HashMap<String, String> arguments = new HashMap<>();
			final ItemPrice itemPrice = item.getStorePriceData();

			final String emptyText = "";

			// Put data
			arguments.put(ARG_ID, item.getId() + emptyText);
			arguments.put(ARG_USER, mUser);
			arguments.put(ARG_WORLD, StoreUtil.worldToNumber(mWorld) + emptyText);
			arguments.put(ARG_ITEM, item.getName());
			arguments.put(ARG_COST, item.getCost() + emptyText);
			arguments.put(ARG_PROFIT, item.getProfit() + emptyText);
			arguments.put(ARG_SHOP_PRICE, itemPrice.getShopPrice() + emptyText);
			if (itemPrice.hasPlayerPrice()) {
				final PlayerPrice playerPrice = itemPrice.getPlayerPrice().get();
				arguments.put(ARG_PLAYER_PRICE, playerPrice.getPrice() + emptyText);
				arguments.put(ARG_TS_PLAYER_PRICE, StoreUtil.millisToSeconds(playerPrice.getTimestamp()) + emptyText);
			}
			arguments.put(ARG_WAS_CACHED, itemPrice.isCached() + emptyText);
			arguments.put(ARG_TS_CACHE, StoreUtil.millisToSeconds(itemPrice.getLookupTimestamp()) + emptyText);

			arguments.put(ARG_ID, item.getId() + emptyText);

			try {
				StoreUtil.sendPostRequest(url, arguments);
			} catch (final IOException e) {
				// TODO Correct error handling and logging
				e.printStackTrace();
			}
		} catch (final MalformedURLException e) {
			// TODO Correct error handling and logging
			e.printStackTrace();
		}
	}
}
