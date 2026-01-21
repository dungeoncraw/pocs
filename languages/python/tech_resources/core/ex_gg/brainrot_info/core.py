from enum import IntEnum
from typing import Optional
import json
from pathlib import Path

class Rarity(IntEnum):
    COMMON = 1
    RARE = 2
    EPIC = 3
    LEGENDARY = 4
    MYTHIC = 5
    BRAINROT_GOD = 6
    SECRET = 7
    OG = 8
    ADMIN = 9

class Brainrot:
    def __init__(self, name: str, rarity: Rarity):
        self.name = name
        self.rarity = rarity

    def is_worth_buying(self, threshold: Rarity = Rarity.RARE) -> bool:
        """
        Returns True if the brainrot's rarity is greater than or equal to the threshold.
        The default threshold is RARE.
        """
        return self.rarity >= threshold

    def __repr__(self):
        return f"Brainrot(name='{self.name}', rarity={self.rarity.name})"

def _load_brainrots():
    data_file = Path(__file__).parent / "brainrots.json"
    if not data_file.exists():
        return {}
    
    with open(data_file, "r") as f:
        data = json.load(f)
    
    return {
        key: Brainrot(item["name"], Rarity[item["rarity"]])
        for key, item in data.items()
    }

BRAINROTS = _load_brainrots()

def get_brainrot_info(name: str) -> Optional[Brainrot]:
    return BRAINROTS.get(name.lower())
