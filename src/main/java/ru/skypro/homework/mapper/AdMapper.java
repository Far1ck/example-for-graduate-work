package ru.skypro.homework.mapper;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.entity.AdEntity;

@Component
public class AdMapper {

    public Ad toDto(AdEntity entity) {
        if (entity == null) {
            return null;
        }
        Ad dto = new Ad();
        dto.setAuthor(entity.getAdAuthor().getId());
        dto.setImage(entity.getImage());
        dto.setPk(entity.getId());
        dto.setPrice(entity.getPrice());
        dto.setTitle(entity.getTitle());
        return dto;
    }

    public AdEntity toEntity(Ad dto) {
        if (dto == null) {
            return null;
        }
        AdEntity entity = new AdEntity();
        entity.setImage(dto.getImage());
        entity.setId(dto.getPk());
        entity.setPrice(dto.getPrice());
        entity.setTitle(dto.getTitle());
        return entity;
    }
}
