Modules 1–5 Complete Revision Guide


MODULE 1: Linux Architecture & Filesystem

The Layered Architecture

User → Applications → Shell → Kernel → Hardware


Kernel: talks directly to hardware (CPU, RAM, disk). Manages processes, memory, files, devices.
Shell: translator between you and the kernel. Parses commands, asks kernel to execute them.
"Everything is a file": Linux's core philosophy — hardware devices, processes, network sockets are all represented as files under /dev, /proc, /sys. This is why Linux is so scriptable.


Filesystem Hierarchy

PathPurpose/root of everything/binessential user binaries (ls, cp, cat)/sbinessential system/admin binaries/etcconfiguration files/homepersonal directories for each user/roothome directory for root user/varvariable data — logs, caches (/var/log critical for debugging)/tmptemp files, cleared on reboot/usruser-installed programs/libraries/optoptional third-party software/procvirtual filesystem — LIVE info about running processes (not on disk!)/devdevice files/mnt, /mediaexternal drive mount points

Commands:

bashls /
ls /etc | head -20
ls /var/log
cat /proc/cpuinfo | head -10
cat /proc/meminfo | head -5
echo $$                        # current shell's PID
cat /proc/$$/status | head -5   # live info about current shell process

Why /proc works without being "real" files

The kernel tracks live system stats (RAM usage, CPU info, running processes) internally. Instead of forcing every tool to use a special API, Linux exposes this live data AS IF it were readable files. /proc is generated on-the-fly in memory, not stored on disk.

Absolute vs Relative Paths


Absolute: starts from /, works from anywhere. E.g. /home/ranjay/projects/app.py
Relative: starts from current location. E.g. projects/app.py


bashpwd            # current absolute path
cd /var/log     # absolute path
cd ..           # up one level (relative)
cd ../..        # up two levels
cd -            # jump to PREVIOUS directory (history-based, toggles back and forth)
cd ~            # jump to home
cd              # same as cd ~

Key distinction:


cd .. → tree-based, always "up one level from where you currently are"
cd - → history-based, "back button" to wherever you were before your last cd


Quiz Recap (Module 1)


Q: Difference between /bin and /home?
A: /bin = essential system binaries; /home = personal directories for each user.
Q: From /home/ranjay/projects, where does cd ../.. land you?
A: /home — first .. goes to /home/ranjay, second .. goes to /home.
Q: Why can you cat a file in /proc if it's not on disk?
A: Kernel generates /proc content live in memory; "everything is a file" philosophy exposes live kernel data as readable files.
Q: cd - vs cd ..?
A: cd .. moves up the folder tree; cd - jumps back to your previous location regardless of tree position.



MODULE 2: Navigation & File Operations

Deeper Navigation

bashls -lh      # long listing, human-readable sizes
ls -lt      # sort by modified time, newest first
ls -lS      # sort by size, largest first
ls -R       # recursive listing
tree        # visual folder tree (sudo apt install tree)
tree /etc -L 1   # limit depth to 1 level

File Operations

bashcp file.txt backup/        # copy file into folder
cp -r folder1/ folder2/     # copy folder recursively (REQUIRED for directories)
cp -v file.txt backup/      # verbose
cp -i file.txt backup/      # interactive (confirm before overwrite)
mv -i file.txt backup/      # move with overwrite confirmation
rm -i file.txt              # confirm before delete
rm -rf folder/              # force recursive delete — DANGEROUS, no undo
touch -t 202501010000 file.txt   # set specific timestamp

Key insight: -r (recursive) makes NO difference on a single file — cp file.txt backup/ and cp -r file.txt backup/ do the exact same thing. It only matters for directories, since a directory contains other files/folders that need to be copied recursively.

Safety habit for rm -rf: Always echo a variable path before deleting:

bashtarget="/home/ranjay/old_logs"
echo "rm -rf $target"    # sanity check first
rm -rf "$target"          # only after confirming

file and stat

bashfile document.pdf     # tells actual file type
stat file.txt          # detailed metadata: size, permissions, timestamps, inode

Wildcards (Globbing)

bashls *.txt          # all .txt files
ls file?.txt      # exactly one character wildcard (file1.txt, file2.txt)
ls [abc]*.txt     # files starting with a, b, or c
rm *.log          # delete all .log files
cp *.jpg photos/  # copy all jpgs

Common mistake caught: cp *.txt text_backups/ fails with "No such file or directory" if text_backups/ doesn't exist in your CURRENT directory — always verify folder structure with pwd/ls before running wildcard commands. Also /text_backups (with leading slash) means root-level folder, completely different from relative text_backups/.

Quiz Recap (Module 2)


Q: cp file.txt backup/ vs cp -r file.txt backup/ (file.txt is a real file)?
A: Identical behavior — -r only matters for directories, which may contain multiple files needing recursive copying.
Q: Why is rm -rf dangerous, and what protects you?
A: -r deletes recursively, -f = force, no confirmation, no undo. Protective habit: echo the path before deleting, especially when using variables.
Q: ? vs * wildcard?
A: ? matches exactly one character; * matches any number of characters (including zero).



MODULE 3: Permissions & Ownership

Reading a Permission String

ls -l
-rw-r--r--  1 ranjay ranjay  0 Jul  5 16:30 data.csv

-     rw-      r--      r--
Type  Owner    Group    Others


Position 1: - = file, d = directory, l = symlink
Each triplet = r w x (read, write, execute); - = permission absent


Numeric (Octal) Permissions

r = 4, w = 2, x = 1
7 = rwx (4+2+1)
6 = rw- (4+2)
5 = r-x (4+1)
4 = r-- (4)
0 = ---

bashchmod 755 script.sh      # owner: rwx, group/others: r-x (typical for scripts)
chmod 644 file.txt       # owner: rw-, group/others: r-- (standard safe default)
chmod 600 secret.key     # owner only — typical for SSH keys/passwords
chmod 000 file.txt       # nobody can do anything

Symbolic Mode

bashchmod u+x script.sh     # add execute for owner only
chmod g-w file.txt      # remove write for group
chmod o+r file.txt      # add read for others
chmod a+x script.sh     # add execute for ALL (user+group+others)
chmod u=rwx,g=rx,o=r file.txt   # set exact permissions

Critical distinction proven via real bug: chmod +x file (no letter specified) = adds execute for ALL THREE categories (owner+group+others), same as a+x. chmod u+x file only touches the OWNER's bit — it does NOT reset group/others. chmod is always additive/subtractive on CURRENT state, never a full reset (unless using =).

x on Directories ≠ "Execute"

A directory isn't a program — nothing to "run." Instead:


r on directory = can list contents (ls works)
w on directory = can create/delete/rename files inside
x on directory = can enter/traverse it (cd into it, or access files by path)


Proof: Even with r-- (read only, no x) on a folder, you can ls it but NOT cd into it, and you CANNOT cat a file inside it either — because reaching that file requires traversal permission (x) on its parent folder, regardless of the file's own permissions.

bashmkdir demo
chmod 600 demo         # owner: rw- (no x)
ls demo                # WORKS — have r
cd demo                 # Permission denied — no x
cat demo/inside.txt      # Permission denied too — x required to traverse

Ownership

bashsudo chown ranjay file.txt              # change owner
sudo chown ranjay:developers file.txt   # change owner AND group
sudo chgrp developers file.txt          # change only group
sudo chown -R ranjay:ranjay myfolder/   # recursive, applies to folder + contents

umask

New files start at base 666, folders at 777. Default umask (usually 022) is SUBTRACTED from base:


Files: 666 - 022 = 644
Folders: 777 - 022 = 755


bashumask              # view current umask
touch newfile.txt
ls -l newfile.txt  # confirms 644 default

Quiz Recap (Module 3)


Q: What does chmod 640 file.txt set?
A: Owner = rw- (6), Group = r-- (4), Others = --- (0, no permission).
Q: Why doesn't x on a directory mean "execute" like on a file?
A: A directory isn't executable code. x on a directory means permission to traverse/enter it (cd into it, access files inside by path) — not "run" it.
Q: If umask is 022, why do files come out as 644 instead of 666?
A: Because umask is subtracted from the base permission (666 for files). 666 - 022 = 644.



MODULE 4: Users & Groups

Key System Files

bashcat /etc/passwd       # every user account
cat /etc/shadow        # encrypted passwords (needs sudo)
cat /etc/group         # every group + members

/etc/passwd line format (colon-separated):

ranjay:x:1000:1000:,,,:/home/ranjay:/bin/bash
username : password(x=in shadow) : UID : GID : comment : home_dir : default_shell

Creating/Managing Users

bashsudo useradd -m devuser              # -m creates home directory
sudo passwd devuser                   # set password
sudo useradd -m -s /bin/bash -G sudo devuser   # add to sudo group at creation
sudo usermod -aG docker ranjay        # ADD to group (-a = append, critical!)
sudo userdel devuser                  # delete user, keep home dir
sudo userdel -r devuser               # delete user AND home dir

Critical trap (real-world incident pattern): usermod -G docker ranjay WITHOUT -a REPLACES all of ranjay's group memberships with just docker — silently kicking him out of sudo, and every other group he was in. ALWAYS use -aG, never bare -G, when adding to a group.

Groups

bashsudo groupadd developers
sudo gpasswd -a ranjay developers     # add user to group
sudo gpasswd -d ranjay developers     # remove user from group
groups ranjay                          # show all groups for a user
id ranjay                              # UID, GID, all group memberships

sudo

bashsudo -l                # list what you're allowed to run as sudo
visudo                 # SAFELY edit /etc/sudoers (never edit directly with vim)

Sudoers line: ranjay ALL=(ALL:ALL) ALL = user ranjay, on ALL hosts, can run as ALL users/groups, ALL commands.

Switching Users

bashsu devuser              # switch identity, KEEPS current shell's environment
su - devuser             # switch AND load devuser's own environment/home (full login simulation)
sudo -i                  # become root with root's environment
sudo -u devuser command  # run ONE command as another user, without switching

Quiz Recap (Module 4)


Q: Why is usermod -aG safer than usermod -G?
A: -a (append) adds the new group while keeping all existing group memberships. Without -a, the user's groups get REPLACED entirely — they could silently lose access to sudo, docker, etc.
Q: su devuser vs su - devuser?
A: su devuser switches user but keeps your current shell's PATH/env/directory. su - devuser fully simulates a real login — loads devuser's own .bashrc, environment, and home directory.
Q: What does UID tell you, and why do regular users start at 1000?
A: UID is the kernel's actual numeric identity for a user (username is just a label). UID 0 = root. UIDs 1-999 reserved for system/service accounts (like www-data, sshd). Regular human users start at 1000 by convention (Debian/Ubuntu) to cleanly separate system internals from real people — this is exactly why awk -F: '$3 >= 1000' works to filter real users.


Real debugging case: nobody (UID 65534) can slip into a >= 1000 filter since 65534 satisfies that condition — but it's a special low-privilege system account, not a real user. Better filter: $3 >= 1000 && $3 < 65534.


MODULE 5: Text Processing — grep, cut, sort/uniq, awk, sed

(Deep-dive version — rebuilt from scratch for full understanding)

grep — Pattern Searching

Mental model: grep reads line by line, checks if each line contains your pattern, prints the WHOLE line if yes, skips if no. Think of it as "a highlighter that deletes everything it didn't highlight."

bashgrep "pattern" filename        # basic search
grep -i "error" app.log         # case-INsensitive (ERROR, error, Error all match)
grep -v "error" app.log         # INVERT — show lines that DON'T match
grep -c "error" app.log         # COUNT of matching LINES (not occurrences!)
grep -n "error" app.log         # show line numbers
grep -r "TODO" ~/company        # recursive search through folder
grep -l "error" *.log           # show only FILENAMES with a match
grep -E "error|fail|critical" app.log   # extended regex, multiple patterns

Key facts proven through testing:


grep is case-sensitive by default — searching "error" will NOT find "ERROR" unless you use -i. A surprising "0 results" is often just a case-sensitivity issue, not a broken command.
grep -c counts matching LINES, not total word occurrences. A line with "ERROR ERROR" twice still counts as 1 line.
Bug caught: Pressing Enter INSIDE an unclosed quote (e.g., grep -c "error<Enter>" file) makes bash wait for more input — it silently searches for "error\n" (with a literal newline), which never matches anything in a normal file. Always keep the full command on one line.
False positives: grep "ERROR" matches the word ERROR ANYWHERE in a line — including inside a message like "INFO User reported ERROR in dashboard" — even though that's actually an INFO-level line, not a real error. Grep has no concept of "columns" or "structure."


cut — Extracting Columns

Mental model: Like a paper cutter — you specify the delimiter (where to cut) and which resulting slice to keep.

bashcut -d: -f1 /etc/passwd          # -d: sets delimiter to colon, -f1 = field 1
cut -d: -f1,3 /etc/passwd        # multiple fields (username + UID)
cut -d' ' -f2 file.txt            # space-delimited, field 2

Important limitation: cut doesn't understand MEANING, only POSITION. It also breaks on inconsistent spacing — multiple/repeated spaces create false "empty fields." This is one reason awk is often preferred for messy real-world text.

sort and uniq — Deduplication & Counting

Critical mechanic: uniq ONLY compares each line to the line DIRECTLY ABOVE it — it has no memory of anything further back. This is why you must sort FIRST.

bashsort file.txt                # alphabetical
sort -n numbers.txt           # numeric sort
sort -r file.txt              # reverse
sort file.txt | uniq          # remove ADJACENT duplicates only
sort file.txt | uniq -c       # count occurrences of each unique line

Proven experiment:

bashprintf "apple\nbanana\napple\n" | uniq          # apple NOT removed (not adjacent)
printf "apple\nbanana\napple\n" | sort | uniq   # apple correctly deduplicated

Real pipeline pattern (used in capstone project):

bashcut -d' ' -f3 server.log | sort | uniq -c

Step by step: cut extracts the log-level column from every line → sort groups identical values together → uniq -c collapses duplicates and counts occurrences of each. This exact extract | sort | uniq -c pattern is extremely common in real log analysis.

awk — Column Processing With Logic

Why awk beats cut: awk is a mini programming language — it can filter, do math, and reformat, not just slice by position. It also handles multiple/repeated whitespace correctly (no false empty fields like cut has).

bashawk '{print $1}' file.txt              # print column 1 (default delimiter = whitespace)
awk -F: '{print $1}' /etc/passwd        # -F sets delimiter to colon
awk '$3 == "ERROR" {print $0}' file.txt   # CONDITION: if column 3 equals ERROR, print whole line
awk '{print NR, $0}' file.txt           # NR = auto-incrementing line number
awk '$3 >= 1000 {print $1, $3}' /etc/passwd   # numeric comparison on a column
awk '$3 == "ERROR" {count++} END {print "Total:", count}' file.txt   # counting with a variable

Key concepts:


$1, $2, $3... = column references; $0 = entire line
NR = built-in line-number counter
Main {} block runs ONCE PER LINE; END {} block runs ONCE, after ALL lines are processed — perfect for printing final totals
count++ = increment a variable, just like a real programming language


PROVEN: awk beats grep for structured data (real test performed):

bash# Log line: "INFO User reported ERROR in dashboard UI"
grep "ERROR" server.log | wc -l              # → 4 (FALSE POSITIVE — caught the word inside a message)
awk '$3 == "ERROR" {print $0}' server.log | wc -l   # → 3 (CORRECT — only checks the actual log-level column)

This proves: grep does blind text matching across the WHOLE line (fooled by a word appearing in a message). awk checks a SPECIFIC column, so it correctly ignores an unrelated mention of "ERROR" inside free text.

sed — Find and Replace

bashsed 's/error/ERROR/' file.txt           # replace FIRST match per line only (default!)
sed 's/error/ERROR/g' file.txt          # replace ALL matches on the line (global flag)
sed -i 's/error/ERROR/g' file.txt       # edit file IN PLACE — permanent, no undo
sed -n '5,10p' file.txt                  # print only lines 5-10
sed '/error/d' file.txt                  # delete lines matching pattern

Proven default behavior:

bashecho "ERROR ERROR: double failure" | sed 's/ERROR/CRITICAL/'
# Output: "CRITICAL ERROR: double failure"  ← only the FIRST match replaced
echo "ERROR ERROR: double failure" | sed 's/ERROR/CRITICAL/g'
# Output: "CRITICAL CRITICAL: double failure"  ← 'g' flag replaces BOTH

⚠️ Real mistake made and learned from: Running sed -i 's/ERROR/CRITICAL/g' directly on a structured log file — WITHOUT testing the safe (non -i) version first — silently corrupted a line that had "ERROR" as part of a MESSAGE, not the log-level field:

Before: "INFO User reported ERROR in dashboard UI"
After:  "INFO User reported CRITICAL in dashboard UI"   ← WRONG, this was never a real CRITICAL event

Why this happens: sed does blind text replacement — it can't distinguish "ERROR as a structured field" from "ERROR as a word inside a sentence." Once -i runs, there's no undo — the original file is gone.

The safer fix — use awk instead, since it's column-aware:

bashawk '{if ($3 == "ERROR") $3 = "CRITICAL"; print}' server.log

This only touches column 3 (the actual log-level field), leaving any mention of "ERROR" inside message text completely untouched.

Golden habit: ALWAYS test sed WITHOUT -i first (see the output on screen), and only add -i once you've visually confirmed the transformation is exactly right. For structured data (logs, CSVs), prefer awk over sed since it understands columns, not just raw text.

Understanding !~ (regex NOT-match) — from the permission_check.sh script

bashls -l | awk '$1 !~ /^-rw-r--r--/ {print $0}'


~ = "matches this regex pattern"
!~ = "does NOT match this regex pattern" (negation)
/^-rw-r--r--/ = regex where ^ anchors to START of string, followed by the literal 644 permission string


Full translation: "If column 1 (permission string) does NOT start with -rw-r--r-- (i.e., isn't the safe 644 default), print the whole line." This flags any file with unusual permissions (overly permissive like 777, or unusually restrictive like 600/000).

Proven trace example: Does -rw-rw-r-- (664) match /^-rw-r--r--/?

Pattern:  -rw-r--r--
String:   -rw-rw-r--
          ✓✓✓✓✗  (diverges at character 5: pattern wants 'r', string has 'w')

They do NOT match at character 5 → !~ evaluates TRUE → this file GETS FLAGGED as unusual (correctly, since group-writable is a deviation from the safe default).

Quiz Recap (Module 5)


Q: Why must you sort before uniq?
A: uniq only compares each line to its IMMEDIATE neighbor above — it has no memory further back. Without sorting, duplicate lines separated by other content are missed entirely.
Q: What's the risk of sed -i without testing first?
A: It overwrites the file directly, immediately, with no confirmation and no backup. A slightly-wrong regex can silently corrupt data — especially dangerous on structured files where a word can appear both as a "field" and inside a "message" (proven via the ERROR-in-message bug above).
Q: What does -F: do in awk, and why is it needed for /etc/passwd?
A: -F: sets the field delimiter to colon. awk's default delimiter is whitespace, but /etc/passwd is colon-separated — without -F:, awk would treat each whole line as a single field.
Q: Why is awk safer/more accurate than grep or sed for structured log data?
A: grep and (default) sed do blind text matching across the entire line/string, which produces false positives/corruption when the search word also appears inside unrelated free-text content. awk can target a SPECIFIC column, so it only acts on the actual structured field, ignoring incidental mentions elsewhere in the line.
