package com.lan.task.framework.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lan.task.framework.model.ResultMessage;

@Controller
@RequestMapping("framework")
public class LoginController {
	
	@Value("${login.user}")
	private String loginUser;
	
	@Value("${login.password}")
	private String loginPassword;
	
	@PostMapping("login")
	@ResponseBody
	public ResultMessage login(HttpServletRequest request, String userName, String password){
		if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
			return ResultMessage.error("账号密码不能为空");
		}
		if(loginUser.equals(userName) && loginPassword.equals(password)){
			HttpSession session = request.getSession();
			session.setAttribute("CURRENT_USER", userName);
			return ResultMessage.success();
		}
		return ResultMessage.error("密码错误");
	}
	
	@RequestMapping("logout")
	@ResponseBody
	public ResultMessage logout(HttpServletRequest request,  HttpServletResponse response){
		HttpSession session = request.getSession();
		session.invalidate();
		try {
			response.sendRedirect(request.getContextPath()+"/login.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultMessage.success();
	}

}
