package cnrst.ma.services;

import java.time.LocalDate;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cnrst.ma.entity.Boursier;
import cnrst.ma.entity.Rapport;
import cnrst.ma.entity.RapportTypeEnum;

@Service
public class EmailServiceImp {

	@Autowired
	public JavaMailSender emailSender;
	
	private void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		Address fromAddress = new InternetAddress("cnrst@cnrst.com");// TODO Add current email of cnrst
		Address toAddress = new InternetAddress(to);

		message.setRecipient(RecipientType.TO, toAddress);
		message.setFrom(fromAddress);
		message.setSubject(subject);
		message.setContent(text, "text/html; charset=utf-8");
		emailSender.send(message);

	}
	@Async
	public void sendDownLoadRapportMail(Boursier boursierAuthentifie, String nomRapport, RapportTypeEnum type) {
		String Subject = "Bourse CNRST : Rapport ";
		String rapportURL = "http://localhost:8989/rapports/download/" + nomRapport;
		String text = "Bonjour<br>Vous avez modifié ou généré votre Rapport ";
		if (type == RapportTypeEnum.Semestriel) {
			String append = "Semestriel " + boursierAuthentifie.getSemestreActuel();
			Subject += append;
			text += append;
		} else {
			String append = "Annuel " + LocalDate.now().getYear();
			Subject += append;
			text += append;
		}
		text += ", merci de le telecharger en cliquant sur ce <a href='" + rapportURL + "'>lien</a>";
		// TODO add URL of the rapport
		try {
			sendSimpleMessage(boursierAuthentifie.getEmail(), Subject, text);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Async
	public void sendRapportValide(Boursier boursier, Rapport rapport) {
		String Subject = "Bourse CNRST : Rapport Validé";
		String text = "Bonjour<br>Votre Rapport "+rapport.getType()+" ["+rapport.getURIDocument()+"] a  été validé";
		sendEtatRapportChanged(boursier.getEmail(),Subject,text);
	}
	@Async
	public void sendRapportRejete(Boursier boursier, Rapport rapport) {
		String Subject = "Bourse CNRST : Rapport Rejeté";
		String text = "Bonjour<br>Votre Rapport "+rapport.getType()+" ["+rapport.getURIDocument()+"] a été rejeté. Merci de contacter le responsable pour plus d'information.";
		sendEtatRapportChanged(boursier.getEmail(),Subject,text);		
	}

	private void sendEtatRapportChanged(String Email, String Subject,String text) {
		try {
			sendSimpleMessage(Email, Subject, text);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	@Async
	public void sendAdmisMail(Boursier boursier) {
		String contratURL ="http://localhost:8989/...";//TODO 
		String text="Bonjour Ms."+boursier.getNomComplet()+"<br>"+
					"Vous étes admis pour la bourse d'excelence CNRST. "+
					"Merci d'accéder à la plateform pour remplir votre contrat via le lien:"+contratURL+" .<br>"+
					"Votre code confidentialité est:"+boursier.getCodeConfidentielle();
					
		try {
			sendSimpleMessage(boursier.getEmail(),"CNRST :Bourse d'excélence", text);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}