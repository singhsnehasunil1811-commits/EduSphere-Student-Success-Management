package com.student360.repository;

import com.student360.entity.Notice;
import com.student360.entity.TargetAudience;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByTargetAudience(TargetAudience audience);
    List<Notice> findAllByOrderByDateDescCreatedAtDesc();
    List<Notice> findTop5ByOrderByDateDescCreatedAtDesc();
}
