import random
import time
import sys

if __name__ == "__main__":

    if len(sys.argv) != 3:
        print("error: pass only two arguments: seed and filename")
        exit()

    hex_digits = '0123456789abcdef'
    random.seed(int(sys.argv[1]))
    f = open(sys.argv[2], 'w')

    for _ in range(4096):
        lst = [random.choice(hex_digits) for n in range(64)]
        string = "".join(lst)
        f.write(string + '\n')

    f.close()
