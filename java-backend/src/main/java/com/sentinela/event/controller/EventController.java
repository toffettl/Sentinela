package com.sentinela.event.controller;

import com.sentinela.event.dto.EventRequest;
import com.sentinela.event.dto.EventResponse;
import com.sentinela.event.entity.Event;
import com.sentinela.event.repository.EventRepository;
import com.sentinela.event.service.EventService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    private List<EventResponse> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    private EventResponse findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PostMapping
    private EventResponse save(@RequestBody EventRequest eventRequest) {
        return eventService.save(eventRequest);
    }

    @DeleteMapping
    private EventResponse delete(@RequestBody EventRequest eventRequest) {
        return eventService.delete(eventRequest);
    }

    @DeleteMapping("/{id}")
    private EventResponse deleteById(@PathVariable Long id) {
        return eventService.deleteById(id);
    }
}
