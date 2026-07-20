package com.ledgr.service;

import com.ledgr.dto.TransactionForm;
import com.ledgr.entity.Category;
import com.ledgr.entity.Transaction;
import com.ledgr.entity.TransactionType;
import com.ledgr.entity.User;
import com.ledgr.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository txRepo;

    public TransactionService(TransactionRepository txRepo) {
        this.txRepo = txRepo;
    }

    public List<Transaction> allFor(User user) {
        return txRepo.findByUserOrderByDateDesc(user);
    }

    public Transaction create(User user, TransactionForm form) {
        Transaction tx = new Transaction(
                form.getAmount(),
                form.getDescription(),
                Category.valueOf(form.getCategory()),
                TransactionType.valueOf(form.getType()),
                form.getDate(),
                user
        );
        return txRepo.save(tx);
    }

    public Transaction getOwned(User user, Long id) {
        Transaction tx = txRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction not found"));

        if (!tx.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "that's not yours");
        }

        return tx;
    }

    public Transaction update(User user, Long id, TransactionForm form) {
        Transaction tx = getOwned(user, id);

        tx.setAmount(form.getAmount());
        tx.setDescription(form.getDescription());
        tx.setCategory(Category.valueOf(form.getCategory()));
        tx.setType(TransactionType.valueOf(form.getType()));
        tx.setDate(form.getDate());

        return txRepo.save(tx);
    }

    public void delete(User user, Long id) {
        Transaction tx = getOwned(user, id);
        txRepo.delete(tx);
    }
}
