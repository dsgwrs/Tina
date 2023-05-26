package com.example.tina.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tina.common.Result;
import com.example.tina.pojo.Employee;
import com.example.tina.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //将password使用md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据提供的用户名在数据库中查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //查询是否成功
        if (emp == null) {
            return Result.error("登陆失败!");
        }
        //密码比对
        if (!emp.getPassword().equals(password)) {
            return Result.error("登录失败！");
        }
        //查看员工状态
        if (emp.getStatus() == 0) {
            return Result.error("账号已禁用！");
        }
        //登陆成功，将员工id存入session并返回结果
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }


    /**
     * 员工退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        //清楚session中的信息
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功！");
    }


    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        //设置初始密码，使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //获取当前系统时间
        employee.setCreateTime(LocalDateTime.now());
        //更新当前时间
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return Result.success("新增员工成功！");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //分页查询
        employeeService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> update(HttpServletRequest request ,@RequestBody Employee employee) {
        log.info(employee.toString());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功！");
    }
}
