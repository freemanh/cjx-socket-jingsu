package com.cjx.monitor.jingsu;

import io.netty.channel.ChannelFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
	public static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		final ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Safely to close Netty server...");
				ChannelFuture c = ctx.getBean(ChannelFuture.class);
				c.channel().closeFuture();
			}

		});
	}
}
