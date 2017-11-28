package tech.amath.weixin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeixinConfig {
	@Value("${weixin.access_token_url:}")
	private String accessTokenUrl;

	@Value("${weixin.get_ticket_url:}")
	private String getTicketUrl;

	@Value("${weixin.appid:}")
	private String appid;

	@Value("${weixin.secret:}")
	private String secret;

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public String getGetTicketUrl() {
		return getTicketUrl;
	}

	public void setGetTicketUrl(String getTicketUrl) {
		this.getTicketUrl = getTicketUrl;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSecret() {
		if (secret.startsWith("/")) {
			File file = new File(secret);
			if (file.exists()) {
				try {
					FileReader freader = new FileReader(file);
					BufferedReader buf = new BufferedReader(freader);
					String ret = buf.readLine();
					buf.close();
					System.out.println(ret);
					return ret;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
