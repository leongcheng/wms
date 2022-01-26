package com.gr.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gr.constant.Constants;
import com.gr.exception.ResultEnum;
import com.gr.jwt.JwtToken;
import com.gr.jwt.JwtUtil;
import com.gr.redis.RedisUtils;
import com.gr.sys.dao.SysUserDao;
import com.gr.sys.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 认证授权
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm
{
	@Lazy
	@Resource
	private RedisUtils redisUtils;
	@Resource
	private SysUserDao sysUserDao;

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof JwtToken;
	}

	/**
	 * 授权(验证权限时调用)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SysUserEntity user = (SysUserEntity) principals.getPrimaryPrincipal();
		Long userId = user.getUserId();

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		//系统管理员，拥有最高权限
		if (userId == Constants.SUPER_ADMIN) {
			info.addRole("ADMIN");
			info.addStringPermission("*:*:*");
		} else {
			List<String> permsList = sysUserDao.queryAllPerms(userId);
			//用户权限列表
			Set<String> permsSet = new HashSet<>();
			for (String perms : permsList) {
				if (StringUtils.isBlank(perms)) {
					continue;
				}
				permsSet.addAll(Arrays.asList(perms.trim().split(",")));
			}
			info.addRole(user.getRoleName());
			info.setStringPermissions(permsSet);
		}
		return info;
	}

	/**
	 * 认证(登录时调用)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		String token = (String) authcToken.getCredentials();
		// 解密获得account，用于和数据库进行对比
		String account = JwtUtil.getUsername(token);
		if (StringUtils.isBlank(token)) {
			throw new AuthenticationException(ResultEnum.ERR006.getMessage());
		}
		//查询用户信息
		SysUserEntity user = sysUserDao.selectOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getAccount, account).or().eq(SysUserEntity::getPhone, account));

		if (null == user) { //账号不存在
			throw new AuthenticationException(ResultEnum.ERR007.getMessage());

		}else if (user.getStatus() == Constants.SUPER_ADMIN) { //账号锁定
			throw new AuthenticationException(ResultEnum.ERR008.getMessage());

		}else if (!redisUtils.hasKey(Constants.PREFIX_USER_TOKEN + token) || !jwtTokenRefresh(token, account, user)) {// 校验token是否超时失效 & 或者账号密码是否错误
			throw new AuthenticationException(ResultEnum.ERR006.getMessage());
		}

		return new SimpleAuthenticationInfo(user, token, getName());
	}

	/**
	 * JWTToken刷新生命周期 （实现： 用户在线操作不掉线功能）
	 *
	 * @param username
	 * @param user
	 * @return
	 */
	public boolean jwtTokenRefresh(String token, String username, SysUserEntity user) {
		String cacheToken = String.valueOf(redisUtils.get(Constants.PREFIX_USER_TOKEN + token));
		if (StringUtils.isNotEmpty(cacheToken)) {
			// 校验token有效性
			if (!JwtUtil.verify(cacheToken, username, user.getPassword())) {
				log.info("——————————"+username+"用户在线操作，更新token保证不掉线—————————jwtTokenRefresh——————— "+ token);
				String newToken = JwtUtil.sign(username, user.getPassword());
				// 设置超时时间
				redisUtils.set(Constants.PREFIX_USER_TOKEN + token, newToken);
				redisUtils.expire(Constants.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME *2 / 1000);

				//更新用户操作时间
				SysUserEntity sysUser = redisUtils.getCacheObject(Constants.PREFIX_ONLINE_USER + user.getUserId());
				redisUtils.set(Constants.PREFIX_ONLINE_USER + user.getUserId(), sysUser.setLastAccessTime(new Date()).setLoginStatus(Constants.SUPER_ADMIN));
				//redisUtils.expire(Constants.PREFIX_ONLINE_USER + user.getUserId(), JwtUtil.EXPIRE_TIME *2 / 1000);
			}
			return true;
		}
		return false;
	}
}
