package com.jiezipoi.mall.component;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class EmailComponent {
    private final AmazonSimpleEmailService emailService;
    private final TemplateEngine templateEngine;

    public EmailComponent(AmazonSimpleEmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    private void processSendRequest(SendEmailRequest emailRequest) {
        emailService.sendEmail(emailRequest);
    }

    private SendEmailRequest generateRequest(Destination destination, Message message) {
        return new SendEmailRequest()
                .withSource("noreply@jiezicloud.com")
                .withDestination(destination)
                .withMessage(message);
    }

    public void sendEmail(String email, String titleStr, String htmlBody) {
        Destination destination = new Destination().withToAddresses(email);

        Content subject = new Content("[JieziCloud] " + titleStr);
        Content bodyContent = new Content(htmlBody);
        Body body = new Body().withHtml(bodyContent);
        Message message = new Message()
                .withSubject(subject)
                .withBody(body);
        SendEmailRequest sendEmailRequest = generateRequest(destination, message);
        processSendRequest(sendEmailRequest);
    }

    public String processEmailTemplate(String templateLocation, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(templateLocation, context);
    }
}
