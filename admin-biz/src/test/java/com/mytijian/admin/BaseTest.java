package com.mytijian.admin;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Administrator on 2017/7/12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppTestApplication.class)
public abstract class BaseTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
}

@SpringBootApplication
class AppTestApplication {
}
