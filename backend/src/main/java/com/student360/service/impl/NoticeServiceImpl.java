package com.student360.service.impl;

import com.student360.dto.NoticeRequest;
import com.student360.dto.NoticeResponse;
import com.student360.entity.Notice;
import com.student360.entity.TargetAudience;
import com.student360.entity.User;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.NoticeRepository;
import com.student360.repository.UserRepository;
import com.student360.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Override
    public NoticeResponse create(NoticeRequest request, String username) {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        Notice notice = new Notice();
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setPriority(request.priority());
        notice.setTargetAudience(request.targetAudience());
        notice.setCreatedBy(user);
        
        return mapToResponse(noticeRepository.save(notice));
    }

    @Override
    public NoticeResponse update(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));
            
        notice.setTitle(request.title());
        notice.setContent(request.content());
        notice.setPriority(request.priority());
        notice.setTargetAudience(request.targetAudience());
        
        return mapToResponse(noticeRepository.save(notice));
    }

    @Override
    public void delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notice not found");
        }
        noticeRepository.deleteById(id);
    }

    @Override
    public List<NoticeResponse> getAll() {
        return noticeRepository.findAllByOrderByDateDescCreatedAtDesc().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public NoticeResponse getById(Long id) {
        return noticeRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));
    }

    @Override
    public List<NoticeResponse> getRecent() {
        return noticeRepository.findTop5ByOrderByDateDescCreatedAtDesc().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<NoticeResponse> getForAudience(TargetAudience audience) {
        return noticeRepository.findByTargetAudience(audience).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private NoticeResponse mapToResponse(Notice notice) {
        return new NoticeResponse(
            notice.getId(),
            notice.getTitle(),
            notice.getContent(),
            notice.getPriority(),
            notice.getDate(),
            notice.getTargetAudience(),
            notice.getCreatedBy().getFirstName() + " " + notice.getCreatedBy().getLastName(),
            notice.getCreatedAt()
        );
    }
}
