package fr.iut.pathpilotapi.services;

import fr.iut.pathpilotapi.salesman.Salesman;
import fr.iut.pathpilotapi.salesman.SalesmanRepository;
import fr.iut.pathpilotapi.salesman.SalesmanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SalesmanServiceTest {

    @Mock
    private SalesmanRepository salesmanRepository;

    @InjectMocks
    private SalesmanService salesmanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSalesmen() {
        List<Salesman> expectedSalesmen = Collections.emptyList();
        when(salesmanRepository.findAll()).thenReturn(expectedSalesmen);

        List<Salesman> result = salesmanService.getAllSalesmen();

        assertEquals(expectedSalesmen, result);
        verify(salesmanRepository, times(1)).findAll();
    }

    @Test
    void testAddSalesman() {
        Salesman salesman = new Salesman();
        when(salesmanRepository.save(salesman)).thenReturn(salesman);

        Salesman result = salesmanService.addSalesman(salesman);

        assertEquals(salesman, result);
        verify(salesmanRepository, times(1)).save(salesman);
    }

    @Test
    void testGetSalesmanById() {
        Integer salesmanId = 1;
        Salesman salesman = new Salesman();
        when(salesmanRepository.findById(salesmanId)).thenReturn(Optional.of(salesman));

        Salesman result = salesmanService.getSalesmanById(salesmanId);

        assertEquals(salesman, result);
        verify(salesmanRepository, times(1)).findById(salesmanId);
    }

    @Test
    void testGetSalesmanByIdNotFound() {
        Integer salesmanId = 1;
        when(salesmanRepository.findById(salesmanId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            salesmanService.getSalesmanById(salesmanId);
        });

        assertEquals("Salesman not found", exception.getMessage());
        verify(salesmanRepository, times(1)).findById(salesmanId);
    }
}