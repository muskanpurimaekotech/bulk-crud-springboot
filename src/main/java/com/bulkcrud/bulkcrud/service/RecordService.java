package com.bulkcrud.bulkcrud.service;

import com.bulkcrud.bulkcrud.entity.Record;
import com.bulkcrud.bulkcrud.repository.RecordRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public List<Record> saveAll(List<Record> records) {
        if (records == null || records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Record list cannot be null or empty");
        }

        List<Record> filteredRecords = records.stream()
                .filter(r -> recordRepository.findByEmail(r.getEmail()).isEmpty())
                .collect(Collectors.toList());

        if (filteredRecords.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All records already exist in database");
        }

        return recordRepository.saveAll(filteredRecords);
    }


    public List<Record> getAll() {
        return recordRepository.findAll();
    }

    public void deleteById(Long id) {
        recordRepository.deleteById(id);
    }

    public Record update(Long id, Record updatedRecord) {
        Optional<Record> existingRecordOpt = recordRepository.findById(id);
        if (existingRecordOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record with id " + id + " not found");
        }

        Record existingRecord = existingRecordOpt.get();

        Optional<Record> emailCheck = recordRepository.findByEmail(updatedRecord.getEmail());
        if (emailCheck.isPresent() && !emailCheck.get().getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists: " + updatedRecord.getEmail());
        }

        existingRecord.setName(updatedRecord.getName());
        existingRecord.setEmail(updatedRecord.getEmail());
        existingRecord.setAge(updatedRecord.getAge());

        return recordRepository.save(existingRecord);
    }
}
