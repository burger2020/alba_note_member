package com.albanote.memberservice.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class TermsController {

    @GetMapping("/kakaoAddressWeb")
    fun getKakaoAddressWeb(): String {
        print("kakaoAddress")
        return "kakaoAddress"
    }
}