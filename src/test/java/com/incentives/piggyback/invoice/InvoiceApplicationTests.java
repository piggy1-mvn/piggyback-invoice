package com.incentives.piggyback.invoice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@SpringBootTest
public class InvoiceApplicationTests {

	@Test
	@PrepareForTest(SpringApplication.class)
	public void contextLoads() {
			mockStatic(SpringApplication.class);
			InvoiceApplication.main(new String[]{"Hello", "World"});
			verifyStatic(SpringApplication.class);
			SpringApplication.run(InvoiceApplication.class, "Hello", "World");
		}
	}
