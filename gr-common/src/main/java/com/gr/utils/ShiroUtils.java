package com.gr.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * Shiro工具类
 *
 * @date 2016年11月12日 上午9:49:19
 */
public class ShiroUtils
{
	/**  加密算法 */
	public final static String hashAlgorithmName = "SHA-256";
	/**  循环次数 */
	public final static int hashIterations = 16;

	public static String sha256(String password, String salt) {
		return new SimpleHash(hashAlgorithmName, password, salt, hashIterations).toString();
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static Session getSession() { return SecurityUtils.getSubject().getSession(); }

	public static void logout() {
		SecurityUtils.getSubject().logout();
	}

}
