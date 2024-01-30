package com.a205.brushbuddy.palette.controller;

import com.a205.brushbuddy.palette.domain.Palette;
import com.a205.brushbuddy.palette.dto.PaletteDetailResponseDto;
import com.a205.brushbuddy.palette.dto.PaletteListRequestDto;
import com.a205.brushbuddy.palette.dto.PaletteListResponseDto;
import com.a205.brushbuddy.palette.dto.PaletteModifyRequestDto;
import com.a205.brushbuddy.palette.service.PaletteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/api/palette")
@RequiredArgsConstructor
public class PaletteController {
    public final PaletteService paletteService;

    //TODO :팔레트 리스트 조회
    @GetMapping("")
    public ResponseEntity<?> getPaletteList(PaletteListRequestDto requestDto){
        //TODO : userId JWT 토큰으로 부터 추출 및 유효성 조사
        Integer userId = 1;

        //pageable 생성
        Pageable pageable = PageRequest.of(
                requestDto.getPageNum(),
                requestDto.getListNum());

        List<Palette> result =  paletteService.getPaletteList(userId, requestDto.getOrder(), pageable);

        PaletteListResponseDto dto = PaletteListResponseDto.builder()
                .palettes(result.stream().map(m ->PaletteListResponseDto.PaletteDTO.builder()
                                .paletteName(m.getPaletteName())
                                .paletteId(m.getPaletteId())
                                .paletteCreatedAt(m.getPaletteCreatedAt().toString())
                                .paletteModifiedTime(m.getPaletteLastModifiedTime().toString()) // 최종 수정 일자
                                .paletteColorCode(m.getPaletteColorCode())
                                .draftImage(m.getDraft().getDraftThumbnail())
                                .build())
                        .toList())
                .build();

        return  ResponseEntity.ok(dto);
    }

    //TODO : 팔레트 상세 조회
    @GetMapping("/{paletteId}")
    public ResponseEntity<?> getPaletteDetail(@PathVariable(value = "palettedId") Long paletteId){
        //TODO : userId JWT 토큰으로 부터 추출 및 유효성 조사
        Integer userId = 1;
        Palette result = paletteService.getPaletteDetail(userId, paletteId);

        PaletteDetailResponseDto dto = PaletteDetailResponseDto.builder()
                .paletteName(result.getPaletteName()) // 팔레트 이름
                .draftImage(result.getDraft().getDraftThumbnail()) // 워터마크 달린 사진
                .paletteColorCode(result.getPaletteColorCode()) // 팔레트 색깔 코드
                .paletteCreatedAt(result.getPaletteCreatedAt().toString()) // 팔레트 생성 일시
                .paletteModifiedTime(result.getPaletteLastModifiedTime().toString()) // 팔레트 수정 일시
                .build();
        return  ResponseEntity.ok(dto);
    }

    //TODO : 팔레트 수정 메소드
    @PutMapping("/{paletteId}")
    public ResponseEntity<?> modifyPalette(@PathVariable(value = "palettedId") Long paletteId, PaletteModifyRequestDto requestDto){
        //TODO : userId JWT 토큰으로 부터 추출 및 유효성 조사
        Integer userId = 1;
        paletteService.modifyPalette(userId, paletteId, requestDto);
        return  ResponseEntity.ok().build();
    }

    //TODO : 팔레트 복제
    @PostMapping("/{paletteId}/duplicate")
    public ResponseEntity<?> duplicatePalette(@PathVariable(value = "palettedId") Long paletteId){
        //TODO : userId JWT 토큰으로 부터 추출 및 유효성 조사
        Integer userId = 1;
        paletteService.duplicatePalette(userId, paletteId);
        return  ResponseEntity.ok().build();
    }

    // TODO : 팔레트 삭제
    @DeleteMapping("/{paletteId}")
    public ResponseEntity<?> getPaletteList(@PathVariable(value = "palettedId") Long paletteId){
        //TODO : userId JWT 토큰으로 부터 추출 및 유효성 조사
        Integer userId = 1;
        paletteService.deletePalette(userId,paletteId);
        return  ResponseEntity.ok().build();
    }
}
