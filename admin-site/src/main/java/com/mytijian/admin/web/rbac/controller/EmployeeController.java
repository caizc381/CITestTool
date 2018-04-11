package com.mytijian.admin.web.rbac.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mytijian.admin.api.rbac.constant.EmployeeStatusEnum;
import com.mytijian.admin.api.rbac.model.Employee;
import com.mytijian.admin.api.rbac.service.EmployeeService;
import com.mytijian.admin.dao.base.dataobject.PageUtils;
import com.mytijian.admin.web.exception.CommonConstants;
import com.mytijian.admin.web.util.CommonUtil;
import com.mytijian.admin.web.util.Page;
import com.mytijian.admin.web.util.ShiroUtils;
import com.mytijian.cache.RedisCacheClient;
import com.mytijian.cache.annotation.RedisClient;
import com.mytijian.mediator.department.constans.DepartmentExceptionCode;
import com.mytijian.mediator.exceptions.ExceptionFactory;
import com.mytijian.mediator.exceptions.ServiceException;
import com.mytijian.mediator.user.constans.UserExceptionCode;

/**
 * 
 * 类EmployeeController.java的实现描述：用户相关
 * @author zhanfei.feng 2017年4月7日 下午4:04:21
 */
@RestController
@RequestMapping("/user")
public class EmployeeController {

	private final static Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	@Resource(name = "employeeService")
	private EmployeeService employeeService;
	
	@Value("${ops_departmentId}")
	private Integer opsDeparmentId;

	@RedisClient(nameSpace = CommonConstants.REDISEMPLOYEE, timeout = 60 * 60 * 12)
	private RedisCacheClient<Employee> employeeCacheService;

	@RequestMapping(value = "/login", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseStatus(value = HttpStatus.OK)
	public Employee login(@RequestParam(value = "loginName") String loginName,
			@RequestParam(value = "password") String password, HttpServletResponse response, HttpServletRequest request,
			HttpSession session) throws ServiceException {

		// 解析参数
		checkLoginNameAndPwd(loginName, password);

		// 获取用户信息
		Employee employee = employeeService.getEmployeeInfo(null, null, loginName);

		// 用户是否合法
		checkEmployee(employee, loginName, password);

		String token = accountLogin(employee);
		session.setAttribute(CommonConstants.EMPLOYEE_TOKEN, token);
		// 添加防两次提交token
		// LoginUtil.addUniqueSubmitToken(response, request);
		getEmployeeWithoutSaltAndPwd(employee);

		UsernamePasswordToken shiroToken = new UsernamePasswordToken(loginName, password);
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.login(shiroToken);
		Cookie cookie = new Cookie("token", token);
		cookie.setPath("/");
		response.addCookie(cookie);

		session.setAttribute("employee", employee);
		return employee;
	}

	/**
	 * 根据用户名获取用户基本信息
	 * 
	 * @param loginName
	 * @return
	 */
	@RequestMapping(value = "/getEmployeeByLoginName")
	@RequiresPermissions("sys:user:info")
	public Employee getEmployeeByLoginName(@RequestParam(value = "loginName") String loginName) {
		Employee employee = employeeService.getEmployeeInfo(null, null, loginName);
		getEmployeeWithoutSaltAndPwd(employee);
		return employee;
	}

	/**
	 * 根据用户Id获取用户基本信息
	 * 
	 * @param loginName
	 * @return
	 */
	@RequestMapping(value = "/info/{employeeId}")
	@RequiresPermissions("sys:user:info")
	public Employee info(@PathVariable("employeeId") Integer employeeId) {
		Employee employee = employeeService.getEmployeeInfo(employeeId, null, null);
		getEmployeeWithoutSaltAndPwd(employee);
		return employee;
	}

	/**
	 * 获取用户基本信息
	 * 
	 * @param loginName
	 * @return
	 */
	@RequestMapping(value = "/getEmployeeInfo")
	public Employee getEmployeeInfo() {
		String loginName = ShiroUtils.getEmployeeEntity().getLoginName();
		Employee employee = employeeService.getEmployeeInfo(null, null, loginName);
		getEmployeeWithoutSaltAndPwd(employee);
		return employee;
	}

	@RequestMapping(value = "/logout")
	public boolean logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		/*
		try {
			String token = (String) session.getAttribute(CommonConstants.EMPLOYEE_TOKEN);
			if (token != null) {
				employeeCacheService.remove(token);
			}
			// session.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LoginUtil.removeCookie(request, response);
		 */
		// 退出
		ShiroUtils.logout();

		return true;
	}

	/**
	 * 所有用户列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:user:list")
	public PageUtils list(String employeeName, Integer currPage, Integer pageSize) {
		// 查询列表数据
		List<Employee> employeeList = new ArrayList<>();
		Page page = CommonUtil.getPage(currPage, pageSize);
		int offset = page.getOffset();
		pageSize = page.getPageSize();
		currPage = page.getCurrPage();

		int total = employeeService.countTotalEmployees(employeeName, offset, pageSize);

		if (total >= 0) {
			employeeList = employeeService.listEmployees(employeeName, offset, pageSize);
			getEmployeeWithoutSaltAndPwd(employeeList);
		}

		return new PageUtils(employeeList, total, pageSize, currPage);
	}

	/**
	 * 保存用户
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@RequiresPermissions("sys:user:save")
	public boolean save(@RequestBody Employee employee) {
		// Employee e = JSON.parseObject(employee, Employee.class);
		if (StringUtils.isEmpty(employee.getLoginName())) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + "(EmployeeController.save), loginName is null");
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_LOGINNAME_EMPTY, new Object[] { null });
		}

		// 查看登录名是否已经存在
		checkLoginName(employee.getLoginName());

		if (employee.getDepartmentId() == null) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + "(EmployeeController.save), departmentId is null");
			throw ExceptionFactory.makeFault(DepartmentExceptionCode.DEPARTMENT_ID_EMPTY, new Object[] { null });
		}
		getEmployBasicInfo(employee);
		employeeService.addEmployee(employee);

		return true;
	}

	/**
	 * 修改用户
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:user:update")
	public boolean update(@RequestBody Employee employee) {
		if (StringUtils.isEmpty(employee.getLoginName())) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + "(EmployeeController.update), loginName is null");
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_LOGINNAME_EMPTY, new Object[] { null });
		}

		// 获取用户信息
		Employee emp = employeeService.getEmployeeInfo(null, null, employee.getLoginName());
		if (emp == null) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + "(EmployeeController.update), loginName : {}",
					employee.getLoginName());
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_NOT_EXIST, new Object[] { null });
		}

		if (!StringUtils.isEmpty(employee.getPassword())) {
			employee.setSalt(emp.getSalt());
			employee.setPassword(CommonUtil.MD5(employee.getSalt() + employee.getPassword()));
		}
		// 如果不存在则新增
		employeeService.updateEmployee(employee);

		return true;
	}

	/**
	 * 更新用户密码
	 * 
	 * @param password 用户密码
	 * @return
	 */
	@RequestMapping(value = "/updatePwd")
	public boolean updatePwd(@RequestParam(value = "password") String password, 
				@RequestParam(value = "passwordNew") String passwordNew) {

		Integer employeeId = ShiroUtils.getUserId();

		// 检测用户密码
		if (StringUtils.isEmpty(password) || password.trim().length() == 0) {
			logger.error(UserExceptionCode.USER_PASSWORD_EMPTY
					+ "(EmployeeController.updatePwd), employeeId : {} ,password : {}", employeeId, password);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_EMPTY, new Object[] { null });
		}
		
		// 检测用户密码
		if (!CommonUtil.isStrFitLength(passwordNew, 6, 20)) {
			logger.error(UserExceptionCode.USER_PASSWORD_ILLEAGLE
					+ "(EmployeeController.updatePwd), employeeId : {} ,passwordNew : {}", employeeId, passwordNew);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_ILLEAGLE, new Object[] { null });
		}
		
		if (password.equals(passwordNew)) {
			logger.error(UserExceptionCode.USER_PASSWORD_OLD_ADN_NEW_SAME
					+ "(EmployeeController.updatePwd), employeeId : {} , password : {}, passwordNew : {} ", employeeId, password, passwordNew);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_OLD_ADN_NEW_SAME, new Object[] { null });
		}
		
		
		// 查询用户信息
		Employee employee = employeeService.getEmployeeInfo(employeeId, null, null);
		// 查询
		if (employee == null) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + "(EmployeeController.updatePwd), employeeId : {}",
					employeeId);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_NOT_EXIST, new Object[] { null });
		}
		
		// 检测用户密码
		if (!checkUserPwd(password, employee.getPassword(), employee.getSalt())) {
			logger.error(UserExceptionCode.USER_PASSWORD_INCORRECT + ", password : {}", password);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_INCORRECT, new Object[] { null });
		}
		
		String salt = employee.getSalt();
		passwordNew = CommonUtil.MD5(salt + passwordNew);

		employeeService.updateEmployeePwd(employeeId, passwordNew);
		return true;
	}

	/**
	 * 删除用户
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:user:delete")
	public boolean delete(@RequestParam(value = "employeeIds") List<Integer> employeeIds) {
		if (CollectionUtils.isEmpty(employeeIds)) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + "(EmployeeController.delete), employeeIds : {}",
					employeeIds);
			throw ExceptionFactory.makeFault(UserExceptionCode.CURRENT_CAN_NOT_DEL, new Object[] { null });
		}

		if (employeeIds.contains(1)) {
			throw ExceptionFactory.makeFault(UserExceptionCode.SYS_ADMIN_CAN_NOT_DEL, new Object[] { null });
		}

		if (employeeIds.contains(ShiroUtils.getUserId())) {
			throw ExceptionFactory.makeFault(UserExceptionCode.CURRENT_CAN_NOT_DEL, new Object[] { null });
		}

		employeeService.deleteBatch(employeeIds);

		return true;
	}

	/**
	 *  查看用户是否具有某权限
	 * @param permission
	 * @return
	 */
	@RequestMapping("/hasPermission")
	public boolean hasPermission(@RequestParam(value = "permission") String permission) {
		Subject subject = SecurityUtils.getSubject();
		return subject != null && subject.isPermitted(permission);
	}

	@RequestMapping("/getUserPermissions")
	public List<String> getUserPermissions() {
		return employeeService.listPermsByemployeeId(ShiroUtils.getUserId());
	}
	
	/**
	 * 查找运营经理信息
	 * @return
	 */
	@RequestMapping("/getOperationInfo")
	public List<Employee> getOperationInfo(){
		return employeeService.getOperationInfoByDep(opsDeparmentId);
	}
	
	/**
	 * 初始员工拼音信息
	 */
	@RequestMapping("/initEmployeePinYin")
	public void initEmployeePinYin(){
		employeeService.initEmployeePinYin();
	}
	
	private boolean checkUserPwd(String pwd, String realPwd, String salt) {
		if (!StringUtils.isEmpty(salt)) {
			pwd = salt + pwd;
		}
		return realPwd.equals(CommonUtil.MD5(pwd));
	}

	private String accountLogin(Employee employee) {
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		employeeCacheService.put(token, employee);		
		return token;
	}

	/**
	 * 检测用户名和密码
	 * 
	 * @param loginName
	 * @param password
	 * @throws ServiceException
	 */
	private void checkLoginNameAndPwd(String loginName, String password) throws ServiceException {
		// 检测登录名
		if (StringUtils.isEmpty(loginName) || loginName.trim().length() == 0) {
			logger.error(UserExceptionCode.USER_LOGINNAME_EMPTY + ", loginName : {}", loginName);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_LOGINNAME_EMPTY, new Object[] { null });

		}

		// 检测用户密码
		if (StringUtils.isEmpty(password) || password.trim().length() == 0) {
			logger.error(UserExceptionCode.USER_PASSWORD_EMPTY + ", password : {}", password);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_EMPTY, new Object[] { null });
		}
	}

	/**
	 * 查看用户是否合法
	 * 
	 * @param employee
	 *            用户信息
	 * @param password
	 *            用户密码
	 * @param loginName
	 *            登录名称
	 */
	private void checkEmployee(Employee employee, String loginName, String password) {

		// 检测用户是否存在
		if (StringUtils.isEmpty(employee)) {
			logger.error(UserExceptionCode.USER_NOT_EXIST + ", loginName : {}", loginName);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_NOT_EXIST, new Object[] { null });
		}

		// 检测用户状态
		if (employee.getStatus() != EmployeeStatusEnum.NORMAL.getCode()) {
			logger.error(UserExceptionCode.USER_FROZED + ", loginName : {}", loginName);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_FROZED, new Object[] { null });
		}

		// 检测用户密码
		if (!checkUserPwd(password, employee.getPassword(), employee.getSalt())) {
			logger.error(UserExceptionCode.USER_PASSWORD_INCORRECT + ", password : {}", password);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_PASSWORD_INCORRECT, new Object[] { null });
		}
	}

	private void getEmployeeWithoutSaltAndPwd(Employee employee) {
		if (employee != null) {
			employee.setSalt(null);
			employee.setPassword(null);
		}
	}

	private void getEmployeeWithoutSaltAndPwd(List<Employee> employees) {
		if (!CollectionUtils.isEmpty(employees)) {
			employees.forEach(employee -> {
				employee.setSalt(null);
				employee.setPassword(null);
			});
		}
	}

	private void getEmployBasicInfo(Employee employee) {
		if (StringUtils.isEmpty(employee.getPassword())) {
			// 默认123456
			employee.setPassword(CommonUtil.DEFAULT_PWD);
		}
		String salt = CommonUtil.getSalt();
		employee.setSalt(salt);
		employee.setPassword(CommonUtil.MD5(salt + employee.getPassword()));
	}

	private void checkLoginName(String loginName) {
		// 获取用户信息
		Employee employee = employeeService.getEmployeeInfo(null, null, loginName);
		if (employee != null) {
			logger.error(UserExceptionCode.USER_LOGINNAME_ALREADY_REGISTED + ", loginName : {}", loginName);
			throw ExceptionFactory.makeFault(UserExceptionCode.USER_LOGINNAME_ALREADY_REGISTED, new Object[] { null });
		}
	}
	

	public static void main(String args[]) {
		// System.out.println(CommonUtil.MD5("111111111a"));
		Integer currPage = 0;
		currPage = Optional.ofNullable(currPage).orElse(CommonUtil.DEFAULT_CURRPAGE);
		System.out.println(currPage);
	}
}
