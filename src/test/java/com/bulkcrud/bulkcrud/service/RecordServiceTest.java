package com.bulkcrud.bulkcrud.service;

import com.bulkcrud.bulkcrud.entity.Record;
import com.bulkcrud.bulkcrud.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordServiceTest {

    @InjectMocks
    private RecordService recordService;

    @Mock
    private RecordRepository recordRepository;

    private Record record1;
    private Record record2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        record1 = new Record("Muskan Goswami", "muskan2@gmail.com", 25);
        record1.setId(1L);

        record2 = new Record("Goswami Muskan", "goswami1@gmail.com", 30);
        record2.setId(2L);
    }

    @Test
    void saveAll_success() {
        when(recordRepository.findByEmail("muskan2@gmail.com")).thenReturn(Optional.empty());
        when(recordRepository.findByEmail("goswami1@gmail.com")).thenReturn(Optional.empty());
        when(recordRepository.saveAll(anyList())).thenReturn(Arrays.asList(record1, record2));

        List<Record> result = recordService.saveAll(Arrays.asList(record1, record2));
        assertEquals(2, result.size());
        verify(recordRepository, times(1)).saveAll(anyList());
    }

    @Test
    void saveAll_allDuplicates_throwsException() {
        when(recordRepository.findByEmail(anyString())).thenReturn(Optional.of(record1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                recordService.saveAll(Arrays.asList(record1))
        );
        assertEquals("All records already exist in database", exception.getReason());
    }

    @Test
    void update_success() {
        Record updatedRecord = new Record("Muskan Updated", "muskan2@gmail.com", 26);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record1));
        when(recordRepository.findByEmail("muskan2@gmail.com")).thenReturn(Optional.of(record1));
        when(recordRepository.save(any(Record.class))).thenReturn(updatedRecord);

        Record result = recordService.update(1L, updatedRecord);
        assertEquals("Muskan Updated", result.getName());
        assertEquals(26, result.getAge());
        assertEquals("muskan2@gmail.com", result.getEmail());
    }

    @Test
    void update_duplicateEmail_throwsException() {
        Record anotherRecord = new Record("Someone Else", "someone@example.com", 35);
        anotherRecord.setId(3L);

        Record updatedRecord = new Record("Muskan Updated", "someone@example.com", 26);

        when(recordRepository.findById(1L)).thenReturn(Optional.of(record1));
        when(recordRepository.findByEmail("someone@example.com")).thenReturn(Optional.of(anotherRecord));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                recordService.update(1L, updatedRecord)
        );
        assertEquals("Email already exists: someone@example.com", exception.getReason());
    }

    @Test
    void deleteById_success() {
        doNothing().when(recordRepository).deleteById(1L);

        recordService.deleteById(1L);

        verify(recordRepository, times(1)).deleteById(1L);
    }
}
