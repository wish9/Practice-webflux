package com.codestates.member.service;

import com.codestates.exception.BusinessLogicException;
import com.codestates.exception.ExceptionCode;
import com.codestates.member.entity.Member;
import com.codestates.member.repository.MemberRepository;
import com.codestates.stamp.Stamp;
import com.codestates.utils.CustomBeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;


@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;  // Spring Data R2DBC에서 DB에 접근하는 첫번째 방법
    private final CustomBeanUtils<Member> beanUtils; // request body에 포함된 정보만 테이블에 업데이트 되도록 해주는 유틸리티 클래스
    private final R2dbcEntityTemplate template;       // DB에 접근하는 2번째 방법 // Spring Data R2DBC에서 지원하는 가독성 좋은 SQL 쿼리 빌드 메서드를 이용하는 방식
    public MemberService(MemberRepository memberRepository, CustomBeanUtils<Member> beanUtils, R2dbcEntityTemplate template) {
        this.memberRepository = memberRepository;
        this.beanUtils = beanUtils;
        this.template = template;
    }

    public Mono<Member> createMember(Member member) {
        return verifyExistEmail(member.getEmail())      // 이미 존재하는 이메일인지 검증
                .then(memberRepository.save(member))    // then() Operator = 이전에 동작하고 있던 Sequence를 종료하고 새로운 Sequence를 시작하게 해주는 Operator
                .map(resultMember -> {
                    // Stamp 저장
                    template.insert(new Stamp(resultMember.getMemberId())).subscribe();  // 스탬프 정보를 테이블에 저장 // Inner Sequence이기 때문에 subscribe() 호출

                    return resultMember;
                });

    }

    public Mono<Member> updateMember(Member member) {
        return findVerifiedMember(member.getMemberId())    // 존재하는 member인지 확인
                .map(findMember -> beanUtils.copyNonNullProperties(member, findMember)) // 데이터 바꾸기  // findMember = 기존에 있던 데이터 // request body에 포함된 정보만 테이블에 업데이트
                .flatMap(updatingMember -> memberRepository.save(updatingMember));    // 바꾼 데이터 테이블에 저장
    }

    @Transactional(readOnly = true)
    public Mono<Member> findMember(long memberId) {
        return findVerifiedMember(memberId);
    }

    @Transactional(readOnly = true)
    public Mono<Page<Member>> findMembers(PageRequest pageRequest) {
        return memberRepository.findAllBy(pageRequest)
                .collectList()     // List 객체로 변환
                .zipWith(memberRepository.count())   // List와  전체 건 수(Long) 묶기
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageRequest, tuple.getT2())); //  PageImpl 객체 생성
    }

    public Mono<Void> deleteMember(long memberId) {
        return findVerifiedMember(memberId)
                .flatMap(member -> template.delete(query(where("MEMBER_ID").is(memberId)), Stamp.class))  // 스템프 정보 삭제
                .then(memberRepository.deleteById(memberId));              // 회원 정보 삭제
        // MEMBER 테이블과 STAMP 테이블은 외래키로 관계를 맺고 있기 때문에 MEMBER 테이블의 식별자를 외래키로 가지는 STAMP 테이블의 스탬프 정보를 먼저 삭제해 주어야 한다.
    }

    private Mono<Void> verifyExistEmail(String email) {
        return memberRepository.findByEmail(email)
                .flatMap(findMember -> {
                    if (findMember != null) { // 이미 존재하는 이메일이면
                        return Mono.error(new BusinessLogicException(ExceptionCode.MEMBER_EXISTS)); // error() Operator로 Exception을 throw
                    }
                    return Mono.empty();    //  Spring WebFlux의 경우 Mono 안에서 모든 처리가 이루어져야 하므로, Mono.empty()를 리턴해 주어야 다음 동작을 진행할 수 있다.
                });
    }

    private Mono<Member> findVerifiedMember(long memberId) {
        return memberRepository
                .findById(memberId)
                .switchIfEmpty(Mono.error(new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND)));
        // switchIfEmpty() Operator는 emit되는 데이터가 없다면 switchIfEmpty() Operator의 파라미터로 전달되는 Publisher가 대체 동작을 수행할 수 있게 해주는 Operator다.
    }
}
