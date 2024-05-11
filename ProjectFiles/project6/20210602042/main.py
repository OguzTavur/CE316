import sys

def main(args):
    # Your code here
    for arg in args:
        print(f"{arg}")

if __name__ == "__main__":
    main(sys.argv[1:])
