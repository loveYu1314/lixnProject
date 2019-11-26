package com.lixn.demo.service.impl;


import com.lixn.demo.service.IDemoService;
import com.lixn.spring.annotation.MyService;

@MyService
public class DemoService implements IDemoService {

	public String get(String name) {
		return "My name is " + name;
	}

}
