#include <iostream>

int main(int argc, char* argv[]) {
    for (int i = 1; i < argc; ++i) {
        std::cout << "a"<< argv[i] << std::endl;
    }
    return 0;
}