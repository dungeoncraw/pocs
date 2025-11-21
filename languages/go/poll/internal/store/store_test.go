package store_test

import (
    "testing"

    "github.com/thiagonasc/poll/internal/models"
    "github.com/thiagonasc/poll/internal/store"
)

func newOpenPoll(t *testing.T, s *store.MemoryStore, pollID string) {
    t.Helper()
    if err := s.CreatePoll(pollID, "question?", true); err != nil {
        t.Fatalf("create poll: %v", err)
    }
}

func addOption(t *testing.T, s *store.MemoryStore, pollID, optID, label string) {
    t.Helper()
    if err := s.AddOption(pollID, optID, label); err != nil {
        t.Fatalf("add option: %v", err)
    }
}

func TestMemoryStore_CreateAndVote(t *testing.T) {
    s := store.New()
    pollID := "p1"
    optID := "o1"
    newOpenPoll(t, s, pollID)
    addOption(t, s, pollID, optID, "Option 1")

    if err := s.CheckPollAndOption(pollID, optID); err != nil {
        t.Fatalf("CheckPollAndOption failed: %v", err)
    }

    if err := s.ApplyVote(models.VoteRequest{PollID: pollID, OptionID: optID, VoterID: "u1"}); err != nil {
        t.Fatalf("ApplyVote: %v", err)
    }

    snap, ok := s.GetPollSnapshot(pollID)
    if !ok {
        t.Fatalf("GetPollSnapshot not found")
    }
    if len(snap.Options) != 1 || snap.Options[0].Votes != 1 {
        t.Fatalf("unexpected votes after ApplyVote: %+v", snap.Options)
    }
    if len(snap.Voters) != 1 || snap.Voters[0] != "u1" {
        t.Fatalf("unexpected voters: %+v", snap.Voters)
    }
}

func TestMemoryStore_DuplicateVoteRejected(t *testing.T) {
    s := store.New()
    pollID := "p2"
    optID := "o1"
    newOpenPoll(t, s, pollID)
    addOption(t, s, pollID, optID, "Option 1")

    vr := models.VoteRequest{PollID: pollID, OptionID: optID, VoterID: "u1"}
    if err := s.ApplyVote(vr); err != nil {
        t.Fatalf("first vote failed: %v", err)
    }
    if err := s.ApplyVote(vr); err == nil {
        t.Fatalf("expected duplicate vote to fail, got nil error")
    }
}

func TestMemoryStore_CheckPollAndOptionErrors(t *testing.T) {
    s := store.New()
    if err := s.CheckPollAndOption("missing", "opt"); err == nil {
        t.Fatalf("expected error for missing poll")
    }
    if err := s.CreatePoll("p", "q", false); err != nil {
        t.Fatalf("create poll: %v", err)
    }
    if err := s.CheckPollAndOption("p", "o"); err == nil {
        t.Fatalf("expected error for closed poll")
    }
    if err := s.UpdatePoll("p", "q2", true); err != nil {
        t.Fatalf("update poll: %v", err)
    }
    if err := s.CheckPollAndOption("p", "o"); err == nil {
        t.Fatalf("expected error for missing option")
    }
}

func TestMemoryStore_CRUDOptionsAndPolls(t *testing.T) {
    s := store.New()
    if err := s.CreatePoll("p", "q", true); err != nil {
        t.Fatal(err)
    }
    if err := s.AddOption("p", "o1", "A"); err != nil { t.Fatal(err) }
    if err := s.AddOption("p", "o2", "B"); err != nil { t.Fatal(err) }

    opts := s.ListOptions("p")
    if len(opts) != 2 { t.Fatalf("want 2 options, got %d", len(opts)) }

    if err := s.UpdateOption("o2", "B2"); err != nil { t.Fatal(err) }
    if opt, ok := s.GetOption("o2"); !ok || opt.Label != "B2" {
        t.Fatalf("option update not reflected: %+v ok=%v", opt, ok)
    }

    if err := s.DeleteOption("o1"); err != nil { t.Fatal(err) }
    if _, ok := s.GetOption("o1"); ok { t.Fatalf("o1 should be removed from index") }

    if err := s.DeletePoll("p"); err != nil { t.Fatal(err) }
    if _, ok := s.GetPollSnapshot("p"); ok { t.Fatalf("poll should be deleted") }
}
