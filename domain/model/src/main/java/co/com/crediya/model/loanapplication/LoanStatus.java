package co.com.crediya.model.loanapplication;

import lombok.Getter;

@Getter
public enum LoanStatus {
    
    PENDING(1),
    APPROVED(2),
    REJECTED(3);
    
    private final int statusId;

    LoanStatus(int statusId) {
        this.statusId = statusId;
    }
        
}
