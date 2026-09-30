use challenge_22_broken_hash_eq::*;
use std::collections::hash_map::DefaultHasher;
use std::collections::{HashMap, HashSet};
use std::hash::{Hash, Hasher};

fn hash_of(h: &Handle) -> u64 {
    let mut s = DefaultHasher::new();
    h.hash(&mut s);
    s.finish()
}

#[test]
fn equality_ignores_case() {
    assert_eq!(Handle::new("Rusty"), Handle::new("rUSTY"));
    assert_ne!(Handle::new("rusty"), Handle::new("rusty2"));
}

#[test]
fn equal_handles_have_equal_hashes() {
    assert_eq!(hash_of(&Handle::new("Ferris")), hash_of(&Handle::new("FERRIS")));
    assert_eq!(hash_of(&Handle::new("crab")), hash_of(&Handle::new("Crab")));
}

#[test]
fn set_treats_case_variants_as_one() {
    let mut set = HashSet::new();
    for i in 0..40 {
        set.insert(Handle::new(&format!("user{i}")));
    }
    for i in 0..40 {
        assert!(set.contains(&Handle::new(&format!("USER{i}"))), "USER{i} missing");
        assert!(!set.insert(Handle::new(&format!("User{i}"))));
    }
    assert_eq!(set.len(), 40);
}

#[test]
fn map_lookup_is_case_insensitive() {
    let mut followers: HashMap<Handle, u32> = HashMap::new();
    for i in 0..30 {
        followers.insert(Handle::new(&format!("acct{i}")), i);
    }
    for i in 0..30 {
        assert_eq!(followers.get(&Handle::new(&format!("ACCT{i}"))), Some(&i));
    }
}

#[test]
fn dedup_keeps_first_spelling_and_order() {
    let raw = ["Zed", "amy", "ZED", "Amy", "bob", "AMY", "Bob"];
    let out = dedup_handles(&raw);
    let names: Vec<&str> = out.iter().map(|h| h.as_str()).collect();
    assert_eq!(names, vec!["Zed", "amy", "bob"]);
}
