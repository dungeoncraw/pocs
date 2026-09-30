//! A hit-counter registry. Each registered label gets a `Handle` that can be
//! used to record hits cheaply without re-indexing into the slot table.

const LABEL_LEN: usize = 32;

struct Slot {
    label: [u8; LABEL_LEN],
    hits: u64,
}

pub struct Handle {
    id: usize,
    slot: *mut Slot,
}

pub struct Registry {
    slots: Vec<Slot>,
}

impl Registry {
    pub fn new() -> Self {
        Registry { slots: Vec::new() }
    }

    pub fn register(&mut self, label: &str) -> Handle {
        let mut buf = [0u8; LABEL_LEN];
        let bytes = label.as_bytes();
        let n = bytes.len().min(LABEL_LEN);
        buf[..n].copy_from_slice(&bytes[..n]);

        if self.slots.len() == self.slots.capacity() {
            // grow geometrically into a fresh buffer
            let mut bigger = Vec::with_capacity((self.slots.capacity() * 2).max(4));
            bigger.append(&mut self.slots);
            self.slots = bigger;
        }
        self.slots.push(Slot { label: buf, hits: 0 });
        let id = self.slots.len() - 1;
        let slot = &mut self.slots[id] as *mut Slot;
        Handle { id, slot }
    }

    pub fn hit(&mut self, handle: &Handle) {
        debug_assert!(handle.id < self.slots.len());
        unsafe {
            (*handle.slot).hits += 1;
        }
    }

    pub fn hits(&self, handle: &Handle) -> u64 {
        self.slots[handle.id].hits
    }

    pub fn total_hits(&self) -> u64 {
        self.slots.iter().map(|s| s.hits).sum()
    }

    pub fn label(&self, handle: &Handle) -> String {
        let raw = &self.slots[handle.id].label;
        let end = raw.iter().position(|&b| b == 0).unwrap_or(LABEL_LEN);
        String::from_utf8_lossy(&raw[..end]).into_owned()
    }
}

impl Default for Registry {
    fn default() -> Self {
        Self::new()
    }
}
