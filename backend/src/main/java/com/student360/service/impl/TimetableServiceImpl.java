package com.student360.service.impl;

import com.student360.dto.TimetableRequest;
import com.student360.dto.TimetableResponse;
import com.student360.entity.Subject;
import com.student360.entity.Teacher;
import com.student360.entity.Timetable;
import com.student360.exception.ResourceNotFoundException;
import com.student360.repository.SubjectRepository;
import com.student360.repository.TeacherRepository;
import com.student360.repository.TimetableRepository;
import com.student360.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableServiceImpl implements TimetableService {
    private final TimetableRepository timetableRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public TimetableResponse create(TimetableRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(request.teacherId())
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            
        Timetable timetable = new Timetable();
        timetable.setDay(request.day().toUpperCase());
        timetable.setStartTime(request.startTime());
        timetable.setEndTime(request.endTime());
        timetable.setSubject(subject);
        timetable.setTeacher(teacher);
        timetable.setRoom(request.room());
        
        return mapToResponse(timetableRepository.save(timetable));
    }

    @Override
    public TimetableResponse update(Long id, TimetableRequest request) {
        Timetable timetable = timetableRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Timetable not found"));
            
        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Teacher teacher = teacherRepository.findById(request.teacherId())
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
            
        timetable.setDay(request.day().toUpperCase());
        timetable.setStartTime(request.startTime());
        timetable.setEndTime(request.endTime());
        timetable.setSubject(subject);
        timetable.setTeacher(teacher);
        timetable.setRoom(request.room());
        
        return mapToResponse(timetableRepository.save(timetable));
    }

    @Override
    public void delete(Long id) {
        if (!timetableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Timetable not found");
        }
        timetableRepository.deleteById(id);
    }

    @Override
    public List<TimetableResponse> getAll() {
        return timetableRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TimetableResponse> getByDay(String day) {
        return timetableRepository.findByDay(day.toUpperCase()).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TimetableResponse> getByCourse(Long courseId) {
        return timetableRepository.findBySubjectCourseId(courseId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TimetableResponse> getByTeacher(Long teacherId) {
        return timetableRepository.findByTeacherId(teacherId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private TimetableResponse mapToResponse(Timetable timetable) {
        return new TimetableResponse(
            timetable.getId(),
            timetable.getDay(),
            timetable.getStartTime(),
            timetable.getEndTime(),
            timetable.getSubject().getId(),
            timetable.getSubject().getName(),
            timetable.getSubject().getCode(),
            timetable.getTeacher().getId(),
            timetable.getTeacher().getUser().getFirstName() + " " + timetable.getTeacher().getUser().getLastName(),
            timetable.getRoom()
        );
    }
}
