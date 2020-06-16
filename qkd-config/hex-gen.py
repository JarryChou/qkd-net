import random
import time
import sys

if __name__ == "__main__":

    if len(sys.argv) != 2:
        print("error: pass only file name as one command line argument to hex-gen.py")
        exit()

    hex_digits = '0123456789abcdef'
    random.seed(time.time())
    f = open(sys.argv[1], 'w')

    for _ in range(4096):
        lst = [random.choice(hex_digits) for n in range(64)]
        string = "".join(lst)
        f.write(string + '\n')

    f.close()
