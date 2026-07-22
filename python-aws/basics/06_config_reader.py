import os

config_file = "config.txt"

if os.path.exists(config_file):
    with open(config_file,"r") as f:
        lines = f.readlines()
        for line in lines:
            line = line.strip()
            if "=" not in line or line =="":
                continue
            key,value = line.split("=",1)
            print(f"{key} -> {value}")
else:
    print(f"{config_file} not found")
