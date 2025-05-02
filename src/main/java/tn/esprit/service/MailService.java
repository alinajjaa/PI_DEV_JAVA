package tn.esprit.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailService {

    public void sendConfirmationEmail() {
        final String username = "msekniaziz56@gmail.com";
        final String password = "xarr eqte jurk xxxi";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("msekni.aziz@esprit.tn"));
            message.setSubject("Confirmation de paiement - Agritrace Shop");

            String nomUtilisateur = "Aziz Msekni";
            message.setText("Bonjour " + nomUtilisateur + ",\n\nVotre paiement a été effectué avec succès.\nVotre produit vous sera livré prochainement.\n\nMerci !");

            Transport.send(message);

            System.out.println("Email envoyé !");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
