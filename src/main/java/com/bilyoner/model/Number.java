package com.bilyoner.model;

import org.springframework.data.annotation.Id;

public class Number {

	@Id
	private String sayi;
	
	private String eklenmeTarihi;

	public Number() {};

	public Number(String sayi, String eklenmeTarihi) {
		super();
		this.sayi = sayi;
		this.eklenmeTarihi = eklenmeTarihi;
	}

	public String getSayi() {
		return sayi;
	}

	public void setSayi(String sayi) {
		this.sayi = sayi;
	}

	public String getEklenmeTarihi() {
		return eklenmeTarihi;
	}

	public void setEklenmeTarihi(String eklenmeTarihi) {
		this.eklenmeTarihi = eklenmeTarihi;
	}




}
