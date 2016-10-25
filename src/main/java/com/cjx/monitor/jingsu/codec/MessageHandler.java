package com.cjx.monitor.jingsu.codec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(MessageHandler.class);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ctx.writeAndFlush(Unpooled.copiedBuffer("ALLSU",
				Charset.forName("ascii")));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		logger.info("Received data:{}", msg);
		
		if (msg instanceof MonitorMessage) {
			MonitorMessage data = (MonitorMessage) msg;
			if (data.getFailCount() > 0) {
				logger.info("ask for more failure message...");
				ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(
						"CSV" + (data.getFailCount() - 1),
						Charset.forName("ascii")));
				if (future.isSuccess()) {
					logger.info("Succeed to send CSV");
				}
			} else {
				logger.info("Nothing need to be synced.");
				ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(
						"RECEIVE", Charset.forName("ascii")));
				if (future.isSuccess()) {
					logger.info("Proactively close connection");
					ctx.close();
				}
			}
			
			

		} else if (msg instanceof String) {
			ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(
					"ALLSU", Charset.forName("ascii")));
			if (future.isSuccess()) {
				logger.info("Proactively close connection");
				ctx.close();
			}
		} else {
			logger.error("Failed to process message type:{}", msg.getClass());
		}
	}
}
