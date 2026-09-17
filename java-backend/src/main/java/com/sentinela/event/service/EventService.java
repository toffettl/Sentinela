package com.sentinela.event.service;

import com.sentinela.asset.repository.AssetRepository;
import com.sentinela.event.dto.EventRequest;
import com.sentinela.event.dto.EventResponse;
import com.sentinela.event.entity.Event;
import com.sentinela.event.entity.EventType;
import com.sentinela.event.repository.EventRepository;
import com.sentinela.exception.ResourceNotFoundException;
import com.sentinela.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public EventService(EventRepository repository, UserRepository userRepository, AssetRepository assetRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }

    public List<EventResponse> findAll() {
        return repository.findAll().stream().map(EventResponse::fromEntity).toList();
    }

    public EventResponse findById(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));
        return EventResponse.fromEntity(event);
    }

    public EventResponse save(EventRequest eventRequest) {
        Event event = eventRequestToEvent(eventRequest);
        repository.save(event);
        return EventResponse.fromEntity(event);
    }

    public EventResponse delete(EventRequest eventRequest) {
        Event event = eventRequestToEvent(eventRequest);
        repository.delete(event);
        return EventResponse.fromEntity(event);
    }

    public EventResponse deleteById(Long id) {
        Event event = repository.findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        repository.delete(event);
        return EventResponse.fromEntity(event);
    }

    public Event eventRequestToEvent(EventRequest eventRequest) {
        Event event = new Event();
        event.setIp(eventRequest.getIp());
        event.setSource(eventRequest.getSource());
        event.setTimestamp(eventRequest.getTimestamp());
        event.setEventType(eventRequest.getEventType());
        event.setUser(userRepository.findById(eventRequest.getUserId()).orElseThrow(() -> new RuntimeException("Usuário não encontrado")));
        event.setAsset(assetRepository.findById(eventRequest.getAssetId()).orElseThrow(() -> new RuntimeException("Ativo não encontrado")));
        return event;
    }


}
