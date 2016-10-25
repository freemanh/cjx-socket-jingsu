package com.cjx.monitor.jingsu.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cjx.monitor.jingsu.repo.UserRepo;

@Service
public class SmsSender {
	private final static Logger LOG = LoggerFactory.getLogger(SmsSender.class);

	private String cpid;
	private String url;
	private String channelId;
	private UserRepo userRepo;

	@Async
	public void send(String content) {
		StringBuffer queryBuf = new StringBuffer("cpid=" + cpid + "&password=");
		Md5PasswordEncoder encoder = new Md5PasswordEncoder();
		String currentTimestamp = String.valueOf(System.currentTimeMillis())
				.substring(0, 10);
		queryBuf.append(encoder.encodePassword("520530_" + currentTimestamp
				+ "_topsky", null));
		queryBuf.append("&timestamp=");
		queryBuf.append(currentTimestamp);
		queryBuf.append("&channelid=");
		queryBuf.append(channelId);
		queryBuf.append("&msg=");

		content += String.format("%tT", new Date());
		try {
			queryBuf.append(URLEncoder.encode(content, "gbk"));
		} catch (UnsupportedEncodingException e1) {
			queryBuf.append(content);
		}

		queryBuf.append("&tele=");
		queryBuf.append(userRepo.findMobiles().get(0));

		String urlString = url + "?" + queryBuf.toString();
		LOG.debug("Sms sending url is:{}", urlString);

		BufferedReader in = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			int responseCode = con.getResponseCode();
			LOG.debug("Response code is:{}", responseCode);

			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			LOG.debug("SMS platform response detail: {}", response);
			boolean success = response.toString().startsWith("success") ? true
					: false;
			LOG.info("SMS content:{},send result:{}", content, success);
		} catch (Exception e) {
			LOG.error("Fail to send SMS!", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("Fail to close inputstream", e);
				}
			}
		}
	}

	@Value("${sms.cpid}")
	public void setCpid(String cpid) {
		this.cpid = cpid;
	}

	@Value("${sms.url}")
	public void setUrl(String url) {
		this.url = url;
	}

	@Value("${sms.channelId}")
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	@Autowired
	public void setUserRepo(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
}
