package com.lixn.login.filter;

import com.lixn.login.POJO.CurrentUser;
import com.lixn.login.POJO.UserBO;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.redisson.api.RBucket;
import org.redisson.api.RDeque;
import org.redisson.api.RLock;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/19
 * @描述 队列踢出
 */
public class QueueKickOutFilter extends KickOutFilter {

    // 踢出之前登录的/之后登录的用户，默认踢出之前登录的用户
    private boolean kickOutAfter = false;

    // 同一个账号最大会话数 默认1
    private int maxSession = 1;

    public void setKickOutAfter(boolean kickOutAfter) {
        this.kickOutAfter = kickOutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String token = request.getHeader("Authorization");
        UserBO currentSession = CurrentUser.get();

        Assert.notNull(currentSession, "currentSession cannot null");
        String username = currentSession.getUsername();
        String userKey = PREFIX + "deque_" + username;
        String lockKey = PREFIX_LOCK + username;

        RLock lock = redissonClient.getLock(lockKey);

        lock.lock(2, TimeUnit.SECONDS);

        try {
            RDeque<String> deque = redissonClient.getDeque(userKey);

            // 如果队列里没有此token，且用户没有被踢出；放入队列
            if (!deque.contains(token) && currentSession.isKickout() == false) {
                deque.push(token);
            }

            // 如果队列里的sessionId数超出最大会话数，开始踢人
            while (deque.size() > maxSession) {
                String kickOutSessionId;
                if (kickOutAfter) { // 如果踢出后者
                    kickOutSessionId = deque.removeFirst();
                } else { // 否则踢出前者
                    kickOutSessionId = deque.removeLast();
                }

                try {
                    RBucket<UserBO> bucket = redissonClient.getBucket(kickOutSessionId);
                    UserBO kickOutSession = bucket.get();

                    if (kickOutSession != null) {
                        // 设置会话的kickOut属性表示踢出了
                        kickOutSession.setKickout(true);
                        bucket.set(kickOutSession);
                    }

                } catch (Exception e) {
                }

            }

            // 如果被踢出了，直接退出，重定向到踢出后的地址
            if (currentSession.isKickout()) {
                // 会话被踢出了
                try {
                    // 注销
                    userService.logout(token);
                    sendJsonResponse(response, 4001, "您的账号已在其他设备登录");

                } catch (Exception e) {
                }

                return false;

            }

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                LOGGER.info(Thread.currentThread().getName() + " unlock");

            } else {
                LOGGER.info(Thread.currentThread().getName() + " already automatically release lock");
            }
        }

        return true;
    }
}
