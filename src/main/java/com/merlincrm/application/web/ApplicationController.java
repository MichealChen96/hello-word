package com.merlincrm.application.web;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.merlincrm.application.dao.ApplicationDao;
import com.merlincrm.application.dao.DocumentDao;
import com.merlincrm.application.dao.EnterSchoolLogDao;
import com.merlincrm.application.dao.StudentDao;
import com.merlincrm.application.domain.Application;
import com.merlincrm.application.domain.Document;
import com.merlincrm.application.domain.EnterSchoolLog;
import com.merlincrm.application.domain.Student;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/enterschool/application")
public class ApplicationController {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private DocumentDao documentDao;
	
	@Autowired
	private EnterSchoolLogDao enterSchoolLogDao;
	
	@GetMapping("/list/{id}")
	public Map<String, Object> list(@PathVariable("id") Long id,
			@RequestParam(value = "page", required = true) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			HttpServletResponse response) throws IOException {
		Student user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<Student> userOpt = studentDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		
		user = userOpt.get();
		List<Application> applications = applicationDao.findApplicationsByUser(user);
		List<Map<String, Object>> applications1 = new ArrayList<Map<String, Object>>();
		for (Application application : applications) {
			applications1.add(application.toMap());
		}
		
		data.put("code", "SUCCESS");
		data.put("applications", applications1);
		return data;
	}
	
	@GetMapping("/precreate/{id}")
	public Map<String, Object> precreate(
			@PathVariable("id") Long id,
			 HttpServletResponse response)
			throws IOException {
		Student user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<Student> userOpt = studentDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		
		Date now1 = new Date();
		SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = sim1.format(now1);
		try {
			now1 = sim1.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar1 = new GregorianCalendar(); 
		calendar1.setTime(now1); 
		calendar1.add(Calendar.DATE, 1);
		Calendar calendar2 = new GregorianCalendar(); 
		calendar2.setTime(now1); 
		calendar2.add(Calendar.DATE, 2);
		Calendar calendar3 = new GregorianCalendar(); 
		calendar3.setTime(now1); 
		calendar3.add(Calendar.DATE, 3);
		List<Date> dates = new ArrayList<Date>();
		dates.add(now1);
		dates.add(calendar1.getTime());
		dates.add(calendar2.getTime());
		dates.add(calendar3.getTime());
		data.put("dates", dates);
		
		user = userOpt.get();
		Date now = new Date();
		Application application = null;
		List<Application> applications = applicationDao.findApplicationsByUser(user);
		if (applications.size() > 0 ) {
			application = applications.get(0);
			data.put("application", application.toMap());
		} else {
			data.put("application", application);
			data.put("code", "SUCCESS");
			return data;
		}
		
		if (application.getStatus() == Application.STATUS_SUBMIT && now.before(application.getExpirationTime())) {
			data.put("code", "SUCCESS");
			data.put("status", "20");
			data.put("Msg", "学生入校申请正在审核中");
			return data;
		} else if (application.getStatus() == Application.STATUS_SUBMIT && now.after(application.getExpirationTime())) {
			data.put("code", "SUCCESS");
			data.put("status", "30");
			data.put("Msg", "学生入校申请未通过");
			return data;
		}
		
		if (application.getStatus() == Application.STATUS_REJECT) {
			data.put("code", "SUCCESS");
			data.put("status", "30");
			data.put("Msg", "学生入校申请未通过");
			return data;
		}
		
		if(now.before(application.getSelectTime()) && application.getStatus() == Application.STATUS_PASS) {
			data.put("code", "SUCCESS");
			data.put("status", "31");
			data.put("Msg", "还没有到学生选择的入校时间");
			return data;
		} else if (now.after(application.getExpirationTime()) && application.getStatus() == Application.STATUS_PASS) {
			data.put("code", "SUCCESS");
			data.put("status", "40");
			data.put("Msg", "该入学申请已失效");
			return data;
		} else {
			data.put("code", "SUCCESS");
			data.put("status", "31");
			return data;
		}
	}
	
	@PostMapping("/create/{id}")
	public Map<String, Object> create(
			@PathVariable("id") Long id,
			@RequestParam(value = "selectDate", required = false) Date selectDate,
			@RequestParam(value = "result", required = false) String result,
			 HttpServletResponse response)
			throws IOException {
		Student user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<Student> userOpt = studentDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		user = userOpt.get();
		if (selectDate == null ) {
			data.put("code", "ERROR");
			data.put("errorField", "selectDate");
			data.put("Msg","selectDate not be null.");
			return data;
		}
		
		if (StringUtils.isBlank(result)) {
			data.put("code", "ERROR");
			data.put("errorField", "result");
			data.put("Msg", "result not be null.");
			return data;
		}
		Application application = null;
		List<Application> applications = applicationDao.findApplicationsByUser(user);
		if (applications.size() > 0 ) {
			application = applications.get(0);
		} else {
			application = new Application();
		}
		
		UUID uid = UUID.randomUUID();
		
		application.setApplicationUser(user);
		application.setSelectTime(selectDate);
		application.setResult(result);
		
		Calendar calendar = new GregorianCalendar(); 
		calendar.setTime(selectDate); 
		calendar.add(Calendar.DATE, 4);
		application.setExpirationTime(calendar.getTime());
		application.setStatus(Application.STATUS_SUBMIT);
		application.setUuid(uid.toString());
		applicationDao.save(application);
		
		data.put("code", "SUCCESS");
		data.put("application", application.toMap());
		return data;
	}
	
	@GetMapping("/profile/{id}")
	public Map<String, Object> porfile(@PathVariable("id") Long id,
			HttpServletResponse response) throws IOException {
		Student user = null;
		Map<String, Object> data = new HashMap<>();
		Optional<Student> userOpt = studentDao.findById(id);
		if (userOpt.isPresent() ==  false) {
			data.put("code", "ERROR");
			data.put("Msg", "User not find.");
			return data;
		}
		user = userOpt.get();
		Application application = null;
		List<Application> applications = applicationDao.findApplicationsByUser(user);
		if (applications.size() <= 0) {
			data.put("code", "ERROR");
			data.put("Msg", "Application not find.");
			return data;
		}
		application = applications.get(0);
		String url ="/enterschool/application/qrcode/" + application.getUuid();
		Date now = new Date();
		if (application.getStatus() == Application.STATUS_PASS) {
			application.setCodeImageUrl(url);
		}
		long s = application.getExpirationTime().getTime();
		s = s-1;
		Date newTime = new Date(s);
		application.setExpirationTime(newTime);
		getImg(data, application.getApplicationUser());
		data.put("code", "SUCCESS");
		data.put("application", application.toMap());
		data.put("user", user.toMap());
		return data;
	}
	
	void getImg(Map<String, Object> data, Student student) {
		List<Document> doucmentsType1 = documentDao.findDocumentByStudent(student, 1);
		List<Document> doucmentsType2 = documentDao.findDocumentByStudent(student, 2);
		if (doucmentsType1.size() > 0) {
			data.put("imageType1", "/enterschool/documents/downloadfile/" + doucmentsType1.get(0).getId());
		}
		
		if (doucmentsType2.size() > 0) {
			data.put("imageType2", "/enterschool/documents/downloadfile/" + doucmentsType2.get(0).getId());
		}
	}
	
	@GetMapping("/getapplication/{uuid}")
	public Map<String, Object> getApplicationForUUid(
			@PathVariable("uuid") String uuid,
			HttpServletResponse response) throws IOException {
		Map<String, Object> data = new HashMap<>();
		Application application = null;
		Optional<Application> applicationOpt = applicationDao.findApplicationForUUid(uuid);
		if (applicationOpt.isPresent() == false) {
			data.put("code", "ERROR");
			data.put("Msg", "申请没找到");
			return data;
		}
		application = applicationOpt.get();
		Date now = new Date();
		if (application.getStatus() == Application.STATUS_SUBMIT && now.before(application.getExpirationTime())) {
			data.put("code", "ERROR");
			data.put("Msg", "学生入校申请正在审核中");
			return data;
		} else if (application.getStatus() == Application.STATUS_SUBMIT && now.after(application.getExpirationTime())) {
			data.put("code", "ERROR");
			data.put("Msg", "学生入校申请未通过");
			return data;
		}
		
		if (application.getStatus() == Application.STATUS_REJECT) {
			data.put("code", "ERROR");
			data.put("Msg", "学生入校申请未通过");
			return data;
		}
		
		if(now.before(application.getSelectTime()) && application.getStatus() == Application.STATUS_PASS) {
			data.put("code", "ERROR");
			data.put("Msg", "还没有到学生选择的入校时间");
			return data;
		} else if (now.after(application.getExpirationTime()) && application.getStatus() == Application.STATUS_PASS) {
			data.put("code", "ERROR");
			data.put("Msg", "该入学申请已失效");
			return data;
		} else {
			EnterSchoolLog enterSchoolLog = new EnterSchoolLog();
			enterSchoolLog.setStudent(application.getApplicationUser());
			enterSchoolLogDao.save(enterSchoolLog);
			data.put("code", "SUCCESS");
			data.put("application", application.toMap());
			return data;
		}
	}
	
	
	@RequestMapping(value = "/qrcode/{code}", produces = "text/html")
	public String qrcode(@PathVariable("code") String code,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("image/png");
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		Writer writer = new QRCodeWriter();
		BitMatrix matrix;
		
		MatrixToImageConfig config;
			config = new MatrixToImageConfig();
		try {
			matrix = writer.encode(code, BarcodeFormat.QR_CODE, 300, 300, hints);
			MatrixToImageWriter.writeToStream(matrix, "PNG", response.getOutputStream(), config);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return null;
	}
	
	@PostMapping("/getenterschoollist")
	public Map<String, Object> getEnterschoolList(
			@RequestParam(value = "stattime", required = false) String strstattime,
			@RequestParam(value = "endtime", required = false) String strendtime,
			HttpServletResponse response) throws IOException {
		Map<String, Object> data = new HashMap<>();
		List<EnterSchoolLog> enterSchoolLogs = null;
		
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date stattime = null;
		Date endtime = null;
		try {
			if (StringUtils.isNotBlank(strstattime) ) {
				stattime = sim.parse(strstattime);
			}
			if (StringUtils.isNotBlank(strendtime) ) {
				endtime = sim.parse(strendtime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (stattime == null && endtime != null) {
			enterSchoolLogs = enterSchoolLogDao.findEnterSchoolLogList3(endtime);
		} else if (stattime != null && endtime == null) {
			enterSchoolLogs = enterSchoolLogDao.findEnterSchoolLogList2(stattime);
		} else if (stattime != null && endtime != null) {
			enterSchoolLogs = enterSchoolLogDao.findEnterSchoolLogList1(stattime, endtime);
		} else {
			enterSchoolLogs = enterSchoolLogDao.findEnterSchoolLogAll();
		}
		
		data.put("code", "SUCCESS");
		data.put("enterSchooleLists", enterSchoolLogs);
		return data;
	}
	
}
