package de.zabuza.beedlebot.store;

public final class ItemPrice {
	private final String mName;
	private final PlayerPrice mPlayerPrice;
	private final int mShopPrice;

	public ItemPrice(final String name, final int shopPrice, final PlayerPrice playerPrice) {
		mName = name;
		mShopPrice = shopPrice;
		mPlayerPrice = playerPrice;
	}

	public String getName() {
		return mName;
	}

	public PlayerPrice getPlayerPrice() {
		return mPlayerPrice;
	}

	public int getShopPrice() {
		return mShopPrice;
	}
}
