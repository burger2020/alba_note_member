package com.albanote.memberservice.security

import java.io.Serializable

enum class SocialLoginType: Serializable {
    KAKAO, GOOGLE, APPLE, //NAVER, FIREBASE_PHONE
    TEST
}