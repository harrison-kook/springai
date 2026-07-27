package com.tororang.springai.member.infrastructure;

import com.tororang.springai.member.application.FindMemberUseCase;
import com.tororang.springai.member.application.RegisterMemberUseCase;
import com.tororang.springai.member.domain.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberConfig {

    @Bean
    public MemberRepository memberRepository() {
        return new InMemoryMemberRepository();
    }

    @Bean
    public RegisterMemberUseCase registerMemberUseCase(MemberRepository memberRepository) {
        return new RegisterMemberUseCase(memberRepository);
    }

    @Bean
    public FindMemberUseCase findMemberUseCase(MemberRepository memberRepository) {
        return new FindMemberUseCase(memberRepository);
    }
}
