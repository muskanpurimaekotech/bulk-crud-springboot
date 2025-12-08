package com.bulkcrud.bulkcrud.controller;

import com.bulkcrud.bulkcrud.entity.Record;
import com.bulkcrud.bulkcrud.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Record>> addRecords(@RequestBody @Valid List<Record> records) {
        List<Record> saved = recordService.saveAll(records);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Record>> getAllRecords() {
        return ResponseEntity.ok(recordService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRecord(@PathVariable Long id) {
        recordService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Record with id " + id + " deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Record> updateRecord(@PathVariable Long id, @RequestBody @Valid Record record) {
        Record updated = recordService.update(id, record);
        return ResponseEntity.ok(updated);
    }
}
