package com.tororang.springai.member.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberPageController {

    @GetMapping("/members")
    public String memberPage() {
        return "members";
    }
}
