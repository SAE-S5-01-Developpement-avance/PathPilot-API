package fr.iut.pathpilotapi.salesman;

import fr.iut.pathpilotapi.test.IntegrationTestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the Salesman class.
 */
class SalesmanTest {

    private static Salesman copy(Salesman salesman) {
        Salesman copy = new Salesman();
        copy.setId(salesman.getId());
        copy.setEmailAddress(salesman.getEmailAddress());
        copy.setPassword(salesman.getPassword());
        copy.setFirstName(salesman.getFirstName());
        copy.setLastName(salesman.getLastName());
        copy.setLatHomeAddress(salesman.getLatHomeAddress());
        copy.setLongHomeAddress(salesman.getLongHomeAddress());
        return copy;
    }

    @Test
    void testEquals() {
        Salesman salesman1 = IntegrationTestUtils.createSalesman();
        salesman1.setId(1);

        // Test with the same object
        assertEquals(salesman1, salesman1);

        // Test with null and different object
        assertNotEquals(null, salesman1);
        assertNotEquals(new Object(), salesman1);

        // Test with same fields but different objects
        Salesman salesman2 = copy(salesman1);
        assertEquals(salesman1, salesman2);
        assertNotSame(salesman1, salesman2);


        // Test with one different field
        Salesman salesmanId = copy(salesman1);
        salesmanId.setId(2);

        Salesman salesmanEmail = copy(salesman1);
        salesmanEmail.setEmailAddress(salesman1.getEmailAddress() + "test");

        Salesman salesmanPassword = copy(salesman1);
        salesmanPassword.setPassword(salesman1.getPassword() + "test");

        Salesman salesmanFirstName = copy(salesman1);
        salesmanFirstName.setFirstName(salesman1.getFirstName() + "test");

        Salesman salesmanLastName = copy(salesman1);
        salesmanLastName.setLastName(salesman1.getLastName() + "test");

        Salesman salesmanLatHomeAddress = copy(salesman1);
        salesmanLatHomeAddress.setLatHomeAddress(salesman1.getLatHomeAddress() + 1);

        Salesman salesmanLongHomeAddress = copy(salesman1);
        salesmanLongHomeAddress.setLongHomeAddress(salesman1.getLongHomeAddress() + 1);

        assertNotEquals(salesman1, salesmanId);
        assertNotEquals(salesman1, salesmanEmail);
        assertNotEquals(salesman1, salesmanPassword);
        assertNotEquals(salesman1, salesmanFirstName);
        assertNotEquals(salesman1, salesmanLastName);
        assertNotEquals(salesman1, salesmanLatHomeAddress);
        assertNotEquals(salesman1, salesmanLongHomeAddress);
    }
}