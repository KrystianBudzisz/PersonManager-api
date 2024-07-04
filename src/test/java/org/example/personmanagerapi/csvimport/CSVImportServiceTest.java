package org.example.personmanagerapi.csvimport;

import org.example.personmanagerapi.csvImport.CSVImportService;
import org.example.personmanagerapi.csvImport.CSVProcessAsync;
import org.example.personmanagerapi.csvImport.ImportStatusRepository;

import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.example.personmanagerapi.csvImport.model.ImportStatusMapper;
import org.example.personmanagerapi.exception.CSVProcessingException;
import org.example.personmanagerapi.exception.ConcurrentImportException;
import org.example.personmanagerapi.exception.ImportStatusNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CSVImportServiceTest {

    @Mock
    private CSVProcessAsync csvProcessAsync;

    @Mock
    private ImportStatusRepository importStatusRepository;

    @Mock
    private ImportStatusMapper importStatusMapper;

    @InjectMocks
    private CSVImportService csvImportService;

    private ImportStatus importStatus;
    private ImportStatusDTO importStatusDTO;

    @BeforeEach
    void setUp() {
        importStatus = new ImportStatus(1L, "IN_PROGRESS", LocalDateTime.now(), null, 0);
        importStatusDTO = new ImportStatusDTO(1L, "IN_PROGRESS", importStatus.getCreatedDate(), null, 0);
    }

    @Test
    void testStartCSVImportSuccess() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        when(importStatusRepository.save(any(ImportStatus.class))).thenReturn(importStatus);
        when(importStatusMapper.toDTO(any(ImportStatus.class))).thenReturn(importStatusDTO);

        ImportStatusDTO result = csvImportService.startCSVImport(file);

        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(importStatusRepository, times(2)).save(any(ImportStatus.class));
        verify(csvProcessAsync).processCSVFile(file, importStatus.getId());
    }

    @Test
    void testStartCSVImportFileEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);

        Exception exception = assertThrows(CSVProcessingException.class, () -> {
            csvImportService.startCSVImport(file);
        });

        String expectedMessage = "Bad request. File is empty.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testStartCSVImportConcurrentImport() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        doThrow(new ConcurrentImportException("Another import is already in progress. Please wait until it completes."))
                .when(csvProcessAsync).processCSVFile(any(MultipartFile.class), anyLong());

        Exception exception = assertThrows(ConcurrentImportException.class, () -> {
            csvImportService.startCSVImport(file);
        });

        String expectedMessage = "Another import is already in progress. Please wait until it completes.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testGetImportStatusSuccess() {
        when(importStatusRepository.findById(anyLong())).thenReturn(Optional.of(importStatus));
        when(importStatusMapper.toDTO(any(ImportStatus.class))).thenReturn(importStatusDTO);

        ImportStatusDTO result = csvImportService.getImportStatus(1L);

        assertNotNull(result);
        assertEquals(importStatusDTO.getId(), result.getId());
    }

    @Test
    void testGetImportStatusNotFound() {
        when(importStatusRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(ImportStatusNotFoundException.class, () -> {
            csvImportService.getImportStatus(1L);
        });

        String expectedMessage = "Import status not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}