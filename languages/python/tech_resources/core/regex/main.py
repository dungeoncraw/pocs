import re
# The official internal replacements for sre_* modules
import re._parser as _parser
import re._compiler as _compiler
import re._constants as _constants

if __name__ == '__main__':
    # Advanced pattern with Lookaheads
    pattern = r"^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"

    # 1. Parse the pattern into an internal tree structure
    parsed = _parser.parse(pattern)

    # 2. Generate the raw bytecode using the new _compiler module
    # This is the list of integers that the C extension (_sre.c) loops through
    bytecode = _compiler._code(parsed, 0)

    # 3. Create the mapping using re._constants
    # We look for all uppercase attributes that are integers (the Opcodes)
    op_mapping = {v: k for k, v in vars(_constants).items()
                  if isinstance(v, int) and k.isupper()}

    print(f"Pattern: {pattern}")
    print("\n--- Raw Bytecode for C Engine (Modern API) ---")
    print(list(bytecode))

    print("\n--- Interpreting the Instructions ---")
    bytecode_list = list(bytecode)

    # Let's peek at the first 25 integers
    for i in range(min(len(bytecode_list), 25)):
        val = bytecode_list[i]
        name = op_mapping.get(val)

        if name:
            # If it's a known Opcode, it's a command for the C engine
            print(f"Index [{i:03}] -> Instruction: {name} ({val})")
        else:
            # If not, it's a parameter (like a jump offset or a character code)
            print(f"Index [{i:03}] ->   Data/Argument: {val}")

    print("\nThis bytecode is executed by the SRE_MATCH function in C.")