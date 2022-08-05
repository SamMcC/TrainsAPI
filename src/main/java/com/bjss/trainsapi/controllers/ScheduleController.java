package com.bjss.trainsapi.controllers;

import com.bjss.trainsapi.exceptions.EntityNotFoundException;
import com.bjss.trainsapi.model.persistence.Schedule;
import com.bjss.trainsapi.services.IScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final IScheduleService scheduleService;

    @GetMapping
    public List<Schedule> findAll() {
        return scheduleService.findAll();
    }

    @GetMapping("/{id}")
    public Schedule findById(@PathVariable long id) {
        return scheduleService.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Schedule createSchedule(@RequestBody Schedule schedule) {
        return scheduleService.save(schedule);
    }

    @PutMapping("/{id}")
    public Schedule updateSchedule(@RequestBody Schedule schedule, @PathVariable long id) {
        Schedule foundSchedule = scheduleService.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        return scheduleService.save(schedule.withId(foundSchedule.getId()));
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable long id) {
        scheduleService.deleteById(id);
    }
}
