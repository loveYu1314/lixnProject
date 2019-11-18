package com.lixn.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/10/27
 * @描述
 */
@Controller
public class IndexController {

    @GetMapping({"/",""})
    public String index(Model model){
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("string", new StringUtil());
        model.addAllAttributes(map);
        return "index";
    }

    public class StringUtil{
        public StringUtil(){}

        public boolean isNotBlank(String value){
            return StringUtils.hasText(value);
        }
    }

    @ModelAttribute(name = "message")
    public String message() {
        return "Hello,World";
    }
}
