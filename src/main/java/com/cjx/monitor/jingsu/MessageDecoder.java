package com.cjx.monitor.jingsu;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageDecoder extends ReplayingDecoder<Void> {
	private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
			throw new IllegalArgumentException(String.format("Unknow message header %#x", header));
		}
		}
	}

	private void processMonitorData(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		// skip body length
		byte len = in.readByte();
		logger.info("message length:{}", len);
		short type = in.readShort();
		logger.info("message type:{}", type);

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
			boolean poweroff = (in.readShort() == 1);
			// CRC
			in.readShort();

			String deviceCode = "Jingsu" + id;
			out.add(new MonitorMessage(deviceCode, temp, hum, poweroff, failCount, null));

			break;
		}
		case 127: {
			int id = in.readInt();
			String dateStr = String.format("%1$x-%2$x-%3$x", in.readByte(), in.readByte(), in.readByte());
			String timeStr = String.format("%1$x:%2$x:%3$x", in.readByte(), in.readByte(), in.readByte());
			double temp = in.readShort() * 0.1;
			double hum = in.readShort() * 0.1;
			short failCount = in.readShort();
			// skip CRC
			in.readShort();
			String deviceCode = "Jingsu" + id;

			try {
				Date date = new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(dateStr + " " + timeStr);
				out.add(new MonitorMessage(deviceCode, temp, hum, false, failCount, date));
			} catch (ParseException e) {
				throw new RuntimeException("Failed to parse date string!", e);
			}

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
			out.add(new MonitorMessage(deviceCode, temp, hum, false, failCount, null));
			break;
		}
		default: {
			logger.warn("Not very sure how to process this type of data:{}, using the same process as 0x23.", String.format("%1$#x", type));
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
			out.add(new MonitorMessage(deviceCode, temp, hum, poweroff, failCount, null));
		}
		}

	}

	private void processSettingResponse(ByteBuf in, List<Object> out) {
		logger.info("Received setting command response.");
		in.skipBytes(6);
		out.add("done");
	}

}
