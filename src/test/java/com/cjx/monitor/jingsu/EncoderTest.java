package com.cjx.monitor.jingsu;

import io.netty.channel.embedded.EmbeddedChannel;

import org.junit.Test;

import com.cjx.monitor.jingsu.codec.MessageDecoder;

public class EncoderTest {
	@Test
	public void test() {
		EmbeddedChannel chn = new EmbeddedChannel(new MessageDecoder());
	}
}
