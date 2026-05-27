package com.ski.resort.service.impl;

import com.ski.resort.common.PageResult;
import com.ski.resort.common.ResultCode;
import com.ski.resort.entity.Venue;
import com.ski.resort.exception.BusinessException;
import com.ski.resort.mapper.VenueMapper;
import com.ski.resort.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    private VenueMapper venueMapper;

    @Override
    public Venue getById(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Venue venue = venueMapper.selectById(id);
        if (venue == null) throw new BusinessException(ResultCode.FAILED);
        return venue;
    }

    @Override
    public Venue getByCode(String venueCode) {
        if (venueCode == null || venueCode.trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return venueMapper.selectByCode(venueCode);
    }

    @Override
    @Transactional
    public boolean save(Venue venue) {
        if (venue.getVenueCode() == null || venue.getVenueCode().trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        if (venue.getVenueName() == null || venue.getVenueName().trim().isEmpty()) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Venue existing = venueMapper.selectByCode(venue.getVenueCode());
        if (existing != null) throw new BusinessException("场地编号已存在");
        if (venue.getStatus() == null) venue.setStatus(1);
        if (venue.getCurrentCapacity() == null) venue.setCurrentCapacity(0);
        return venueMapper.insert(venue) > 0;
    }

    @Override
    @Transactional
    public boolean update(Venue venue) {
        if (venue.getId() == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Venue existing = venueMapper.selectById(venue.getId());
        if (existing == null) throw new BusinessException(ResultCode.FAILED);
        return venueMapper.update(venue) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return venueMapper.deleteById(id) > 0;
    }

    @Override
    public List<Venue> listAll() {
        return venueMapper.selectAll();
    }

    @Override
    public List<Venue> listByType(String venueType) {
        return venueMapper.selectByType(venueType);
    }

    @Override
    public List<Venue> listAvailable() {
        return venueMapper.selectAvailable();
    }

    @Override
    public List<Venue> listByDifficulty(String difficultyLevel) {
        return venueMapper.selectByDifficulty(difficultyLevel);
    }

    @Override
    public PageResult<Venue> page(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Venue> list = venueMapper.selectPage(offset, pageSize);
        Long total = venueMapper.countAll();
        PageResult<Venue> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int)((total + pageSize - 1) / pageSize));
        result.setList(list);
        return result;
    }

    @Override
    public PageResult<Venue> pageByCondition(String venueCode, String venueName, String venueType, Integer status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Venue> list = venueMapper.selectByCondition(venueCode, venueName, venueType, status, offset, pageSize);
        Long total = venueMapper.countByCondition(venueCode, venueName, venueType, status);
        PageResult<Venue> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int)((total + pageSize - 1) / pageSize));
        result.setList(list);
        return result;
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return venueMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean enter(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        Venue venue = venueMapper.selectById(id);
        if (venue == null) throw new BusinessException(ResultCode.FAILED);
        if (venue.getCurrentCapacity() >= venue.getMaxCapacity()) throw new BusinessException(ResultCode.FAILED);
        return venueMapper.incrementCurrentCapacity(id) > 0;
    }

    @Override
    @Transactional
    public boolean leave(Long id) {
        if (id == null) throw new BusinessException(ResultCode.VALIDATE_FAILED);
        return venueMapper.decrementCurrentCapacity(id) > 0;
    }
}



