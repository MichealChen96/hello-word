package com.merlincrm.application.web;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merlincrm.application.config.AppCofig;
import com.merlincrm.application.dao.ApplicationDao;
import com.merlincrm.application.dao.ClasseDao;
import com.merlincrm.application.dao.ClasseTeacherDao;
import com.merlincrm.application.dao.DocumentDao;
import com.merlincrm.application.dao.StudentDao;
import com.merlincrm.application.domain.Application;
import com.merlincrm.application.domain.Classe;
import com.merlincrm.application.domain.ClasseTeacher;
import com.merlincrm.application.domain.Student;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/enterschool/classeteacher")
public class ClasseTeacherController {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private AppCofig appConfig;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private ClasseTeacherDao classeTeacherDao;
	
	@Autowired
	private DocumentDao documentDao;
	
	static final int CONNECTION_TIME_OUT = 20000;
	
	@PostMapping("/login")
	public Map<String, Object> login(@RequestParam(value = "ticket", required = false) String ticket, HttpServletResponse response)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("service", appConfig.getSsoServiceTeacher());
		parameters.put("ticket", ticket);

		String result = httpRequest("https://sso.fdsm.fudan.edu.cn/serviceValidate", parameters);
		Map<String, String> map = readStringXmlOut(result);
		String uid = map.get("employeeNumber");
		System.out.println(result);
		ClasseTeacher teacher = null;
		Map<String, Object> parameters1 = new HashMap<String, Object>();
		parameters1.put("uid", uid);
		if (StringUtils.isNotBlank(uid)) {
			Optional<ClasseTeacher> userOpt = classeTeacherDao.findUserByUid(Long.valueOf(uid));
			if (userOpt.isPresent()) {
				teacher = userOpt.get();
				data.put("user", teacher);
			} else {
				data.put("code", "ERROR");
				data.put("Msg", "用户没找到");
				return data;
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
	
	@PostMapping("/login1")
	public Map<String, Object> login1( HttpServletResponse response)
			throws IOException {
		Map<String, Object> data = new HashMap<>();

		ClasseTeacher teacher = null;
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findUserByUid(Long.valueOf(42088));
		if (userOpt.isPresent()) {
			teacher = userOpt.get();
			data.put("user", teacher);
		} else {
			data.put("code", "ERROR");
			data.put("Msg", "用户没找到");
			return data;
		}
		data.put("code", "SUCCESS");
		return data;
	}
	
	@GetMapping("/list/{id}")
	public Map<String, Object> list(@PathVariable("id") Long id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "selectTimeStat", required = false) String selectTimeStatStr,
			@RequestParam(value = "selectTimeEnd", required = false) String selectTimeEndStr,
			@RequestParam(value = "page", required = true) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			HttpServletResponse response) throws IOException {
		ClasseTeacher user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		Date selectTimeStat = formmateDate(selectTimeStatStr);
		Date selectTimeEnd = formmateDate(selectTimeEndStr);
		if (selectTimeEnd != null ) {
        	Calendar calendar = Calendar.getInstance() ;
    		calendar.setTime(selectTimeEnd);
    		calendar.add(Calendar.DATE, 1);
    		selectTimeEnd = calendar.getTime();
        }
		
		user = userOpt.get();
		PageRequest pageRequest = PageRequest.of(page, pageSize);
		Page<Application> applications1 = ApplicationDao.findApplications(entityManager, user, name, selectTimeStat, selectTimeEnd, pageRequest);
		List<Map<String, Object>> application1 = new ArrayList<Map<String, Object>>();
		for (Application application : applications1) {
			long s = application.getExpirationTime().getTime();
			s = s-1;
			Date newTime = new Date(s);
			application.setExpirationTime(newTime);
			application1.add(application.toMap());
		}
		
		data.put("code", "SUCCESS");
		data.put("applications", application1);
		data.put("page", applications1.getNumber());
		data.put("totalPages", applications1.getTotalPages());
		data.put("totalElements", applications1.getTotalElements());
		return data;
	}
	
	@GetMapping("/preaudit/{id}")
	public Map<String, Object> preAudit(
			@PathVariable("id") Long id,
			@RequestParam(value = "applicationid", required = true) Long applicationid,
			 HttpServletResponse response)
			throws IOException {
		ClasseTeacher user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		user = userOpt.get();
		Application application = null;
		Optional<Application> applicationOpt = applicationDao.findById(applicationid);
//		if (applications.size() > 0 ) {
//			application = applications.get(0);
//			data.put("application", application.toMap());
//		} else {
//			data.put("application", application);
//		}
		if (applicationOpt.isPresent() == false ) {
			data.put("code", "ERROR");
			data.put("Msg", "入校申请未找到");
			return data;
		} 
		application = applicationOpt.get();
		long s = application.getExpirationTime().getTime();
		s = s-1;
		Date newTime = new Date(s);
		application.setExpirationTime(newTime);
		data.put("code", "SUCCESS");
		data.put("application", application.toMap());
		return data;
	}
	
	@PostMapping("/auditpass/{id}")
	public Map<String, Object> auditPass(
			@PathVariable("id") Long id,
			@RequestParam(value = "applicationid", required = true) Long applicationid,
			 HttpServletResponse response)
			throws IOException {
		ClasseTeacher user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		user = userOpt.get();
		Application application = null;
		Optional<Application> applicationOpt = applicationDao.findById(applicationid);
		if (applicationOpt.isPresent() == false ) {
			data.put("code", "ERROR");
			data.put("Msg", "入校申请未找到");
			return data;
		} 
		application = applicationOpt.get();
		application.setStatus(Application.STATUS_PASS);
		applicationDao.save(application);
		
		data.put("application", application.toMap());
		data.put("code", "SUCCESS");
		return data;
	}
	
	@PostMapping("/auditreject/{id}")
	public Map<String, Object> auditReject(
			@PathVariable("id") Long id,
			@RequestParam(value = "applicationid", required = true) Long applicationid,
			 HttpServletResponse response)
			throws IOException {
		ClasseTeacher user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		user = userOpt.get();
		Application application = null;
		Optional<Application> applicationOpt = applicationDao.findById(applicationid);
		if (applicationOpt.isPresent() == false ) {
			data.put("code", "ERROR");
			data.put("Msg", "入校申请未找到");
			return data;
		} 
		application = applicationOpt.get();
		application.setStatus(Application.STATUS_REJECT);
		applicationDao.save(application);
		
		data.put("application", application.toMap());
		data.put("code", "SUCCESS");
		return data;
	}
	
	@GetMapping("/count/{id}")
	public Map<String, Object> count(@PathVariable("id") Long id,
			HttpServletResponse response) throws IOException {
		ClasseTeacher user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		user = userOpt.get();
		Long count1 = applicationDao.countApplication(user, Application.STATUS_SUBMIT);
		Long count2 = applicationDao.countApplication(user, Application.STATUS_REJECT);
		Long count3 = applicationDao.countApplication(user, Application.STATUS_PASS);
		data.put("code", "SUCCESS");
		data.put("countApplicationSubmit", count1);
		data.put("countApplicationReject", count2);
		data.put("countApplicationPass", count3);
		data.put("user", user);
		return data;
	}
	
	@GetMapping("/exporeExcel/{id}")
	public Map<String, Object> exportExcel(@PathVariable("id") Long id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "selectTimeStat", required = false) String selectTimeStatStr,
			@RequestParam(value = "selectTimeEnd", required = false) String selectTimeEndStr,
			HttpServletResponse response) throws IOException {
		Optional<ClasseTeacher> userOpt = classeTeacherDao.findById(id);
		ClasseTeacher user = null;
		Map<String, Object> data = new HashMap<>();
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		Date selectTimeStat = formmateDate(selectTimeStatStr);
		Date selectTimeEnd = formmateDate(selectTimeEndStr);
		if (selectTimeEnd != null ) {
        	Calendar calendar = Calendar.getInstance() ;
    		calendar.setTime(selectTimeEnd);
    		calendar.add(Calendar.DATE, 1);
    		selectTimeEnd = calendar.getTime();
        }
		user = userOpt.get();
		List<Application> applications = ApplicationDao.findApplications(entityManager, user, name, selectTimeStat, selectTimeEnd);
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
//		String leaveDateStartStr = null;

		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet();
		int rowIndex = 0;
		HSSFRow row = sheet.createRow(rowIndex++);
		int columnIndex = 0;
		HSSFCell cell = null;
		
		cell = row.createCell(columnIndex++);
		cell.setCellValue("申请状态");
		
		cell = row.createCell(columnIndex++);
		cell.setCellValue("姓名");
		
		cell =row.createCell(columnIndex++);
		cell.setCellValue("项目");
		
		cell =row.createCell(columnIndex++);
		cell.setCellValue("班级");
		
		cell =row.createCell(columnIndex++);
		cell.setCellValue("进校原因");
		
		cell =row.createCell(columnIndex++);
		cell.setCellValue("入校时间");
		
		cell =row.createCell(columnIndex++);
		cell.setCellValue("有效日期");
		
//		cell = row.createCell(columnIndex++);
//		cell.setCellValue("是否有随申码截图");
//		
//		cell =row.createCell(columnIndex++);
//		cell.setCellValue("是否有行程码截图");
		
		for (Application application : applications) {
			row = sheet.createRow(rowIndex++);
			columnIndex = 0;
			
			cell = row.createCell(columnIndex++);
			if (application.getStatus() == Application.STATUS_SUBMIT ) {
				cell.setCellValue("未审核");
			} else if (application.getStatus() == Application.STATUS_REJECT ) {
				cell.setCellValue("已拒绝");
			} else if (application.getStatus() == Application.STATUS_PASS ) {
				cell.setCellValue("已审核");
			} else if (application.getExpirationTime().before(new Date())) {
				cell.setCellValue("已过期");
			} else {
				cell.setCellValue("草稿");
			}
			
			cell = row.createCell(columnIndex++);
			cell.setCellValue(application.getApplicationUser().getName());
			
			cell = row.createCell(columnIndex++);
			cell.setCellValue(application.getApplicationUser().getClasse().getProgram());
			
			cell = row.createCell(columnIndex++);
			cell.setCellValue(application.getApplicationUser().getClasse().getClasseName());
			
			cell = row.createCell(columnIndex++);
			cell.setCellValue(application.getResult());
			
			cell = row.createCell(columnIndex++);
			cell.setCellValue(sim.format(application.getSelectTime()));
			
			cell = row.createCell(columnIndex++);
			cell.setCellValue(sim.format(application.getExpirationTime()));
			
//			List<com.merlincrm.application.domain.Document> doucmentsType1 = documentDao.findDocumentByStudent(application.getApplicationUser(), 1);
//			List<com.merlincrm.application.domain.Document> doucmentsType2 = documentDao.findDocumentByStudent(application.getApplicationUser(), 2);
//			cell = row.createCell(columnIndex++);
//			if (doucmentsType1.size() > 0) {
//				cell.setCellValue("是");
//			} else {
//				cell.setCellValue("否");
//			}
//			cell = row.createCell(columnIndex++);
//			if (doucmentsType2.size() > 0) {
//				cell.setCellValue("是");
//			} else {
//				cell.setCellValue("否");
//			}
			
//			cell = row.createCell(columnIndex++);
//			cell.setCellValue(application.getExpirationTime());
			
		}

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=applicationsList.xls");

		try {
			OutputStream stream = response.getOutputStream();
			workBook.write(stream);
			stream.flush();
			stream.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
	
	public Date formmateDate(String datestr) {
		Date date = null;
		if (StringUtils.isBlank(datestr)) {
			return null;
		}
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = sim.parse(datestr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

}
