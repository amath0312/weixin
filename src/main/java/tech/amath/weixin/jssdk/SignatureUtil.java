package tech.amath.weixin.jssdk;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.amath.weixin.Signature;

public class SignatureUtil {
	private static final Logger logger = LoggerFactory.getLogger(SignatureUtil.class);

	public static Signature sign(String jsapiTicket, String url) {
		logger.debug("jsapi-ticket={}, url={}", jsapiTicket, url);
		Signature ret = new Signature();
		String nonce = createNonce();
		long timestamp = createTimestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonce + "&timestamp=" + timestamp + "&url=" + url;

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.setUrl(url);
		ret.setTicket(jsapiTicket);
		ret.setNonce(nonce);
		ret.setTimestamp(timestamp);
		ret.setSignature(signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private static String createNonce() {
		return UUID.randomUUID().toString();
	}

	private static long createTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
}
