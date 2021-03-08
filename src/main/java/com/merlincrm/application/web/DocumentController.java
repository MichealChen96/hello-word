package com.merlincrm.application.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.merlincrm.application.config.AppCofig;
import com.merlincrm.application.dao.ApplicationDao;
import com.merlincrm.application.dao.DocumentDao;
import com.merlincrm.application.dao.StudentDao;
import com.merlincrm.application.domain.Document;
import com.merlincrm.application.domain.Student;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/enterschool/documents")
public class DocumentController {
	
	@Autowired
	private ApplicationDao applicationDao;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private AppCofig appConfig;
	
	@Autowired
	private DocumentDao documentDao;
	
	@PostMapping("/uploadfile")
	public Map<String, Object> uploadFile(
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "type", required = true) Integer type,
			@RequestParam(value = "files", required = true) MultipartFile files, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Map<String, Object> data = new HashMap<>();

		Student student = null;
		Optional<Student> studentOpt = studentDao.findById(id);
		if (studentOpt.isPresent() == false) {
			data.put("code", "ERROR");
			data.put("Msg", "Application not find.");
			return data;
		}
		if (type != 1 && type != 2) {
			data.put("code", "ERROR");
			data.put("Msg", "File type not find.");
			return data;
		}
		student = studentOpt.get();
		File file = null;
		if (!files.isEmpty()) {
			String fileName = files.getOriginalFilename();
			String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
			String filePath = appConfig.getFilePath() + "/photo/" + student.getStudentId() + type;
			file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			InputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = files.getInputStream();
				fos = new FileOutputStream(file);
				int len;
				while ((len = fis.read()) != -1) {
					fos.write(len);
				}
				fis.close();
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				data.put("errorCode", "");
				data.put("errorMessage", "Error saving file.");
				return data;
			}
			Document document = null;
			List<Document> documents = documentDao.findDocumentByStudent(student, type);
			if (documents.size() > 0) {
				document = documents.get(0);
			} else {
				document = new Document();
			}
			
			document.setStudent(student);
			document.setFileName(files.getName());
			document.setTypeName(fileType);
			document.setUrl(filePath);
			if (type == 1) {
				document.setType(Document.STATUS_GREEN_CODE);
			} else {
				document.setType(Document.STATUS_HEALTH_CODE);
			}
			documentDao.save(document);
		} else {
			data.put("code", "ERROR");
			data.put("errorField", "files");
			data.put("Msg", "files has error");
		}
		
		data.put("code", "SUCCESS");
		data.put("Msg", "upload file success");
		return data;
	}
	
	@GetMapping("/downloadfile/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Optional<Document> documentOpt = documentDao.findById(id);
		if (documentOpt.isPresent() == false) {
			return null;
		}
		Document document = documentOpt.get();
		
		String fileurl = document.getUrl();
		File file = new File(fileurl);
		if (!file.exists()) {
			return null;
		}
		response.setContentType("application/octet-stream");
		response.setHeader("content-type", "image/jpeg");
		InputStream fis = null;
		ServletOutputStream fos = null;
		long lastModified = request.getDateHeader("If-Modified-Since");
		try {
			if (lastModified < file.lastModified()) {
				response.setDateHeader("Last-Modified", file.lastModified());
				response.setContentLength((int) file.length());
				response.setStatus(HttpServletResponse.SC_OK);
				
//				response.setHeader("Content-Disposition", "attachment; filename=" + document.getFileName() + document.getTypeName());
				fis = new FileInputStream(file);
				fos = response.getOutputStream();
				int len;
				while ((len = fis.read()) != -1) {
					fos.write(len);
				}
				fis.close();
				fos.flush();
				fos.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception ex) {
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception ex) {
			}
		}
		return null;
	}
	
	public String generateRandomFilename() {
		String RandomFilename = "";
		Random rand = new Random();
		int random = rand.nextInt();

		Calendar calCurrent = Calendar.getInstance();
		int intDay = calCurrent.get(Calendar.DATE);
		int intMonth = calCurrent.get(Calendar.MONTH) + 1;
		int intYear = calCurrent.get(Calendar.YEAR);
		String now = String.valueOf(intYear) + "_" + String.valueOf(intMonth) + "_" + String.valueOf(intDay) + "_";

		RandomFilename = now + String.valueOf(random > 0 ? random : (-1) * random);

		return RandomFilename;
	}

}
