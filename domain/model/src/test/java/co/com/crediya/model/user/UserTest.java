package co.com.crediya.model.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void builderSetsAllFields() {
        LocalDate birth = LocalDate.of(1990, 5, 20);

        User user = User.builder()
                .name("Ana")
                .lastName("García")
                .birthDate(birth)
                .address("Calle 123")
                .phone(3001234567L)
                .email("ana@example.com")
                .rolId(2L)
                .baseSalary(3500000.50)
                .build();

        assertEquals("Ana", user.getName());
        assertEquals("García", user.getLastName());
        assertEquals(birth, user.getBirthDate());
        assertEquals("Calle 123", user.getAddress());
        assertEquals(3001234567L, user.getPhone());
        assertEquals("ana@example.com", user.getEmail());
        assertEquals(2L, user.getRolId());
        assertEquals(3500000.50, user.getBaseSalary());
    }

    @Test
    void toBuilderCopiesAndAllowsChanges() {
        User base = User.builder()
                .name("Juan")
                .lastName("Pérez")
                .birthDate(LocalDate.of(1985, 1, 15))
                .address("Av. Siempre Viva 742")
                .phone(3115557788L)
                .email("juan@example.com")
                .rolId(1L)
                .baseSalary(2500000.0)
                .build();

        User modified = base.toBuilder()
                .address("Av. Central 100")
                .baseSalary(2600000.0)
                .build();

        assertEquals("Juan", modified.getName());
        assertEquals("Pérez", modified.getLastName());
        assertEquals(LocalDate.of(1985, 1, 15), modified.getBirthDate());
        assertEquals("Av. Central 100", modified.getAddress());
        assertEquals(3115557788L, modified.getPhone());
        assertEquals("juan@example.com", modified.getEmail());
        assertEquals(1L, modified.getRolId());
        assertEquals(2600000.0, modified.getBaseSalary());
    }
}

