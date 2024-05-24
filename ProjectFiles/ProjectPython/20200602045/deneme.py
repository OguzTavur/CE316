import sys

def main(arg1, arg2):
    print(f"You are first {arg1}")
    print(f"You are second {arg2}")

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])

