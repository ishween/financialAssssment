package com.example.financialassessment.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

//1. GET SESSION
//2. CREATE MESSAGE
//3. SEND MESSAGE

public class EmailSendingUtil {
    public static void sendEmail(File file, File fileWriter, String recipientEmailId){
        final String from = "ishweenk999@gmail.com",
                host = "smtp.gmail.com", port = "465",
                sslEnable = "true", auth = "true";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", sslEnable);
        properties.put("mail.smtp.auth", auth);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("password authentication.............");
                return new PasswordAuthentication("ishweenk999@gmail.com", "isha@1999");
            }
        });

        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(recipientEmailId));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Create the message part for text
            BodyPart testMime = new MimeBodyPart();
            testMime.setText("This is message body");

            // Part two is attachment
            MimeBodyPart fileMime = new MimeBodyPart();
            fileMime.attachFile(file);

            // Set text message part
            multipart.addBodyPart(testMime);
            // Set file message part
            multipart.addBodyPart(fileMime);

            // Send the complete message parts
//            message.setContent(multipart );

            MimeBodyPart fileMime2 = new MimeBodyPart();
            fileMime2.attachFile(fileWriter);

            // Set text message part
            // Set file message part
            multipart.addBodyPart(fileMime2);

            // Send the complete message parts
            message.setContent(multipart );

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException | IOException mex) {
            mex.printStackTrace();
        }
    }
}
