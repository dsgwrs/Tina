package com.example.tina.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tina.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
