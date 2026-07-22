# Python File Handling + OS Module — Quick Reference

Built Week 6, Day 2. Covers file read/write/append, the `os` module, and
writing a basic config file parser with real error handling.

---

## File Modes

| Mode | Meaning | Behavior if file exists | Behavior if file missing |
|------|---------|--------------------------|----------------------------|
| `"r"` | Read | Opens for reading | Crashes (FileNotFoundError) |
| `"w"` | Write | **Erases everything**, then writes | Creates new file |
| `"a"` | Append | Adds to the end, never erases | Creates new file |

**Key lesson:** `"w"` truncates the file on EVERY open, even if you write
identical content. If a script opens a file in `"w"` mode inside a loop
that runs multiple times, only the LAST write survives — everything
before it is silently gone. Verified this by running a `"w"` block twice
in a row and seeing the file stay at the same 2 lines both times, then
switching to `"a"` and watching it grow by 1 line per run instead.

---

## Writing a File

```python
with open("notes.txt", "w") as f:
    f.write("Line one\n")
    f.write("Line two\n")
```

Always use `with` — it auto-closes the file even if an error happens
mid-write. Manually calling `f.close()` is easy to forget and can leave
a file in a half-written or locked state.

Don't forget `\n` — `write()` does NOT add a newline automatically,
unlike `print()`.

---

## Reading a File

```python
# Whole file as one string
with open("notes.txt", "r") as f:
    content = f.read()
    print(content)

# File as a list, one entry per line
with open("notes.txt", "r") as f:
    lines = f.readlines()
    for line in lines:
        print(line.strip())   # .strip() removes the trailing \n
```

`readlines()` keeps the `\n` character as part of each string — confirmed
by printing the raw list and seeing `['Line one\n', 'Line two\n']`.
Always `.strip()` before using the line's content for anything, or you'll
get unexpected blank lines / whitespace bugs.

---

## The `os` Module

```python
import os

os.getcwd()              # current working directory (string)
os.listdir(".")           # list of files/folders in given path
os.path.exists("x.txt")   # True/False — check before opening a file
                           # that might not exist, to avoid a crash
```

Always check `os.path.exists()` before opening a file you're not 100%
sure exists — opening a missing file in `"r"` mode crashes the script.

---

## Config File Parser (real pattern, with error handling)

```python
import os

config_file = "config.txt"

if os.path.exists(config_file):
    with open(config_file, "r") as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if "=" not in line or line == "":
                continue
            key, value = line.split("=", 1)
            print(f"{key} -> {value}")
else:
    print(f"{config_file} not found")
```

### Bugs hit while building this, and the actual fixes:

1. **`line.split("=")` without a limit crashes on lines with multiple `=`
   signs.**
   Example: `"note=this=has=extra=equals".split("=")` returns 5 pieces,
   but `key, value = ...` expects exactly 2 → `ValueError: too many
   values to unpack`.
   **Fix:** `line.split("=", 1)` — the `1` means "split only once," so
   everything after the first `=` becomes the value, no matter how many
   more `=` signs follow.

2. **Blank lines or lines with no `=` at all also crash it.**
   **Fix:** `if "=" not in line or line == "": continue` — skip that
   line entirely and move to the next one.

**General lesson:** never trust that input (a file, user input, an API
response) will always be clean. Real scripts check first, or handle the
failure gracefully, rather than assuming the happy path.

---

## Quick Gotchas Log

- Trailing `;` after Python statements is silently tolerated but not
  idiomatic — drop the habit from other languages.
- `int(input(...))` crashes on non-numeric input — no guard yet, revisit
  with try/except later.
- `"w"` mode inside a script that runs more than once (or a script you
  re-run for testing) will silently destroy prior content — think before
  reaching for `"w"` vs `"a"` in a real automation script.
