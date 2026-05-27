package com.ski.resort.service;

import com.ski.resort.common.PageResult;
import com.ski.resort.entity.Venue;
import java.util.List;

public interface VenueService {
    Venue getById(Long id);
    Venue getByCode(String venueCode);
    boolean save(Venue venue);
    boolean update(Venue venue);
    boolean deleteById(Long id);
    List<Venue> listAll();
    List<Venue> listByType(String venueType);
    List<Venue> listAvailable();
    List<Venue> listByDifficulty(String difficultyLevel);
    PageResult<Venue> page(Integer pageNum, Integer pageSize);
    PageResult<Venue> pageByCondition(String venueCode, String venueName, String venueType, Integer status, Integer pageNum, Integer pageSize);
    boolean updateStatus(Long id, Integer status);
    boolean enter(Long id);
    boolean leave(Long id);
}



