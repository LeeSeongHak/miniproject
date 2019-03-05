package global.sesoc.project1.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import global.sesoc.project1.dao.UserDAO;
import global.sesoc.project1.vo.UserVO;

@Controller
public class UserController {

	@Autowired
	UserDAO dao;
	
	private final static Logger logger = LoggerFactory.getLogger(DiaryController.class);

	//Login Form
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(String id, String password, Model model, HttpSession session){
		UserVO vo = null;
		vo = dao.login(id, password);
		
		if(vo == null){
			model.addAttribute("vo", vo);
			return "redirect:/";
		}
		
		else{
			session.setAttribute("id", id);
			session.setAttribute("password", password);
			session.setAttribute("name", vo.getName());
			session.setAttribute("country", vo.getCountry());
			session.setAttribute("region", vo.getRegion());
			translate(session);
			
			return "weatherTest";
		}
	}
	
	//Join Form
	@RequestMapping(value = "/join", method= RequestMethod.GET)
	public String join1(){
		return "user/join";
	}
	
	@RequestMapping(value="/join", method=RequestMethod.POST)
	public String join2(String custid, UserVO vo){
		vo.setid(custid);
		int cnt=0;
		cnt = dao.join(vo);
		
		if(cnt==0){
			return " user/join";
		}
		else{
			return "redirect:/";
		}
	}
	
	//ID �ߺ�Ȯ��
	@RequestMapping(value="/idCheck", method=RequestMethod.GET)
	public String idCheck(){
		return "user/idCheck";
	}
	
	//ID �ߺ�Ȯ��2
	@RequestMapping(value="/idCheck", method=RequestMethod.POST)
	public String idCheck2(String searchId, Model model){
		//ID�� �����Ͽ� �˻� ����� VO ��ü�� ����
		UserVO vo = dao.getCustomer(searchId);
		//�˻� ����� Model�� �����ϰ� JSP�� �ٽ� �̵�
		model.addAttribute("searchId", searchId);
		//�˻��� ����� ��� ����.
		model.addAttribute("searchResult", vo);
		//�˻��ؼ� null����, �˻����ؼ� null���� Ȯ���ϱ� ���� search �ϳ� �� ���� ��. result�� search ��� null�̸� �˻����� ���Ѱ�.
		model.addAttribute("search", true);
		return "user/idCheck";
	}
	
	//@����
	@RequestMapping(value = "/translate", method=RequestMethod.GET)
	public void translate(HttpSession session){
		String clientId = "ibvum1Y0Dx5JXH1pXGDp";//���ø����̼� Ŭ���̾�Ʈ ���̵�";
        String clientSecret = "4Bwp4Jf6Cg";//���ø����̼� Ŭ���̾�Ʈ ��ũ����";
        String country = (String)session.getAttribute("country") + " " + (String)session.getAttribute("region");
        try {
            String text = URLEncoder.encode(country, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source=ko&target=en&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // ���� ȣ��
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // ���� �߻�
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            logger.debug(response.toString());
            session.setAttribute("country2", response.toString());
            
        } catch (Exception e) {
            logger.debug(e.toString());
        }
        return;
	}
}