package com.cjx.monitor.jingsu.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cjx.monitor.jingsu.codec.MessageDecoder;
import com.cjx.monitor.jingsu.codec.MessageHandler;

@Configuration
public class ServerConfig {
	@Autowired
	ApplicationContext ctx;

	@Bean(initMethod = "sync")
	public ChannelFuture setup(@Value("${netty.port}") int port) throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap(); // (2)
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
				.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
							@Override
							public void initChannel(SocketChannel ch) throws Exception {
								ch.pipeline().addFirst(new LoggingHandler(LogLevel.INFO)).addLast(ctx.getBean(MessageDecoder.class)).addLast(ctx.getBean(MessageHandler.class));
							}
						}).option(ChannelOption.SO_BACKLOG, 128) // (5)
				.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

		// Bind and start to accept incoming connections.
		return b.bind(port); // (7)

	}
}
