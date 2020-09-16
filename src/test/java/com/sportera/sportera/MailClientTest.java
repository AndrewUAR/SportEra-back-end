package com.sportera.sportera;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.sportera.sportera.services.EmailSenderService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import java.io.IOException;

import static com.sportera.sportera.TestUtil.createMailMessage;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class MailClientTest {

    private GreenMail smtpServer;

    @Autowired
    EmailSenderService emailSenderService;

    @BeforeEach
    public void setUp() throws Exception {
        smtpServer = new GreenMail(new ServerSetup(587, null, "smtp"));
        smtpServer.start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        smtpServer.stop();
    }

    private void assertReceivedMessageContains(String expected) throws IOException, MessagingException {
        System.out.println(expected);
//        MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
//        System.out.println(receivedMessages);
//        assertThat(receivedMessages.length).isEqualTo(1);
//        String content = (String) receivedMessages[0].getContent();
//        assertTrue(content.contains(expected));
    }

    @Test
    public void shouldSendMail() throws Exception {
        String message = "Test message content";
        emailSenderService.sendEmail(createMailMessage(message));
        assertReceivedMessageContains(message);
    }

}
