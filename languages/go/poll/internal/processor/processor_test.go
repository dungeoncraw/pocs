package processor

import (
    "os"
    "strconv"
    "testing"
    "time"

    "github.com/thiagonasc/poll/internal/models"
    "github.com/thiagonasc/poll/internal/store"
)

func TestProcessor_InMemory_EnqueueAndApply(t *testing.T) {
    _ = os.Unsetenv("REDIS_URL")
    _ = os.Unsetenv("REDIS_QUEUE_NAME")

    ms := store.New()
    if err := ms.CreatePoll("p", "q", true); err != nil {
        t.Fatalf("create poll: %v", err)
    }
    if err := ms.AddOption("p", "o", "opt"); err != nil {
        t.Fatalf("add option: %v", err)
    }

    p := New(ms, 1000, 4)
    t.Cleanup(func() { p.Close() })

    total := 100
    for i := 0; i < total; i++ {
        v := models.VoteRequest{PollID: "p", OptionID: "o", VoterID: "u" + strconv.Itoa(i)}
        if ok := p.Enqueue(v); !ok {
            t.Fatalf("enqueue failed at %d", i)
        }
    }

    deadline := time.Now().Add(2 * time.Second)
    for {
        if time.Now().After(deadline) {
            t.Fatalf("timeout waiting for %d votes applied", total)
        }
        snap, ok := ms.GetPollSnapshot("p")
        if ok && len(snap.Options) == 1 && snap.Options[0].Votes == total && len(snap.Voters) == total {
            break
        }
        time.Sleep(10 * time.Millisecond)
    }
}
