package org.acme.shop.adapter.out.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.acme.shop.adapter.out.persistence.entity.MemberEntity;
import org.acme.shop.domain.Member;

import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class MemberMapper implements DomainEntityMapper<Member, MemberEntity>{

    public List<MemberEntity> domainToEntity(List<Member> members) {
        List<MemberEntity> memberEntities = new ArrayList<MemberEntity>();
        for(Member m : members){
            memberEntities.add(domainToEntity(m));
        }
        return memberEntities;
    }
    
    public MemberEntity domainToEntity(Member member) {
        if (member == null ){
            return null;
        }
        return MemberEntity.builder()
                            .id(member.getId())
                            .address(member.getAddress())
                            .phoneNumber(member.getPhoneNumber())
                            .build();
                            
    }

    public List<Member> entityToDomain(List<MemberEntity> memberEntitis) {
        List<Member> members = new ArrayList<Member>();
        for(MemberEntity me : memberEntitis) {
            members.add(entityToDomain(me));
        }
        return members;                   
    }

    public Member entityToDomain(MemberEntity memberEntity) {
        if (memberEntity == null ){
            return null;
        }
        return Member.builder()
                    .id(memberEntity.getId())
                    .address(memberEntity.getAddress())
                    .phoneNumber(memberEntity.getPhoneNumber())
                    .build();
                            
    }
}
