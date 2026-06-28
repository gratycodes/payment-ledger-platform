package com.gratycodes.ledger.api;

import com.gratycodes.ledger.application.JournalEntryResult;
import com.gratycodes.ledger.application.PostJournalEntryCommand;
import com.gratycodes.ledger.application.PostJournalEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/ledger")
public class LedgerController {
  private final PostJournalEntryService service;

  public LedgerController(PostJournalEntryService service) {
    this.service = service;
  }

  @PostMapping("/journal-entries")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public JournalEntryResult postJournalEntry(
      @RequestHeader("Idempotency-Key") String idempotencyKey,
      @Valid @RequestBody PostJournalEntryRequest request
  ) {
    var command = new PostJournalEntryCommand(
        idempotencyKey,
        request.reference(),
        request.currency(),
        request.lines().stream()
            .map(line -> new PostJournalEntryCommand.Line(
                line.accountId(),
                line.direction(),
                line.amountMinor()
            ))
            .toList()
    );
    return service.post(command);
  }
}

