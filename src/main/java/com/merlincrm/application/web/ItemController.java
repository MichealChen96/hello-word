package com.merlincrm.application.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merlincrm.application.dao.ItemDao;
import com.merlincrm.application.domain.Item;

@RestController
@RequestMapping(value = "/item")
public class ItemController {
	@Autowired
	private ItemDao itemDao;
	
	@GetMapping("/list")
	public Map<String, Object> list(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			HttpServletResponse response) throws IOException {
		Map<String, Object> data = new HashMap<>();
		List<Item> items = (List<Item>) itemDao.findAll();
		List<Map<String, Object>> items1 = new ArrayList<Map<String, Object>>();
		for (Item item : items) {
			items1.add(item.toMap());
		}
		data.put("code", "SUCCESS");
		data.put("items", items1);
		return data;
	}
}
