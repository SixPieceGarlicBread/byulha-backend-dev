package Byulha.project.domain.django.controller;

import Byulha.project.domain.django.model.dto.request.RequestSendToDjangoDto;
import Byulha.project.domain.django.service.DjangoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Django 테스트", description = "Django 관련 테스트용 API")
public class DjangoController {

    private final DjangoService djangoService;

    /**
     * Django로 데이터 전송 분위기 모델 테스트(프론트x)
     */
    @PostMapping("/image-django-new")
    public List<Map.Entry<String, String>> sendToFlaskForMood(@RequestBody RequestSendToDjangoDto dto) throws JsonProcessingException {
        return djangoService.sendToDjangoForMood(dto);
    }
}
