use std::collections::HashMap;
use std::hash::Hash;
use std::sync::{Arc, Mutex};

#[derive(Clone)]
pub struct Cache<K, V> {
    data: Arc<Mutex<HashMap<K, V>>>,
}

impl<K, V> Cache<K, V>
where
    K: Eq + Hash,
{
    pub fn new() -> Self {
        Self {
            data: Arc::new(
                Mutex::new(HashMap::new())
            ),
        }
    }

    pub fn insert(&self, key: K, value: V) {
        let mut map = self.data.lock().unwrap();

        map.insert(key, value);
    }

    pub fn get(&self, key: &K) -> Option<V>
    where
        V: Clone,
    {
        let map = self.data.lock().unwrap();

        map.get(key).cloned()
    }

    pub fn len(&self) -> usize {
        let map = self.data.lock().unwrap();

        map.len()
    }
}
