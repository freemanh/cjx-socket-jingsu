package com.cjx.monitor.jingsu;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import com.cjx.monitor.jingsu.repo.UserRepo;
import com.cjx.monitor.jingsu.service.SmsSender;

@RunWith(value = MockitoJUnitRunner.class)
public class SmsSenderTest {
	@Mock
	UserRepo userRepo;

	@Test
	@Ignore
	public void test() {
		when(userRepo.findMobiles()).thenReturn(Arrays.asList("15308039727"));

		SmsSender sms = new SmsSender();
		sms.setChannelId("6214");
		sms.setCpid("8604");
		sms.setUrl("http://admin.sms9.net/houtai/sms.php");
		sms.setUserRepo(userRepo);

		sms.send("测试中文");
	}
}
