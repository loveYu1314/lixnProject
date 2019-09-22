package com.lixn.login.service;

import com.lixn.login.POJO.UserBO;
import com.lixn.login.util.JWTUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/22
 * @描述
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public String buildUserInfo(UserBO userBO) {
        String username = userBO.getUsername();
        String jwt = JWTUtil.sign(username,JWTUtil.SECRET);
        Assert.notNull(jwt,"jwt cannot null!");
        RBucket rBucket = redissonClient.getBucket(jwt);
        rBucket.set(userBO,JWTUtil.EXPIRE_TIME_MS, TimeUnit.MILLISECONDS);
        return jwt;
    }

    @Override
    public void logout(String jwt) {
        RBucket rBucket = redissonClient.getBucket(jwt);
        rBucket.delete();
    }
}
