import os

print("Current Directory:", os.getcwd())
print("Files here:", os.listdir("."))
print("Does notes.txt exists?", os.path.exists("notes.txt"))
print("Does missing.txt exists?",os.path.exists("missing.txt"))

with open("notes.txt","w") as f:
    f.write("Learning file handling in Python\n")
    f.write("Week 5, Day2\n")

with open("notes.txt","r") as f:
    content = f.read()
    print(content)

with open("notes.txt","r") as f:
    lines = f.readlines()
    print(lines)
    for line in lines:
        print(f"Line : {line.strip()}")
