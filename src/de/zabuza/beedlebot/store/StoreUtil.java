package de.zabuza.beedlebot.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import de.zabuza.beedlebot.exceptions.StoreUnsupportedWorldException;
import de.zabuza.beedlebot.exceptions.UnexpectedUnsupportedEncodingException;
import de.zabuza.sparkle.freewar.EWorld;

public final class StoreUtil {
	public static final String BEEDLE_BOT_SERVICE = "beedlebot/";
	public static final String PLAYER_PRICE_SERVICE = "mplogger/";
	public static final String QUERY_ALLOCATION = "=";
	public static final String QUERY_BEGIN = "?";
	public static final String QUERY_SEPARATOR = "&";
	public static final String SERVER_URL = "http://www.zabuza.square7.ch/freewar/";
	private static final String POST_PROTOCOL = "POST";
	private static final String POST_REQUEST_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
	private static final String POST_REQUEST_TYPE_PROPERTY = "Content-Type";

	private static final int SECOND_TO_MILLI_FACTOR = 1000;

	public static String encodeUtf8(final String text) throws UnexpectedUnsupportedEncodingException {
		try {
			return URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		} catch (final UnsupportedEncodingException e) {
			throw new UnexpectedUnsupportedEncodingException(e);
		}
	}

	public static long millisToSeconds(final long millis) {
		return millis / SECOND_TO_MILLI_FACTOR;
	}

	public static long secondsToMillis(final long seconds) {
		return seconds * SECOND_TO_MILLI_FACTOR;
	}

	public static void sendPostRequest(final URL url, final Map<String, String> arguments) throws IOException {
		final URLConnection con = url.openConnection();
		final HttpURLConnection http = (HttpURLConnection) con;
		http.setRequestMethod(POST_PROTOCOL);
		http.setDoOutput(true);

		final StringJoiner queryJoiner = new StringJoiner(QUERY_SEPARATOR);
		for (final Entry<String, String> entry : arguments.entrySet())
			queryJoiner.add(encodeUtf8(entry.getKey()) + QUERY_ALLOCATION + encodeUtf8(entry.getValue()));
		final byte[] out = queryJoiner.toString().getBytes(StandardCharsets.UTF_8);
		final int length = out.length;

		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty(POST_REQUEST_TYPE_PROPERTY, POST_REQUEST_TYPE);

		http.connect();
		try (final OutputStream os = http.getOutputStream()) {
			os.write(out);
		}
		http.disconnect();
	}

	public static int worldToNumber(final EWorld world) throws StoreUnsupportedWorldException {
		if (world == EWorld.ONE) {
			return 1;
		}
		if (world == EWorld.TWO) {
			return 2;
		}
		if (world == EWorld.THREE) {
			return 3;
		}
		if (world == EWorld.FOUR) {
			return 4;
		}
		if (world == EWorld.FIVE) {
			return 5;
		}
		if (world == EWorld.SIX) {
			return 6;
		}
		if (world == EWorld.SEVEN) {
			return 7;
		}
		if (world == EWorld.EIGHT) {
			return 8;
		}
		if (world == EWorld.NINE) {
			return 9;
		}
		if (world == EWorld.TEN) {
			return 10;
		}
		if (world == EWorld.ELEVEN) {
			return 11;
		}
		if (world == EWorld.TWELVE) {
			return 12;
		}
		if (world == EWorld.THIRTEEN) {
			return 13;
		}
		if (world == EWorld.FOURTEEN) {
			return 14;
		}

		throw new StoreUnsupportedWorldException(world);
	}

	/**
	 * Utility class. No implementation.
	 */
	private StoreUtil() {

	}
}
