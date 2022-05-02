package com.example.financialassessment.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.auth.OAuth2SaslClientFactory;
import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.security.sasl.SaslClientFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//1. GET SESSION
//2. CREATE MESSAGE
//3. SEND MESSAGE

public class EmailSendingUtil {

//    @Value("${recipient.email.id}")
//    static String recipientEmailId = "info@koshantrafinancials.com";
    static String recipientEmailId = "ishweenk999@gmail.com";

    // For using Oauth2

    private static String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    private JavaMailSender sender;

    public static final String OAUTH_TOKEN_PROP =
            "mail.imaps.sasl.mechanisms.oauth2.oauthToken";

    // Not a best practice to store client id, secret and token in source
    // must be stored in a file.
    private static String oauthClientId = "699736610675-gjk00ngno3eat1i2mb1qc31ho88gglf1.apps.googleusercontent.com";
    private static String oauthSecret = "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";
    private static String refreshToken = "1//04sKhRJw-kdjsCgYIARAAGAQSNwF-L9Irr6y7verEeOojmBIpRfyg-fCy92PCGm8AF8yrFLdX9T-MlE5ACYnQBZ_HHF7J_m9IADk";
    private static String accessToken = "ya29.A0ARrdaM9V3bK_Pkp8WtZFEZuUbmQjrSfaaimmSqbsk_XHk9623bz-TyhJc_oIbjbGvXb7XLI34MvwQ6Ow2m5JhuA_JdpWQ2NM9XrnQlIm_1-RNv64ECWqNv0yo76pvmymfnmGZxYJkRanqBS1IDfJCsz8cRhG";
    private static long tokenExpires = 1458168133864L;
    private MailSender mailSender;

    /*
    Renew access token if expired
     */
    public String renewToken() {


        if (System.currentTimeMillis() > tokenExpires) {

            try {
                String request = "client_id=" + URLEncoder.encode(oauthClientId, "UTF-8")
                        + "&client_secret=" + URLEncoder.encode(oauthSecret, "UTF-8")
                        + "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8")
                        + "&grant_type=refresh_token";
                HttpURLConnection conn = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(request);
                out.flush();
                out.close();
                conn.connect();

                try {

                    HashMap<String, Object> result;
                    result = new ObjectMapper().readValue(conn.getInputStream(), new TypeReference<HashMap<String, Object>>() {
                    });
                    accessToken = (String) result.get("access_token");
                    System.out.println("inside renew: " + accessToken);
                    tokenExpires = System.currentTimeMillis() + (((Number) result.get("expires_in")).intValue() * 1000);
                    return accessToken;
                } catch (IOException e) {
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }

    public void sendEmail(File file, File fileWriter, String... files) throws MessagingException {

        final String from = "ishweenk999@gmail.com",
                host = "smtp.gmail.com", port = "465",
                sslEnable = "true", auth = "true";

        // Get system properties
//        Properties properties = System.getProperties();

        // Setup mail server
//        properties.put("mail.smtp.host", host);
//        properties.put("mail.smtp.port", port);
//        properties.put("mail.smtp.ssl.enable", sslEnable);
//        properties.put("mail.smtp.auth", auth);

        String accessTokenLocal = renewToken();

        System.out.println(accessTokenLocal);

//        ((JavaMailSenderImpl)this.mailSender).setPassword(accessTokenLocal);
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(from);
//        message.setTo(from);
//        message.setSubject("Financial Assessment of: " + file);
//        message.setText("let's test");
//
////         sending email
//
//        mailSender.send(message);

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.imaps.auth.login.disable", "true");
        props.put("mail.imaps.auth.plain.disable", "true");
        props.put(OAUTH_TOKEN_PROP, accessToken);
        Session session = Session.getInstance(props);
//        session.setDebug(true);
//        MimeMessage mimeMessage = new MimeMessage(session);
//        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(from));
//        mimeMessage.setSubject("test");
//        mimeMessage.setContent("xxx", "text/html");
//
//
//        SMTPTransport transport = (SMTPTransport)session.getTransport("smtp");
//        transport.connect("smtp.gmail.com", from, accessTokenLocal);
//        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
//        transport.close();




//        Session session = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                System.out.println("password authentication.............");
//                return new PasswordAuthentication("ishweenk999@gmail.com", accessTokenLocal);
////                return new PasswordAuthentication("ishweenk999@gmail.com", "isha@1999");
//
//            }
//        });

        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(recipientEmailId));

            // Set Subject: header field
            message.setSubject("Financial Assessment for client: " + file.toString());

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Create the message part for text
            BodyPart testMime = new MimeBodyPart();
            testMime.setText("");

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
//            Transport.send(message);
            SMTPTransport transport = (SMTPTransport)session.getTransport("smtp");
            transport.connect("smtp.gmail.com", from, "");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("Sent message successfully....");
            file.delete();
            fileWriter.delete();
//            Arrays.stream(files).map(s->new File(s).delete());
            for(String s: files){
                System.out.println("deleting...");
                File f = new File(s);
                f.delete();
            }
        } catch (MessagingException | IOException mex) {
            mex.printStackTrace();
        }
    }
}
