package com.eouil.bank.bankapi.services;

import com.eouil.bank.bankapi.dtos.requests.DepositRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.TransferRequestDTO;
import com.eouil.bank.bankapi.dtos.requests.WithdrawRequestDTO;
import com.eouil.bank.bankapi.dtos.responses.TransactionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionResponseDTO transfer(TransferRequestDTO request) {
        // 1. 계좌 유효성, 잔액 체크, 이체 처리
        // 2. JDBC or JPA로 거래 저장
        // 3. 응답 DTO로 변환
        return new TransactionResponseDTO(); // TODO: 구현
    }

    public TransactionResponseDTO withdraw(WithdrawRequestDTO request) {
        // 1. 계좌 조회, 잔액 확인
        // 2. 출금 처리 및 트랜잭션 기록
        return new TransactionResponseDTO(); // TODO: 구현
    }

    public TransactionResponseDTO deposit(DepositRequestDTO request) {
        // 1. 계좌 조회
        // 2. 예금 처리 및 트랜잭션 기록
        return new TransactionResponseDTO(); // TODO: 구현
    }
}
