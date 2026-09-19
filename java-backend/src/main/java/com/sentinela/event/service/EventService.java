package com.sentinela.event.service;

import com.sentinela.asset.entity.Asset;
import com.sentinela.asset.repository.AssetRepository;
import com.sentinela.event.dto.EventRequest;
import com.sentinela.event.dto.EventResponse;
import com.sentinela.event.entity.Event;
import com.sentinela.event.entity.EventType;
import com.sentinela.event.repository.EventRepository;
import com.sentinela.exception.ResourceNotFoundException;
import com.sentinela.rust.RustEventRequest;
import com.sentinela.user.entity.User;
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
        // TODO: Adicionar lógica de transformar os dados do usuário e servidor vindos da API-KEY e transformar como objeto para os campos user e asset.
        repository.save(event);
        return EventResponse.fromEntity(event);
    }

    public EventResponse saveFromRust(RustEventRequest rustEventRequest) {
        Event event = new Event();
        event.setIp(rustEventRequest.ip());
        event.setSource(rustEventRequest.source());
        event.setTimestamp(rustEventRequest.timestamp());
        event.setEventType(eventTypeFromRust(rustEventRequest.eventType()));
        event.setUser(userRepository.findByEmail(rustEventRequest.user())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
        event.setAsset(assetRepository.findByName(rustEventRequest.asset())
                .or(() -> assetRepository.findByHostname(rustEventRequest.asset()))
                .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado")));

        return EventResponse.fromEntity(repository.save(event));
    }

    private EventType eventTypeFromRust(String eventType) {
        if ("LOGIN_SUCCESS".equals(eventType)) {
            return EventType.LOGIN_SUCESS;
        }
        return EventType.valueOf(eventType);
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
        event.setId(eventRequest.getId());
        event.setIp(eventRequest.getIp());
        event.setSource(eventRequest.getSource());
        event.setTimestamp(eventRequest.getTimestamp());
        event.setEventType(eventRequest.getEventType());
        event.setUser(resolveUser(eventRequest));
        event.setAsset(resolveAsset(eventRequest));
        return event;
    }

    private User resolveUser(EventRequest eventRequest) {
        if (eventRequest.getUserId() != null) {
            return userRepository.findById(eventRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        }
        return userRepository.findByEmail(eventRequest.getUser())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private Asset resolveAsset(EventRequest eventRequest) {
        if (eventRequest.getAssetId() != null) {
            return assetRepository.findById(eventRequest.getAssetId())
                    .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
        }
        return assetRepository.findByName(eventRequest.getAsset())
                .or(() -> assetRepository.findByHostname(eventRequest.getAsset()))
                .orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
    }


}
