package com.ruoyi.framework.shiro.service;

import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.framework.util.MessageUtils;
import com.ruoyi.framework.web.exception.user.UserPasswordNotMatchException;
import com.ruoyi.framework.web.exception.user.UserPasswordRetryLimitExceedException;
import com.ruoyi.system.domain.SysUser;

/**
 * 登录密码方法
 * 
 * @author ruoyi
 */
@Component
public class SysPasswordService
{
    @Value(value = "${user.password.maxRetryCount}")
    private String maxRetryCount;
 
    public void validate(SysUser user, String password)
    {
        String loginName = user.getLoginName();
//
//        AtomicInteger retryCount = loginRecordCache.get(loginName);
//
//        if (retryCount == null)
//        {
//            retryCount = new AtomicInteger(0);
//            loginRecordCache.put(loginName, retryCount);
//        }
//        if (retryCount.incrementAndGet() > Integer.valueOf(maxRetryCount).intValue())
//        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount)));
//            throw new UserPasswordRetryLimitExceedException(Integer.valueOf(maxRetryCount).intValue());
//        }

        if (!matches(user, password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.count", 1)));
//            loginRecordCache.put(loginName, retryCount);
            throw new UserPasswordNotMatchException();
        }
        else
        {
//            clearLoginRecordCache(loginName);
        }
    }

    public boolean matches(SysUser user, String newPassword)
    {
        return user.getPassword().equals(encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
    }

    public void clearLoginRecordCache(String username)
    {
//        loginRecordCache.remove(username);
    }

    public String encryptPassword(String username, String password, String salt)
    {
        return new Md5Hash(username + password + salt).toHex().toString();
    }

}
