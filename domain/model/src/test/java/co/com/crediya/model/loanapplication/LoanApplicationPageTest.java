package co.com.crediya.model.loanapplication;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanApplicationPageTest {

    @Test
    void builderSetsAllFields() {
        LoanApplicationForPage item = LoanApplicationForPage.builder().id(1L).build();
        LoanApplicationPage page = LoanApplicationPage.builder()
                .content(List.of(item))
                .elementsInPage(1)
                .pageNumber(2)
                .totalElements(10L)
                .pageSize(5)
                .totalPages(2)
                .build();

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getElementsInPage());
        assertEquals(2, page.getPageNumber());
        assertEquals(10L, page.getTotalElements());
        assertEquals(5, page.getPageSize());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void toBuilderCopiesAndAllowsChanges() {
        LoanApplicationPage base = LoanApplicationPage.builder()
                .content(List.of())
                .elementsInPage(0)
                .pageNumber(0)
                .totalElements(0L)
                .pageSize(10)
                .totalPages(0)
                .build();

        LoanApplicationPage modified = base.toBuilder()
                .elementsInPage(3)
                .pageNumber(1)
                .totalElements(15L)
                .totalPages(2)
                .build();

        assertEquals(3, modified.getElementsInPage());
        assertEquals(1, modified.getPageNumber());
        assertEquals(15L, modified.getTotalElements());
        assertEquals(10, modified.getPageSize());
        assertEquals(2, modified.getTotalPages());
    }
}

