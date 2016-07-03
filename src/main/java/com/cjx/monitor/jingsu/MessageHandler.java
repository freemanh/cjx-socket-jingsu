package com.cjx.monitor.jingsu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.cjx.monitor.jingsu.util.CRC16Modbus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClientImpl.Serializer;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(MessageHandler.class);

	@Autowired
	private JdbcTemplate jdbc;
	@Autowired
	private Client client;

	private static AttributeKey<Queue<ByteBuf>> key = AttributeKey
			.newInstance("settings");
	private static AttributeKey<Integer> sensorIdKey = AttributeKey
			.valueOf("sensorId");

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		logger.debug("New connection arrived!");
		ctx.writeAndFlush(Unpooled.copiedBuffer("ALLSU",
				Charset.forName("ascii")));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof MonitorMessage) {
			MonitorMessage data = (MonitorMessage) msg;
			logger.debug("Received monitor data:{}", data);
			if (null == data.getDate()) {
				data.setDate(new Date());
			}

			ObjectMapper om = new ObjectMapper();
			om.setDateFormat(new ISO8601DateFormat());

			client.useTube("data.reading");
			client.put(1000, 0, 120, Serializer.serializableToByteArray(om
					.writeValueAsString(data)));

			if (data.getFailCount() > 0) {
				ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(
						"CSV" + (data.getFailCount() - 1),
						Charset.forName("ascii")));
				if (future.isSuccess()) {
					logger.debug("Succeed to send CSV");
				}
			} else {
				Map<String, Object> result = jdbc
						.queryForMap(
								"select xsensor.id, is_synced synced, temp_revision tempRev, hum_revision as humRev,upload_frequency frequency from xsensor join xdevice on xsensor.device_id=xdevice.id where xdevice.code=?",
								data.getDeviceId());
				boolean synced = result.get("synced").toString().equals("1") ? true
						: false;
				double tempRev = Double.valueOf(result.get("tempRev")
						.toString());
				double humRev = Double.valueOf(result.get("humRev").toString());
				int frequency = Integer.valueOf(result.get("frequency")
						.toString());
				int id = Integer.valueOf(result.get("id").toString());

				Queue<ByteBuf> settings = new LinkedList<ByteBuf>();
				if (!synced) {
					ctx.attr(sensorIdKey).set(id);

					settings.add(genTempRevSetting(tempRev));
					settings.add(genHumRevSetting(humRev));
					settings.add(genFrequencySetting(frequency));

					ctx.attr(key).set(settings);

					ByteBuf setting = settings.poll();
					ctx.writeAndFlush(setting);
				} else {
					logger.info("Nothing need to be synced.");
					ChannelFuture future = ctx.writeAndFlush(Unpooled
							.copiedBuffer("RECEIVE", Charset.forName("ascii")));
					if (future.isSuccess()) {
						logger.info("Proactively close connection");
						//ctx.close();
					}
				}

			}

		} else if (msg instanceof String) {
			Queue<ByteBuf> settings = ctx.attr(key).get();
			ByteBuf setting = settings.poll();
			if (null != setting) {
				ctx.writeAndFlush(setting);
			} else {
				ChannelFuture future = ctx.writeAndFlush(Unpooled.copiedBuffer(
						"ALLSU", Charset.forName("ascii")));
				if (future.isSuccess()) {
					logger.info("Proactively close connection");
					ctx.close();
				}
				jdbc.update("update xsensor set is_synced=true where id=?", ctx
						.attr(sensorIdKey).get());
			}
		} else {
			logger.error("Failed to process message type:{}", msg.getClass());
		}
	}

	private ByteBuf genFrequencySetting(int frequency) {
		ByteBuf frequencySetting = Unpooled.buffer();
		frequencySetting.writeByte(0x00);
		frequencySetting.writeByte(0x06);
		frequencySetting.writeShort(45);
		frequencySetting.writeShort(frequency);

		CRC16Modbus crc = new CRC16Modbus();
		crc.update(frequencySetting.array(), 0,
				frequencySetting.readableBytes());
		frequencySetting.writeBytes(crc.getCrcBytes());
		return frequencySetting;
	}

	private ByteBuf genHumRevSetting(double humRev) {
		ByteBuf humRevSetting = Unpooled.buffer();
		humRevSetting.writeByte(0x00);
		humRevSetting.writeByte(0x06);
		humRevSetting.writeShort(152);
		humRevSetting.writeShort((short) Math.round(humRev * 10));

		CRC16Modbus crc = new CRC16Modbus();
		crc.update(humRevSetting.array(), 0, humRevSetting.readableBytes());
		humRevSetting.writeBytes(crc.getCrcBytes());
		return humRevSetting;
	}

	private ByteBuf genTempRevSetting(double tempRev) {
		ByteBuf tempRevSetting = Unpooled.buffer();
		tempRevSetting.writeByte(0x00);
		tempRevSetting.writeByte(0x06);
		tempRevSetting.writeShort(151);
		tempRevSetting.writeShort((short) Math.round(tempRev * 10));

		CRC16Modbus crc = new CRC16Modbus();
		crc.update(tempRevSetting.array(), 0, tempRevSetting.readableBytes());
		tempRevSetting.writeBytes(crc.getCrcBytes());
		return tempRevSetting;
	}

}
