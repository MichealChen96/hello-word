package com.merlincrm.application.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merlincrm.application.config.AppCofig;
import com.merlincrm.application.dao.ApplicationDao;
import com.merlincrm.application.dao.ClasseDao;
import com.merlincrm.application.dao.ClasseTeacherDao;
import com.merlincrm.application.dao.StudentDao;
import com.merlincrm.application.domain.Classe;
import com.merlincrm.application.domain.Student;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/enterschool/students")
public class StudentController {
	
	@Autowired
	private AppCofig appConfig;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private ClasseTeacherDao classeTeacherDao;
	
	@Autowired
	private ClasseDao classeDao;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	static final int CONNECTION_TIME_OUT = 20000;
	
	@PostMapping("/login")
	public Map<String, Object> login(@RequestParam(value = "ticket", required = false) String ticket, HttpServletResponse response)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("service", appConfig.getSsoService());
		parameters.put("ticket", ticket);

		String result = httpRequest("https://sso.fdsm.fudan.edu.cn/serviceValidate", parameters);
		Map<String, String> map = readStringXmlOut(result);
		String uid = map.get("employeeNumber");
		System.out.println(result);
		Student student = null;
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		parameters1.put("uid", uid);
		if (StringUtils.isNotBlank(uid)) {
			Optional<Student> userOpt = studentDao.findUserByUid(Long.valueOf(uid));
			if (userOpt.isPresent()) {
				student = userOpt.get();
				String result1 = httpRequest("https://app.fdsm.fudan.edu.cn/api/v1/student/getInfo/" + uid, null);
				
//				String result1 = httpRequest("http://192.168.1.144/api/get-student", parameters1);
				JSONObject data1 = new JSONObject(result1);
				if (data1.has("state") && data1.get("state").equals("success") && data1.has("data")) {
					JSONObject data2 = data1.getJSONObject("data");
					System.out.println(data2);
					String stu_id = null, name = null, career = null, program = null, className = null, imgUrl = null;
					if (data2.has("stu_id")) {
						stu_id = data2.getString("stu_id");
					}
					if (StringUtils.isBlank(stu_id)) {
						stu_id = "无";
					}
					if (data2.has("name")) {
						name = data2.getString("name");
					}
					if (data2.has("career")) {
						career = data2.getString("career");
					}
					if (data2.has("program")) {
						program = data2.getString("program");
					}
					if (data2.has("class")) {
						className = data2.getString("class");
					}
					if (StringUtils.isBlank(className)) {
						className = "N/A";
					}
					if (data2.has("imgUrl")) {
						imgUrl = data2.getString("imgUrl");
					}
					if (StringUtils.isBlank(stu_id) || StringUtils.isBlank(name) 
							|| StringUtils.isBlank(career) || StringUtils.isBlank(program) 
							|| StringUtils.isBlank(className) || StringUtils.isBlank(imgUrl)) {
						data.put("code", "ERROR");
						data.put("Msg", "用户数据未同步到,请稍后重试");
						return data;
					} 
					Classe classe = null;
					Optional<Classe> classeOpt = classeDao.findClasse(program, className);
					if (classeOpt.isPresent()) {
						classe = classeOpt.get();
					} else {
						classe = new Classe();
						classe.setClasseName(className);
						classe.setProgram(program);
						classeDao.save(classe);
					}
					student.setCareer(career);
					student.setName(name);
					student.setStudentId(stu_id);
					student.setImageUrl(imgUrl);
					studentDao.save(student);
					data.put("user", student);
				} else {
					data.put("code", "ERROR");
					data.put("Msg", "用户数据同步出错,请稍后重试");
					return data;
				}
			} else {
				String result1 = httpRequest("https://app.fdsm.fudan.edu.cn/api/v1/student/getInfo/" + uid, null);
//				String result1 = httpRequest("http://192.168.1.144/api/get-student", parameters1);
				System.out.println(result1);
				JSONObject data1 = new JSONObject(result1);
				if (data1.has("state") && data1.get("state").equals("success") && data1.has("data")) {
					String stu_id = null, name = null, career = null, program = null, className = null, imgUrl = null;
					JSONObject data2 = data1.getJSONObject("data");
					System.out.println(data2);
					if (data2.has("stu_id")) {
						stu_id = data2.getString("stu_id");
					}
					if (StringUtils.isBlank(stu_id)) {
						stu_id = "无";
					}
					if (data2.has("name")) {
						name = data2.getString("name");
					}
					if (data2.has("career")) {
						career = data2.getString("career");
					}
					if (data2.has("program")) {
						program = data2.getString("program");
					}
					if (data2.has("class")) {
						className = data2.getString("class");
					}
					 
					if (StringUtils.isBlank(className)) {
						className = "N/A";
					}
					
					if (data2.has("imgUrl")) {
						imgUrl = data2.getString("imgUrl");
					}
					if (StringUtils.isBlank(stu_id) || StringUtils.isBlank(name) 
							|| StringUtils.isBlank(career) || StringUtils.isBlank(program) 
							|| StringUtils.isBlank(className) || StringUtils.isBlank(imgUrl)) {
						data.put("code", "ERROR");
						data.put("Msg", "用户数据未同步到,请稍后重试");
						return data;
					} 
					student = new Student();
					Classe classe = null;
					Optional<Classe> classeOpt = classeDao.findClasse(program, className);
					if (classeOpt.isPresent()) {
						classe = classeOpt.get();
					} else {
						classe = new Classe();
						classe.setClasseName(className);
						classe.setProgram(program);
						classeDao.save(classe);
					}
					student.setClasse(classe);
					student.setCareer(career);
					student.setName(name);
					student.setStudentId(stu_id);
					student.setUid(Long.valueOf(uid));
					student.setImageUrl(imgUrl);
					studentDao.save(student);
					data.put("user", student);
				} else {
					data.put("code", "ERROR");
					data.put("Msg", "用户数据同步出错,请稍后重试");
					return data;
				}
			}
		} else {
			data.put("code", "ERROR");
			data.put("Msg", "uid 没找到");
			return data;
		}
		data.put("code", "SUCCESS");
		data.put("result", result);
		return data;
	}
	
	public static Map<String, String> readStringXmlOut(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
//			System.out.println("根节点：" + rootElt.getName());

			Iterator iter = rootElt.elementIterator("authenticationSuccess");
			while (iter.hasNext()) {
				Element recordEle = (Element) iter.next();
				String user = recordEle.elementTextTrim("user");
				map.put("user", user);
				Iterator iters = recordEle.elementIterator("attributes");
				while (iters.hasNext()) {
					Element itemEle = (Element) iters.next();
					String username = itemEle.elementTextTrim("username");
					String displayName = itemEle.elementTextTrim("displayName");
					String employeeNumber = itemEle.elementTextTrim("employeeNumber");
					String name = itemEle.elementTextTrim("name");

					map.put("username", username);
					map.put("displayName", displayName);
					map.put("employeeNumber", employeeNumber);
					map.put("name", name);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static String httpRequest(String url, Map<String, Object> parameters) {
		StringBuffer sb = new StringBuffer(url);
		String response = null;
		GetMethod getMethod = null;
		try {
			HttpClient httpClient = new HttpClient();
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIME_OUT);
			if (parameters != null && !parameters.isEmpty()) {
				sb.append("?");
				for (String key : parameters.keySet()) {
					sb.append(key).append("=").append(parameters.get(key)).append("&");
				}
				sb.deleteCharAt(sb.length() - 1);
			}

			// System.out.println("bbb=" + sb.toString());
			getMethod = new GetMethod(sb.toString());
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			int returnCode = httpClient.executeMethod(getMethod);
			// System.out.println("returnCode=" + returnCode);
			if (returnCode == org.apache.commons.httpclient.HttpStatus.SC_OK) {
				response = getMethod.getResponseBodyAsString().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				getMethod.releaseConnection();
			} catch (Exception e) {
			}
		}

		return response;
	}
	
}
