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

/**
 * Class that provides utility methods for {@link Store}s.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class StoreUtil {
	/**
	 * The path to the tools service on the server.
	 */
	public static final String BEEDLE_BOT_SERVICE = "beedlebot/";
	/**
	 * The path to the player price service on the server.
	 */
	public static final String PLAYER_PRICE_SERVICE = "mplogger/";
	/**
	 * Symbol used to allocate parameters in queries.
	 */
	public static final String QUERY_ALLOCATION = "=";
	/**
	 * Symbol used to begin a query.
	 */
	public static final String QUERY_BEGIN = "?";
	/**
	 * Symbol used to separate parameters in a query.
	 */
	public static final String QUERY_SEPARATOR = "&";
	/**
	 * URL to the server that provides all services.
	 */
	public static final String SERVER_URL = "http://www.zabuza.square7.ch/freewar/";
	/**
	 * Name of the post protocol.
	 */
	private static final String POST_PROTOCOL = "POST";
	/**
	 * Request type for post requests.
	 */
	private static final String POST_REQUEST_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
	/**
	 * Type property for post requests.
	 */
	private static final String POST_REQUEST_TYPE_PROPERTY = "Content-Type";
	/**
	 * Factor that converts seconds to milliseconds if multiplied with.
	 */
	private static final int SECOND_TO_MILLIS_FACTOR = 1_000;

	/**
	 * Encodes the given text in UTF-8.
	 * 
	 * @param text
	 *            The text to encode
	 * @return The given text encoded in UTF-8
	 * @throws UnexpectedUnsupportedEncodingException
	 *             If the UTF-8 char-set is unexpectedly not supported
	 */
	public static String encodeUtf8(final String text) throws UnexpectedUnsupportedEncodingException {
		try {
			return URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		} catch (final UnsupportedEncodingException e) {
			throw new UnexpectedUnsupportedEncodingException(e);
		}
	}

	/**
	 * Converts the time given in milliseconds to seconds.
	 * 
	 * @param millis
	 *            The time to convert in milliseconds
	 * @return The given time converted to seconds
	 */
	public static long millisToSeconds(final long millis) {
		return millis / SECOND_TO_MILLIS_FACTOR;
	}

	/**
	 * Converts the time given in seconds to milliseconds.
	 * 
	 * @param seconds
	 *            The time to convert in seconds
	 * @return The given time converted to milliseconds
	 */
	public static long secondsToMillis(final long seconds) {
		return seconds * SECOND_TO_MILLIS_FACTOR;
	}

	/**
	 * Sends a post request with the given arguments to the given URL.
	 * 
	 * @param url
	 *            The URL to send the request to
	 * @param arguments
	 *            The arguments to send as key value pairs
	 * @throws IOException
	 *             If an I/O-Exception occurred
	 */
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

	/**
	 * Gets the representative number to the given world.
	 * 
	 * @param world
	 *            The world to get its representative number for
	 * @return The number that represents the given world
	 * @throws StoreUnsupportedWorldException
	 *             If the given world is not supported by this method as it has
	 *             no representative number, like for example
	 *             {@link EWorld#ACTION}.
	 */
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
