package com.workintech.s19d2.service;

import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AuthenticationService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(MemberRepository memberRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String password){

        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if(memberOptional.isPresent()){
            throw new RuntimeException("User with given email already exist");
        }

        String encodedPassword = passwordEncoder.encode(password);

        Role adminRole = roleRepository.findByAuthority("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        List<Role> roles = new ArrayList<>();
        roles.add(adminRole);

        Member member = new Member();
        member.setPassword(encodedPassword);
        member.setEmail(email);
        member.setRoles(roles);

        return memberRepository.save(member);
    }


    private void addRoleAdmin(List<Role> roles) {
        Role adminRole = roleRepository.findByAuthority("ADMIN")
                .orElseGet(() -> {
                    Role newAdmin = new Role();
                    newAdmin.setAuthority("ADMIN");
                    return roleRepository.save(newAdmin);
                });

        roles.add(adminRole);
    }
}