package api

import (
    "bytes"
    "encoding/json"
    "net/http"
    "net/http/httptest"
    "os"
    "testing"
)

func init() {
    _ = os.Setenv("STORE_BACKEND", "memory")
    _ = os.Unsetenv("REDIS_URL")
    _ = os.Unsetenv("REDIS_QUEUE_NAME")
}

func TestHandleVote_BadRequest(t *testing.T) {
    s := NewServer()
    t.Cleanup(func() { s.Close() })

    body, _ := json.Marshal(map[string]any{})
    req := httptest.NewRequest(http.MethodPost, "/vote", bytes.NewReader(body))
    req.Header.Set("Content-Type", "application/json")
    w := httptest.NewRecorder()

    s.handleVote(w, req)
    res := w.Result()
    defer res.Body.Close()
    if res.StatusCode != http.StatusBadRequest {
        t.Fatalf("got %d, want 400", res.StatusCode)
    }
}

func TestHandlePolls_CreateAndGet(t *testing.T) {
    s := NewServer()
    t.Cleanup(func() { s.Close() })

    payload := map[string]any{"id": "unit-poll", "question": "Q?", "is_open": true}
    b, _ := json.Marshal(payload)
    req := httptest.NewRequest(http.MethodPost, "/polls", bytes.NewReader(b))
    req.Header.Set("Content-Type", "application/json")
    w := httptest.NewRecorder()
    s.handlePolls(w, req)
    res := w.Result()
    if res.StatusCode != http.StatusCreated {
        t.Fatalf("create status=%d, want 201", res.StatusCode)
    }
    _ = res.Body.Close()

    req2 := httptest.NewRequest(http.MethodGet, "/polls?id=unit-poll", nil)
    w2 := httptest.NewRecorder()
    s.handlePolls(w2, req2)
    res2 := w2.Result()
    defer res2.Body.Close()
    if res2.StatusCode != http.StatusOK {
        t.Fatalf("get status=%d, want 200", res2.StatusCode)
    }
    var pr PollResponse
    if err := json.NewDecoder(res2.Body).Decode(&pr); err != nil {
        t.Fatalf("decode: %v", err)
    }
    if pr.ID != "unit-poll" || pr.Question == "" || !pr.IsOpen {
        t.Fatalf("unexpected payload: %+v", pr)
    }
}
