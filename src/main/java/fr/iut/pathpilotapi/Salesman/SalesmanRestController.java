package fr.iut.pathpilotapi.Salesman;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/salesman")
public class SalesmanRestController {
    private final SalesmanService salesmanService;

    public SalesmanRestController(SalesmanService salesmanService) {
        this.salesmanService = salesmanService;
    }

    @GetMapping("/")
    public List<Salesman> list() {
        return salesmanService.getAllSalesmen();
    }

    @PostMapping("/")
    public Salesman addSalesman(Salesman salesman) {
        salesmanService.addSalesman(salesman);
        return salesman;
    }

    @PostMapping("/login")
    public boolean login(String email, String password) {
        return true;
    }
}
