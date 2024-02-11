package com.a205.brushbuddy.draft.service;

import com.a205.brushbuddy.draft.domain.Category;
import com.a205.brushbuddy.draft.domain.Draft;
import com.a205.brushbuddy.draft.dto.request.DraftCategoryModifyRequestDto;
import com.a205.brushbuddy.draft.dto.request.DraftCreateRequestDto;
import com.a205.brushbuddy.draft.dto.response.DraftCreateResponseDto;
import com.a205.brushbuddy.draft.dto.response.DraftDetailResponseDto;
import com.a205.brushbuddy.draft.dto.response.DraftListResponseDto;
import com.a205.brushbuddy.draft.repository.BookmarkRepository;
import com.a205.brushbuddy.draft.repository.CategoryRepository;
import com.a205.brushbuddy.draft.repository.Draft.DraftRepository;
import com.a205.brushbuddy.draft.repository.DraftCategory.DraftCategoryRepository;
import com.a205.brushbuddy.draft.repository.PurchaseRepository;
import com.a205.brushbuddy.exception.BaseException;
import com.a205.brushbuddy.exception.ErrorCode;
import com.a205.brushbuddy.palette.domain.Palette;
import com.a205.brushbuddy.palette.repository.PaletteRepository;
import com.a205.brushbuddy.user.domain.User;
import com.a205.brushbuddy.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService{

    private final DraftRepository draftRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PaletteRepository paletteRepository;
    private final DraftCategoryRepository draftCategoryRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PurchaseRepository purchaseRepository;

    public Page<DraftListResponseDto> getDraftList(Pageable pageable) {
        try {

            return draftRepository.findAll(pageable).map(p->DraftListResponseDto.builder()
                    .draftId(p.getDraftId())
                    .draftThumbnail(p.getDraftThumbnail())
                    .draftTimestamp(p.getDraftTimestamp())
                    .draftDownload(p.getDraftDownload())
                    .draftBookmark(p.getDraftBookmark())
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public Page<DraftListResponseDto> getDraftListByCategory(Pageable pageable, String categoryContent) {
        Category category = categoryRepository.findByCategoryContent(categoryContent);
        List<Long> categoryIds = draftCategoryRepository.findDraftIdByCategoryId(category.getCategoryId());
        try {
            return draftRepository.findAllByDraftIdIn(categoryIds,pageable).map(p->DraftListResponseDto.builder()
                    .draftId(p.getDraftId())
                    .draftThumbnail(p.getDraftThumbnail())
                    .draftTimestamp(p.getDraftTimestamp())
                    .draftDownload(p.getDraftDownload())
                    .draftBookmark(p.getDraftBookmark())
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        // return null;
    }

    public DraftDetailResponseDto getDraftDetail(Long draftId) {
          try {
              Draft draft = draftRepository.findByDraftId(draftId);
            return DraftDetailResponseDto.builder().draftId(draft.getDraftId())
                .draftPrice(draft.getDraftPrice())
                .draftColorCode(draft.getDraftColorCode())
                .draftThumbnail(draft.getDraftThumbnail())
                .draftFileLink(draft.getDraftFileLink())
                .draftIsAI(draft.getDraftIsAI())
                .draftIsPublic(draft.getDraftIsPublic())
                .draftIsDefault(draft.getDraftIsDefault())
                .draftIsDeleted(draft.getDraftIsDeleted())
                .draftDownload(draft.getDraftDownload())
                .draftBookmark(draft.getDraftBookmark())
                .draftPrompt(draft.getDraftPrompt())
                .draftTimestamp(draft.getDraftTimestamp())
                .userId(draft.getUser().getUserId())
                .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Transactional
    @Override
    public DraftCreateResponseDto createDraft(int userId, DraftCreateRequestDto draftCreateDto) throws
        JsonProcessingException {
        List<Category> categoryList = categoryRepository.findByCategoryContentIn(draftCreateDto.getCategoryList());
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_DATA));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(draftCreateDto.getPalette());

        Draft draft = Draft.builder()
            .draftColorCode(json)
            .draftThumbnail(draftCreateDto.getImageFile())
            .draftFileLink(draftCreateDto.getDraftFileLink())
            .draftIsAI(draftCreateDto.isDraftIsAI())
            .draftIsPublic(draftCreateDto.isDraftShare())
            .draftPrompt(draftCreateDto.getDraftPrompt())
            .draftBookmark(0)
            .draftDownload(0)
            .draftIsDefault(false)
            .draftIsDeleted(false)
            .draftPrice(0)
            .draftTimestamp(new Timestamp(System.currentTimeMillis()))
            .user(user).build();

        draftRepository.save(draft);

        Palette palette = Palette.builder()
            .paletteName(draftCreateDto.getPaletteTitle())
            .paletteColorCode(json)
            .paletteCreatedAt(new Timestamp(System.currentTimeMillis()))
            .paletteLastModifiedTime(new Timestamp(System.currentTimeMillis()))
            .draft(draft).build();
        paletteRepository.save(palette);

        for(Category category : categoryList){
            draftCategoryRepository.insertDraftCategory(draft.getDraftId(), category.getCategoryId());
        }

        return new DraftCreateResponseDto(draft.getDraftId(), palette.getPaletteId());
    }

    @Transactional
    @Override
    public void deleteDraft(int userId, Long draftId) {
        Draft draft = draftRepository.findByDraftId(draftId);
        if(draft.getUser().getUserId() == userId){
            draft.setDraftIsDeleted(true);
        }
        else{
            // throw new CustomExcpException("삭제 권한이 없습니다.");
        }
    }

    @Override
    public boolean updateDraft(long draftId, DraftCategoryModifyRequestDto draftCategoryModifyRequestDto) {
        Draft draft = draftRepository.findByDraftId(draftId);
        if(draft == null){
            // throw new CustomExcpException("존재하지 않는 도안입니다.");
        }

        if(draft.getDraftIsDeleted()){
            // throw new CustomExcpException("삭제된 도안입니다.");
        }

        List<Category> categoryList = categoryRepository.findByCategoryContentIn(draftCategoryModifyRequestDto.getCategoryList());
        draftCategoryRepository.deleteDraftCategory(draftId);
        for(Category category : categoryList){
            draftCategoryRepository.insertDraftCategory(draftId, category.getCategoryId());
        }
        return true;

    }

    // 북마크
    @Override
    public void createBookmarkDraft(int userId, Long draftId) throws Exception{
        Draft draft = draftRepository.findByDraftId(draftId);
        if(draft.getDraftIsDeleted()){
            // throw new CustomExcpException("삭제된 도안입니다.");
        }

        bookmarkRepository.insertBookmark(userId, draftId);
    }

    @Override
    public void deleteBookmarkDraft(int userId, Long draftId) {
        Draft draft = draftRepository.findByDraftId(draftId);

        if(draft.getDraftIsDeleted()){
            // throw new CustomExcpException("삭제된 도안입니다.");
        }
        bookmarkRepository.deleteBookmark(userId, draftId);
    }

    @Override
    public void buyDraft(int userId, Long draftId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_DATA));
        Draft draft = draftRepository.findByDraftId(draftId);

        if(draft.getDraftIsDeleted()){
            // throw new CustomExcpException("삭제된 도안입니다.");
        }

        if(user.getUserMileage() < draft.getDraftPrice()){
            // throw new CustomExcpException("마일리지가 부족합니다.");
        }

        user.setUserMileage(user.getUserMileage() - draft.getDraftPrice());
        purchaseRepository.insertPurchase(userId, draftId, draft.getDraftPrice());
        // purchaseRepository.save(null);

    }

}
