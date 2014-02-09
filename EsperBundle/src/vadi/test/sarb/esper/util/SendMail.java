package vadi.test.sarb.esper.util;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	final java.util.logging.Logger log = java.util.logging.Logger.getLogger("global");
	public String subject = "Today's data";

	public void send(String msg)
	{
		final String user = vadi.test.sarb.esper.Messages.getString("email.from");
		
		
		 final String password = vadi.test.sarb.esper.Messages.getString("email.password");
		 if ( vadi.test.sarb.esper.Messages.getString("do.print").equals("true"))
		 	log.info("user "+user+" "+password+" "+password);
		String emailTo = vadi.test.sarb.esper.Messages.getString("email.to");
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(emailTo));
			message.setSubject(subject);

			message.setText(msg);
			Transport.send(message);
			
			log.info("Sent mail");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public  static void main(String []args){
		
		SendMail sm = new SendMail();
		sm.send("Test MEssage");
		
	}
}
