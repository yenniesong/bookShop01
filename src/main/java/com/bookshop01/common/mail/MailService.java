package com.bookshop01.common.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.vo.OrderVO;

@Service("mailService")
public class MailService {
	@Autowired
	private JavaMailSender mailSender;
//	@Autowired
//	private SimpleMailMessage preConfiguredMessage;

	@Async
	//public void sendMail(String to, String subject, String body) {
	public void sendMail(Map receiverMap, List<OrderVO> myOrderList, MemberVO orderer) {
	//public  void sendMail() {
		String to,  subject, body = "";
		String mail_template;
		OrderVO orderVO  = (OrderVO) myOrderList.get(0)
				;
//		to = "x9103293@naver.com";
//		subject = "테스트메일";
//		body = "테스트 메일입니다.";
		
		to = orderer.getEmail1() + "@" + orderer.getEmail2();
//		to = "06yeni@naver.com";
		subject =  orderer.getMember_name() + "님 주문 내역을 확인해 주세요.";
			
		MimeMessage message = mailSender.createMimeMessage();

		try {
			
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			mail_template = readHtmlFile();  //주문 결과 템플릿 파일을 읽어옵니다.
			
			body = setOrderInfo(mail_template, orderVO);  //템플릿에 orderVO의 주문자 정보로 대체합니다.
			
			messageHelper.setSubject(subject);
			messageHelper.setTo(to);
			messageHelper.setFrom("songye9319@gmail.com", "관리자");
			messageHelper.setText(body, true);   
			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private String  readHtmlFile() {
		String mail_template = null;
		 // 버퍼 생성
        BufferedReader br = null;       
         
        // Input 스트림 생성
        InputStreamReader isr = null;   
         
        // File Input 스트림 생성
        FileInputStream fis = null;
        
        File file = null;
        String temp = null;
        
        // File 경로
      //  File file = new File("template/mail_template.html");
        ClassPathResource resource = new ClassPathResource("template/mail_template.html");
 
       
		try {
			file = resource.getFile();
             
//			resource.getFilename(); // 파일 이름
//			resource.getInputStream() // InputStream 객체
//			resource.getPath(); // 파일 경로
//			resource.getURL(); // URL 객체
//			resource.getURI(); // URI 객체
			
            // 파일을 읽어들여 File Input 스트림 객체 생성
            fis = new FileInputStream(file);
             
            // File Input 스트림 객체를 이용해 Input 스트림 객체를 생성하는데 인코딩을 UTF-8로 지정
            isr = new InputStreamReader(fis, "euc-kr");
             
            // Input 스트림 객체를 이용하여 버퍼를 생성
            br = new BufferedReader(isr);
         
            // 버퍼를 한줄한줄 읽어들여 내용 추출
            while( (temp = br.readLine()) != null) {
            	mail_template += temp + "\n";
            }
             
            System.out.println("================== 파일 내용 출력 ==================");
            System.out.println(mail_template);
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
             
        } catch (Exception e) {
            e.printStackTrace();
             
        }
         
		return mail_template;
		
	}
	
	private String setOrderInfo(String mail_template, OrderVO orderVO) {
		String regEx = "_order_id";
	    Pattern pat = Pattern.compile(regEx);
	    
	    Matcher m = pat.matcher(mail_template);
	    mail_template = m.replaceAll(Integer.toString(orderVO.getOrder_id()));
	    
	    
	    regEx = "_goods_id";
	    pat = Pattern.compile(regEx);
	    m = pat.matcher(mail_template);
	    mail_template = m.replaceAll(Integer.toString(orderVO.getGoods_id()));
	    
	    regEx = "_goods_fileName";
	    pat = Pattern.compile(regEx);
	    m = pat.matcher(mail_template);
	    mail_template = m.replaceAll(orderVO.getGoods_fileName());
	    
	    regEx = "_goods_title";
	    pat = Pattern.compile(regEx);
	    m = pat.matcher(mail_template);
	    mail_template = m.replaceAll(orderVO.getGoods_title());
	    
	    
	    
//		mail_template  = mail_template.replace("_order_id", Integer.toString(orderVO.getOrder_id()));
//		mail_template  = mail_template.replace("_goods_id", Integer.toString(orderVO.getGoods_id()));
//		mail_template  = mail_template.replace("_goods_fileName",orderVO.getGoods_fileName());
//		mail_template  = mail_template.replace("_goods_title",orderVO.getGoods_title());
		

	    
		
	    return mail_template;
	}

//	@Async
//	public void sendPreConfiguredMail(String message) {
//		SimpleMailMessage mailMessage = new SimpleMailMessage(preConfiguredMessage);
//		mailMessage.setText(message);
//		mailSender.send(mailMessage);
//	}
}
