package api_test

import (
    "bytes"
    "encoding/json"
    "fmt"
    "net/http"
    "net/http/httptest"
    "os"
    "sync"
    "testing"
    "time"

   	api "github.com/thiagonasc/poll/internal/api"
)

func doJSON(t *testing.T, client *http.Client, method, url string, body any, want int) {
    t.Helper()
    var rdr *bytes.Reader
    if body != nil {
        b, err := json.Marshal(body)
        if err != nil {
            t.Fatalf("marshal body: %v", err)
        }
        rdr = bytes.NewReader(b)
    } else {
        rdr = bytes.NewReader(nil)
    }
    req, err := http.NewRequest(method, url, rdr)
    if err != nil {
        t.Fatalf("new request: %v", err)
    }
    if body != nil {
        req.Header.Set("Content-Type", "application/json")
    }
    resp, err := client.Do(req)
    if err != nil {
        t.Fatalf("do request: %v", err)
    }
    defer resp.Body.Close()
    if resp.StatusCode != want {
        t.Fatalf("%s %s => status %d, want %d", method, url, resp.StatusCode, want)
    }
}

func TestE2E_ConcurrentVotes(t *testing.T) {
    _ = os.Setenv("STORE_BACKEND", "memory")
    _ = os.Unsetenv("REDIS_URL")
    _ = os.Unsetenv("REDIS_QUEUE_NAME")

    http.DefaultServeMux = http.NewServeMux()

    srv := api.NewServer()
    t.Cleanup(func() { srv.Close() })
    srv.Routes()

    ts := httptest.NewServer(http.DefaultServeMux)
    defer ts.Close()
    client := ts.Client()

    pollID := "e2e-poll"
    optID := "opt-1"
    doJSON(t, client, http.MethodPost, ts.URL+"/polls", map[string]any{
        "id":       pollID,
        "question": "E2E test poll",
        "is_open":  true,
    }, http.StatusCreated)

    doJSON(t, client, http.MethodPost, ts.URL+"/options", map[string]any{
        "id":      optID,
        "poll_id": pollID,
        "label":   "Option 1",
    }, http.StatusCreated)

    voters := 500

    var wg sync.WaitGroup
    wg.Add(voters)
    for i := 0; i < voters; i++ {
        i := i
        go func() {
            defer wg.Done()
            doJSON(t, client, http.MethodPost, ts.URL+"/vote", map[string]any{
                "poll_id":   pollID,
                "option_id": optID,
                "voter_id":  fmt.Sprintf("v-%d", i),
            }, http.StatusAccepted)
        }()
    }
    wg.Wait()

    deadline := time.Now().Add(15 * time.Second)
    type pollResp struct {
        ID       string `json:"id"`
        Question string `json:"question"`
        IsOpen   bool   `json:"is_open"`
        Options  []struct {
            ID    string `json:"id"`
            Label string `json:"label"`
            Votes int    `json:"votes"`
        } `json:"options"`
        Voters []string `json:"voters"`
    }
    for {
        if time.Now().After(deadline) {
            t.Fatalf("timeout waiting for %d votes to be applied", voters)
        }
        resp, err := client.Get(ts.URL + "/poll?id=" + pollID)
        if err != nil {
            t.Fatalf("get poll: %v", err)
        }
        if resp.StatusCode != http.StatusOK {
            _ = resp.Body.Close()
            t.Fatalf("GET /poll => %d", resp.StatusCode)
        }
        var pr pollResp
        if err := json.NewDecoder(resp.Body).Decode(&pr); err != nil {
            _ = resp.Body.Close()
            t.Fatalf("decode poll response: %v", err)
        }
        _ = resp.Body.Close()

        totalVotes := 0
        var optVotes int
        for _, o := range pr.Options {
            totalVotes += o.Votes
            if o.ID == optID {
                optVotes = o.Votes
            }
        }
        if totalVotes == voters && optVotes == voters && len(pr.Voters) == voters {
            break
        }
        time.Sleep(10 * time.Millisecond)
    }
}
