package com.gitee.taven.service.impl;

import org.springframework.stereotype.Service;

import com.gitee.taven.entity.User;
import com.gitee.taven.mapper.UserMapper;
import com.gitee.taven.service.UserService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;
	
	@Override
	public User get(String targetSource) {
		return userMapper.selectByPrimaryKey(1);
	}

	@Override
	@Transactional
	public User getByTransactional(String targetSource) {
		return userMapper.selectByPrimaryKey(1);
	}
}
