package com.albanote.memberservice.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class RedisMemberService(
    var redisTemplate: RedisTemplate<String, String>,
) {
    private val MEMBER_REFRESH_TOKEN: (memberSocialId: String) -> String = { "MEMBER_REFRESH_TOKEN_$it" }

    /**
     * 멤버 refreshToken 저장 및 만료 기간 설정
     *
     * @author burger
     * */
    fun setMemberRefreshToken(uuid: String, refreshToken: String, expiredDate: Date) {
        redisTemplate.opsForValue().set(MEMBER_REFRESH_TOKEN(uuid), refreshToken)
        redisTemplate.opsForValue().operations.expireAt(MEMBER_REFRESH_TOKEN(uuid), expiredDate)
    }


    /**
     * 멤버 refreshToken 유효값 확인
     *
     * @author burger
     * */
    fun isValidMemberRefreshToken(uuid: String, refreshToken: String): Boolean {
        return redisTemplate.opsForValue().get(MEMBER_REFRESH_TOKEN(uuid)) == refreshToken
    }
}