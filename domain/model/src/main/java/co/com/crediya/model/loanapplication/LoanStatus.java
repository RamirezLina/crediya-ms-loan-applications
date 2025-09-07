package co.com.crediya.model.loanapplication;

import lombok.Getter;

@Getter
public enum LoanStatus {
    
    PENDING(1),
    APPROVED(2),
    REJECTED(3),
    MANUAL(4);
    
    private final long statusId;

    LoanStatus(long statusId) {
        this.statusId = statusId;
    }
        
}
