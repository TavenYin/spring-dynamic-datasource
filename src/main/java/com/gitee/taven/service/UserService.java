package com.gitee.taven.service;

import com.gitee.taven.entity.User;

public interface UserService {

	User get(String targetSource);

	User getByTransactional(String targetSource);
}
