package org.example.personmanagerapi.csvimport;

import org.example.personmanagerapi.csvImport.CSVImportService;
import org.example.personmanagerapi.csvImport.CSVProcessAsync;
import org.example.personmanagerapi.csvImport.ImportStatusRepository;
import org.example.personmanagerapi.csvImport.model.ImportStatus;
import org.example.personmanagerapi.csvImport.model.ImportStatusDTO;
import org.example.personmanagerapi.csvImport.model.ImportStatusMapper;
import org.example.personmanagerapi.exception.CSVProcessingException;
import org.example.personmanagerapi.exception.ImportStatusNotFoundException;
import org.example.personmanagerapi.person.PersonRepository;
import org.example.personmanagerapi.person.model.Person;
import org.example.personmanagerapi.student.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Mock
    private PersonRepository personRepository;

    private ImportStatus importStatus;
    private ImportStatusDTO importStatusDTO;

    @Captor
    private ArgumentCaptor<ImportStatus> importStatusCaptor;

    @Captor
    private ArgumentCaptor<MultipartFile> multipartFileCaptor;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @BeforeEach
    void setUp() {
        importStatus = new ImportStatus(1L, "IN_PROGRESS", LocalDateTime.now(), null, 0);
        importStatusDTO = new ImportStatusDTO(1L, "IN_PROGRESS", importStatus.getCreatedDate(), null, 0);
    }

    @Test
    @Transactional
    void testStartCSVImportTransactional() {
        String csvContent = "type,firstName,lastName,pesel,height,weight,email\n" +
                "employee,John,Doe,12345678901,180,75,john.doe@example.com\n" +
                "employee,Jane,Doe,1234567890X,160,60,jane.doe@example.com";

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        when(importStatusRepository.save(any(ImportStatus.class))).thenReturn(importStatus);
        doAnswer(invocation -> {
            throw new RuntimeException("Processing error");
        }).when(csvProcessAsync).processCSVFile(any(), anyLong());

        Exception exception = assertThrows(CSVProcessingException.class, () -> {
            csvImportService.startCSVImport(file);
        });

        String expectedMessage = "CSV file processing has failed to start";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(importStatusRepository, times(2)).save(importStatusCaptor.capture());
        List<ImportStatus> capturedStatuses = importStatusCaptor.getAllValues();
        assertEquals("IN_PROGRESS", capturedStatuses.get(0).getStatus());
        assertEquals("FAILED", capturedStatuses.get(1).getStatus());

        verify(personRepository, never()).save(any());
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

    @Test
    void testAsynchronousBehavior() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        when(importStatusRepository.save(any(ImportStatus.class))).thenReturn(importStatus);
        when(importStatusMapper.toDTO(any(ImportStatus.class))).thenReturn(importStatusDTO);
        doNothing().when(csvProcessAsync).processCSVFile(any(), anyLong());

        ImportStatusDTO result = csvImportService.startCSVImport(file);

        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());

        verify(csvProcessAsync, timeout(1000)).processCSVFile(multipartFileCaptor.capture(), longCaptor.capture());
        assertEquals(file, multipartFileCaptor.getValue());
        assertEquals(importStatus.getId(), longCaptor.getValue());
    }

    @Test
    void testImportCSVAndVerifyFirstRecord() throws Exception {
        String csvContent = "type,firstName,lastName,pesel,height,weight,email,universityName,yearOfStudy,fieldOfStudy,scholarship\n" +
                "student,Izajasz,Chmura,79926920518,156.5774843891521,59.79915007826378,arseniusz.skibinski@hotmail.com,Northern Wolak,2,Bachelor of Education,1474\n";
        InputStream is = new java.io.ByteArrayInputStream(csvContent.getBytes());
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", is);

        when(importStatusRepository.save(any())).thenReturn(importStatus);
        when(importStatusMapper.toDTO(any())).thenReturn(importStatusDTO);

        ImportStatusDTO result = csvImportService.startCSVImport(file);

        assertEquals(importStatusDTO, result);

        csvProcessAsync.processCSVFile(file, importStatus.getId());

        List<Person> savedPersons = List.of(new Student(UUID.randomUUID(), "Izajasz", "Chmura", "79926920518", 156.5774843891521, 59.79915007826378, "arseniusz.skibinski@hotmail.com", null, "Northern Wolak", 2, "Bachelor of Education", 1474.0));
        when(personRepository.findAll()).thenReturn(savedPersons);

        List<Person> persons = personRepository.findAll();
        assertEquals(1, persons.size());

        Person person = persons.get(0);
        assertTrue(person instanceof Student);
        Student student = (Student) person;
        assertEquals("Izajasz", student.getFirstName());
        assertEquals("Chmura", student.getLastName());
        assertEquals("79926920518", student.getPesel());
        assertEquals(156.5774843891521, student.getHeight());
        assertEquals(59.79915007826378, student.getWeight());
        assertEquals("arseniusz.skibinski@hotmail.com", student.getEmail());
        assertEquals("Northern Wolak", student.getUniversityName());
        assertEquals(2, student.getYearOfStudy());
        assertEquals("Bachelor of Education", student.getFieldOfStudy());
        assertEquals(1474.0, student.getScholarship());
    }
}