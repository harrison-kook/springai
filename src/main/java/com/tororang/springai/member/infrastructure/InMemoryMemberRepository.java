package com.tororang.springai.member.infrastructure;

import com.tororang.springai.member.domain.Member;
import com.tororang.springai.member.domain.MemberRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<UUID, Member> store = new ConcurrentHashMap<>();

    @Override
    public Member save(Member member) {
        store.put(member.id(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return store.values().stream()
                .filter(member -> member.email().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
