package com.codestates.member.controller;

import com.codestates.member.dto.MemberDto;
import com.codestates.member.mapper.MemberMapper;
import com.codestates.member.service.MemberService;
import com.codestates.utils.UriCreator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/v12/members")
public class MemberController {
    private static final String MEMBER_DEFAULT_URL = "/v12/members";
    private final MemberService memberService;
    private final MemberMapper mapper;

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public Mono<ResponseEntity> postMember(@Valid @RequestBody Mono<MemberDto.Post> requestBody) {  // MemberDto.Post 객체를 Mono로 래핑
        return requestBody
                .flatMap(post -> memberService.createMember(mapper.memberPostToMember(post)))  // Non-Blocking - Operator 체인 사용
                .map(createdMember -> {
                    URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createdMember.getMemberId());
                    return ResponseEntity.created(location).build();
                });
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@PathVariable("member-id") @Positive long memberId,
                                      @Valid @RequestBody Mono<MemberDto.Patch> requestBody) {
        Mono<MemberDto.Response> response =
                requestBody
                        .flatMap(patch -> {
                            patch.setMemberId(memberId);
                            return memberService.updateMember(mapper.memberPatchToMember(patch)); // 회원정보 저장
                        })
                        .map(member -> mapper.memberToMemberResponse(member));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") @Positive long memberId) {
        Mono<MemberDto.Response> response =
                memberService.findMember(memberId)   // 회원정보 조회
                        .map(member -> mapper.memberToMemberResponse(member));
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMembers(@RequestParam("page") @Positive int page,
                                     @RequestParam("size") @Positive int size) {
        Mono<List<MemberDto.Response>> response =
                memberService.findMembers(PageRequest.of(page - 1, size, Sort.by("memberId").descending()))  // PageRequest 객체 만들어서 전달
                        .map(pageMember -> mapper.membersToMemberResponses(pageMember.getContent()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") long memberId) {
        Mono<Void> result = memberService.deleteMember(memberId);    // 회원 삭제
        return new ResponseEntity(result, HttpStatus.NO_CONTENT);
    }
}
