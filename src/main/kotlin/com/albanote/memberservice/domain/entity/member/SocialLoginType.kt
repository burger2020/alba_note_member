package com.albanote.memberservice.domain.entity.member

import java.io.Serializable

enum class SocialLoginType : Serializable {
    NAVER, KAKAO, GOOGLE, APPLE, //NAVER, FIREBASE_PHONE
    TEST
}