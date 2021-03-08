package com.merlincrm.application.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cm_item")
public class Item implements Serializable {
	
	private static final long serialVersionUID = 5185889482378257814L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
	
	@NotNull
	private String channel;
	
	@NotNull
	private String cip;
	
	@NotNull
	private String cname;
	
	@NotNull
	private String url;
	
	@NotNull
	private String referrer;
	
	@NotNull
	private String sh;
	
	@NotNull
	private String sw;
	
	@NotNull
	private String lang;
	
	@JsonIgnore
	@NotNull
	private String sUserAgent;
	
	@NotNull
	private String device;
	
	@JsonIgnore
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date createTime = new Date();
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("channel", this.channel);
		map.put("cip", this.cip);
		map.put("cname", this.cname);
		map.put("url", this.url);
		map.put("referrer", this.referrer);
		map.put("sh", this.sh);
		map.put("sw", this.sw);
		map.put("lang", this.lang);
		map.put("sUserAgent", this.sUserAgent);
		map.put("device", this.device);
		map.put("createTime", this.createTime);
		return map;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCip() {
		return cip;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getSh() {
		return sh;
	}

	public void setSh(String sh) {
		this.sh = sh;
	}

	public String getSw() {
		return sw;
	}

	public void setSw(String sw) {
		this.sw = sw;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getsUserAgent() {
		return sUserAgent;
	}

	public void setsUserAgent(String sUserAgent) {
		this.sUserAgent = sUserAgent;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
