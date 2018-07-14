package name.zhangmin.boot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import name.zhangmin.boot.dao.UserDao;
import name.zhangmin.boot.model.User;
import name.zhangmin.boot.service.UserService;

/**
 * @author hzzhangmin15
 * @date 2018/07/05
 */

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public int addUser(User user) {
        return userDao.insertSelective(user);
    }

	@Override
	public User getUserById(Integer userId) {
		return userDao.selectByPrimaryKey(userId);
	}
}
