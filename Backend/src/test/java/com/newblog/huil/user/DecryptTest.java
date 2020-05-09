package com.newblog.huil.user;

import com.newblog.huil.HuilApplication;
import com.newblog.huil.utils.encode.AESEncode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = HuilApplication.class)
public class DecryptTest {
    Object target;
    private static final Logger logger = LoggerFactory.getLogger(DecryptTest.class);
    @Test
    public void getPassword() throws Exception {
        String pwd = AESEncode.Decrypt("ewQUJED7pNNIj34/je639A==", "44d4C62864y4D0Mk");
        logger.info("密码:"+pwd);
    }
}
