package com.gitee.taven.controller;

import java.util.HashMap;
import java.util.Map;

import com.gitee.taven.config.DynamicDataSourceContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.gitee.taven.config.DynamicRoutingDataSource;
import com.gitee.taven.entity.User;
import com.gitee.taven.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired private UserService userService;
	
	@Autowired private Environment env;
    
    @Autowired private DynamicRoutingDataSource dynamicDataSource;
	
	/**
	 * 添加数据源示例
	 * 
	 * @return
	 */
	@GetMapping("/add_data_source")
	public Object addDataSource() {
		// 构建 DataSource 属性,
		Map<String, String> map = new HashMap<>();
		map.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, 
				env.getRequiredProperty("datasource.dbx.driverClassName"));
		map.put(DruidDataSourceFactory.PROP_URL, 
				env.getRequiredProperty("datasource.dbx.url").replace("{0}", "dynamic_db2"));
		map.put(DruidDataSourceFactory.PROP_USERNAME, 
				env.getRequiredProperty("datasource.dbx.username"));
		map.put(DruidDataSourceFactory.PROP_PASSWORD, 
				env.getRequiredProperty("datasource.dbx.password"));
		map.put("database", "dynamic_db2");
		return dynamicDataSource.addDataSource(map);
	}
	
	/**
	 * 获取全部数据源的结果
	 * 
	 * @return
	 */
	@GetMapping("/getAll")
	public Object getAll() {
		Map<String, Object> map = new HashMap<>();
		User u0 = userService.get("dynamic_db0");
		User u1 = userService.get("dynamic_db1");
		User u2 = userService.get("dynamic_db2");
		map.put("u0", u0);
		map.put("u1", u1);
		map.put("u2", u2);
		return map;
	}

	/**
	 * 无事务的情况，根据参数切换数据源
	 *
	 * @param ds
	 * @return
	 */
	@GetMapping("/get")
	public Object get(@RequestParam(defaultValue = "dynamic_db0") String ds) {
		return userService.get(ds);
	}

	/**
	 * 有事务的情况下，根据参数切换数据源
	 *
	 * @param ds
	 * @return
	 */
	@GetMapping("/getByTransactional")
	public Object getByTransactional(@RequestParam(defaultValue = "dynamic_db0") String ds) {
		try {
			// 默认情况下，事务AOP是要先于 DynamicDataSourceAspect
			// 事务AOP 开启事务，需要向数据库发送 START TRANSACTION，在这之前肯定要通过 DataSource#getConnection 获取连接
			// 在获取完连接之后，会将其存储到上下文中
			// 执行完上述步骤之后，再通过 DynamicDataSourceAspect 指定数据源就没有用了，因为当前线程不会再去调用 DataSource#getConnection 了

			// 所以解决这个问题的原理就清晰了，只需要在开启事务之前指定数据源即可
			// 方法1：手动指定
			// 方法2：改变切面的执行顺序
			DynamicDataSourceContextHolder.setDataSourceKey(ds);
			return userService.getByTransactional(ds);
		} finally {
			DynamicDataSourceContextHolder.clearDataSourceKey();
		}
	}

}
