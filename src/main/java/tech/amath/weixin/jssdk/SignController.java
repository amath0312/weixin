package tech.amath.weixin.jssdk;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import tech.amath.common.util.HttpUtil;
import tech.amath.weixin.Signature;
import tech.amath.weixin.WeixinConfig;

@RestController
@RequestMapping("/api/weixin/jssdk/signature")
public class SignController {
	private static final Logger logger = LoggerFactory.getLogger(SignController.class);
	@Autowired
	WeixinConfig wxConf;

	TicketCache ticket;

	@GetMapping(value = "")
	public Signature getSignature(@RequestParam(required = true) String url) {
		return SignatureUtil.sign(getTicket(), url);
	}

	String getTicket() {
		if (ticket == null || ticket.isExpired()) {
			logger.debug("ticket is expired");
			String accessToken = getAccessToken();
			logger.debug("access token={}",accessToken);
			ticket = getJsapiTicket(accessToken);
		}
		return ticket.getTicket();
	}

	String getAccessToken() {
		Map<String, String> params = new HashMap<>();
		params.put("grant_type", "client_credential");
		params.put("appid", wxConf.getAppid());
		params.put("secret", wxConf.getSecret());
		String ret = HttpUtil.get(wxConf.getAccessTokenUrl(), params, null);
		logger.debug("get access token: {}", ret);
		return JSON.parseObject(ret).getString("access_token");
	}

	TicketCache getJsapiTicket(String accessToken) {
		Map<String, String> params = new HashMap<>();
		params.put("access_token", accessToken);
		params.put("type", "jsapi");
		String ret = HttpUtil.get(wxConf.getGetTicketUrl(), params, null);
		logger.debug("get jsapi ticket: {}", ret);
		JSONObject retJson = JSON.parseObject(ret);
		TicketCache tk = new TicketCache();
		tk.setCacheTime(System.currentTimeMillis());
		tk.setExpiresTime(retJson.getLongValue("expires_in")*1000L);
		tk.setTicket(retJson.getString("ticket"));
		return tk;
	}

}
