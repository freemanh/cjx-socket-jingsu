package com.cjx.monitor.jingsu;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.AttributeKey;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageDecoder extends ReplayingDecoder<Void> {
	private static final Logger logger = LoggerFactory
			.getLogger(MessageDecoder.class);

	private static AttributeKey<Deque<MonitorMessage>> failureDataKey = AttributeKey
			.newInstance("failureData");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		short header = in.readShort();
		logger.info("message header:{}", header);
		switch (header) {
		case 3: {
			processMonitorData(ctx, in, out);
			break;
		}
		case 6: {
			processSettingResponse(in, out);
			break;
		}
		default: {
			throw new IllegalArgumentException(String.format(
					"Unknow message header %#x", header));
		}
		}
	}

	private void processMonitorData(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) {
		// skip body length
		byte len = in.readByte();
		logger.debug("message length:{}", len);
		short type = in.readShort();
		logger.debug("message type:{}", type);

		switch (type) {
		case 35: {
			int id = in.readInt();
			double temp = in.readShort() * 0.1;
			double hum = in.readShort() * 0.1;
			// battery
			in.readShort();
			// signal
			in.readShort();
			// fail count
			int failCount = in.readShort();
			if (failCount == 0) {
				logger.warn("the fail count should not be 0 when in type 35!");
			}
			boolean poweroff = (in.readShort() == 1);
			// CRC
			in.readShort();

			String deviceCode = "Jingsu" + id;

			ctx.attr(failureDataKey).setIfAbsent(
					new ArrayDeque<MonitorMessage>());
			ctx.attr(failureDataKey)
					.get()
					.offerFirst(
							new MonitorMessage(deviceCode, temp, hum, poweroff,
									failCount, null));

			break;
		}
		case 127: {
			int id = in.readInt();
			String dateStr = String.format("%1$x-%2$x-%3$x", in.readByte(),
					in.readByte(), in.readByte());
			String timeStr = String.format("%1$x:%2$x:%3$x", in.readByte(),
					in.readByte(), in.readByte());
			double temp = in.readShort() * 0.1;
			double hum = in.readShort() * 0.1;
			short failCount = in.readShort();
			// skip CRC
			in.readShort();
			String deviceCode = "Jingsu" + id;

			Date date = DateTimeFormat.forPattern("yy-MM-dd HH:mm:ss")
					.withZone(DateTimeZone.forOffsetHours(8))
					.parseDateTime(dateStr + " " + timeStr).toDate();

			MonitorMessage oldest = new MonitorMessage(deviceCode, temp, hum,
					false, failCount, date);
			ctx.attr(failureDataKey).setIfAbsent(
					new ArrayDeque<MonitorMessage>());
			Deque<MonitorMessage> msgs = ctx.attr(failureDataKey).get();
			while (!msgs.isEmpty()) {
				MonitorMessage msg = msgs.pollFirst();

				date = DateUtils.addMinutes(date, 1);
				msg.setDate(date);

				out.add(msg);
			}
			out.add(oldest);
			break;
		}
		case 31: {
			// not support power off
			int id = in.readInt();
			double temp = in.readShort() * 0.1;
			double hum = in.readShort() * 0.1;
			// battery
			in.readShort();
			// signal
			in.readShort();
			// fail count
			int failCount = in.readShort();

			// CRC
			in.readShort();
			String deviceCode = "Jingsu" + id;
			out.add(new MonitorMessage(deviceCode, temp, hum, false, failCount,
					null));
			break;
		}
		default: {
			logger.warn(
					"Not very sure how to process this type of data:{}, using the same process as 0x23.",
					String.format("%1$#x", type));
			int id = in.readInt();
			double temp = in.readShort() * 0.1;
			double hum = in.readShort() * 0.1;
			// battery
			in.readShort();
			// signal
			in.readShort();
			// fail count
			int failCount = in.readShort();
			boolean poweroff = (in.readShort() == 1);
			// CRC
			in.readShort();

			String deviceCode = "Jingsu" + id;
			out.add(new MonitorMessage(deviceCode, temp, hum, poweroff,
					failCount, null));
		}
		}

	}

	private void processSettingResponse(ByteBuf in, List<Object> out) {
		logger.info("Received setting command response.");
		in.skipBytes(6);
		out.add("done");
	}

}
